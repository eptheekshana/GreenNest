package com.horizonix.nboard.service;

import com.horizonix.nboard.entity.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
public class EmailVerificationService {

	private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

	@Value("${sendgrid.api-key:}")
	private String sendGridApiKey;

	@Value("${sendgrid.from-email:}")
	private String fromEmail;

	@Value("${sendgrid.from-name:Nboard}")
	private String fromName;

	@Value("${sendgrid.data-residency:eu}")
	private String dataResidency;

	@Value("${app.base-url:http://localhost:8080}")
	private String appBaseUrl;

	/**
	 * Send verification email via SendGrid. Returns the verification URL so callers
	 * can use it for testing/fallback. If SendGrid is not configured the method
	 * will log the verification URL and return it (no exception) so registration
	 * can continue in environments where SendGrid is unavailable.
	 */
	public String sendVerificationEmail(User user, String token, String baseUrl) {
		String verificationUrl = buildVerificationUrl(token, baseUrl);

		if (sendGridApiKey == null || sendGridApiKey.isBlank()) {
			// Fallback: log and return the verification URL instead of failing
			logger.warn("SendGrid API key is not configured. Verification URL for {}: {}", user.getEmail(), verificationUrl);
			return verificationUrl;
		}

		if (fromEmail == null || fromEmail.isBlank()) {
			// Fallback: log and return the verification URL instead of failing
			logger.warn("SendGrid sender email is not configured. Verification URL for {}: {}", user.getEmail(), verificationUrl);
			return verificationUrl;
		}

		try {
			SendGrid sendGrid = new SendGrid(sendGridApiKey);
			sendGrid.setDataResidency(dataResidency == null || dataResidency.isBlank() ? "eu" : dataResidency.trim());

			Mail mail = new Mail();
			mail.setFrom(new Email(fromEmail, fromName));
			mail.setSubject("Verify your Nboard account");

			Personalization personalization = new Personalization();
			personalization.addTo(new Email(user.getEmail(), user.getFullName()));
			mail.addPersonalization(personalization);

			String textBody = "Hello " + user.getFullName() + ",\n\n"
					+ "Please verify your email address by clicking the link below:\n\n"
					+ verificationUrl + "\n\n"
					+ "This link will expire in 24 hours.";
			mail.addContent(new Content("text/plain", textBody));
			mail.addContent(new Content("text/html", "<p>Hello " + user.getFullName() + ",</p>"
					+ "<p>Please verify your email address by clicking the link below:</p>"
					+ "<p><a href=\"" + verificationUrl + "\">Verify my email</a></p>"
					+ "<p>This link will expire in 24 hours.</p>"));

			Request request = new Request();
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());

			Response response = sendGrid.api(request);
			if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
				logger.info("Verification email sent to {}", user.getEmail());
				return verificationUrl;
			} else {
				String body = response.getBody();
				logger.warn("SendGrid returned status {} for {}. Response body: {}", response.getStatusCode(), user.getEmail(), body);
				throw new IllegalStateException("SendGrid rejected the verification email (status " + response.getStatusCode() + "). Check the verified sender, API key, and SendGrid response body in the logs.");
			}
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to send verification email via SendGrid. Check network access and API key configuration.", ex);
		}
	}

	public String buildVerificationUrl(String token, String baseUrl) {
		String resolvedBaseUrl = (baseUrl == null || baseUrl.isBlank()) ? appBaseUrl : baseUrl;

		return UriComponentsBuilder.fromHttpUrl(resolvedBaseUrl)
				.path("/verify-email")
				.queryParam("token", token)
				.toUriString();
	}
}

