package com.horizonix.nboard.controller;

import com.horizonix.nboard.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OtpController {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @GetMapping("/verify-email")
    public String showVerifyEmailPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "verify-email";
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam("email") String email, @RequestParam("otp") String otp, RedirectAttributes redirectAttributes) {
        if (emailVerificationService.verifyOtp(email, otp)) {
            redirectAttributes.addFlashAttribute("message", "Email verified successfully. You can now log in.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired OTP. Please try again.");
            return "redirect:/verify-email?email=" + email;
        }
    }

    @PostMapping("/verify-email/resend")
    public String resendOtp(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            emailVerificationService.resendOtp(email);
            redirectAttributes.addFlashAttribute("message", "A new OTP was sent to your email.");
            return "redirect:/verify-email?email=" + email + "&resent=true";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/verify-email?email=" + email;
        }
    }
}

