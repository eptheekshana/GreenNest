package com.horizonix.nboard.service;

import com.horizonix.nboard.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailVerificationService {

	private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

	@Value("${resend.api-key:}")
	private String resendApiKey;

	@Value("${resend.from-email:noreply@nboard.com}")
	private String fromEmail;

	@Value("${resend.from-name:Nboard}")
	private String fromName;

	@Value("${app.base-url:http://localhost:8080}")
	private String appBaseUrl;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Send verification email via Resend. Returns the verification URL so callers
	 * can use it for testing/fallback. If Resend is not configured the method
	 * will log the verification URL and return it (no exception) so registration
	 * can continue in environments where Resend is unavailable.
	 */
	public String sendVerificationEmail(User user, String token, String baseUrl) {
		String verificationUrl = buildVerificationUrl(token, baseUrl);

		if (resendApiKey == null || resendApiKey.isBlank()) {
			// Fallback: log and return the verification URL instead of failing
			logger.warn("Resend API key is not configured. Verification URL for {}: {}", user.getEmail(), verificationUrl);
			return verificationUrl;
		}

		if (fromEmail == null || fromEmail.isBlank()) {
			// Fallback: log and return the verification URL instead of failing
			logger.warn("Resend sender email is not configured. Verification URL for {}: {}", user.getEmail(), verificationUrl);
			return verificationUrl;
		}

		try {
			// Prepare email content
			String htmlBody = "<p>Hello " + user.getFullName() + ",</p>"
					+ "<p>Please verify your email address by clicking the link below:</p>"
					+ "<p><a href=\"" + verificationUrl + "\" style=\"background-color: #2E7D4F; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; display: inline-block;\">Verify my email</a></p>"
					+ "<p>This link will expire in 24 hours.</p>";

			String textBody = "Hello " + user.getFullName() + ",\n\n"
					+ "Please verify your email address by clicking the link below:\n\n"
					+ verificationUrl + "\n\n"
					+ "This link will expire in 24 hours.";

			// Build request body
			Map<String, Object> emailRequest = new HashMap<>();
			emailRequest.put("from", fromName + " <" + fromEmail + ">");
			emailRequest.put("to", user.getEmail());
			emailRequest.put("subject", "Verify your Nboard account");
			emailRequest.put("html", htmlBody);
			emailRequest.put("text", textBody);

			// Set up headers
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + resendApiKey);
			headers.set("Content-Type", "application/json");

			// Make the request to Resend API
			HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailRequest, headers);
			ResponseEntity<Map> response = restTemplate.postForEntity(
					"https://api.resend.com/emails",
					request,
					Map.class
			);

			if (response.getStatusCode() == HttpStatus.OK) {
				logger.info("Verification email sent to {}", user.getEmail());
				return verificationUrl;
			} else {
				logger.warn("Resend returned status {} for {}. Response: {}", response.getStatusCode(), user.getEmail(), response.getBody());
				throw new IllegalStateException("Resend rejected the verification email (status " + response.getStatusCode() + "). Check the API key and sender email configuration.");
			}
		} catch (IllegalStateException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error("Error sending verification email via Resend: {}", ex.getMessage(), ex);
			throw new IllegalStateException("Failed to send verification email via Resend. Error: " + ex.getMessage(), ex);
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

