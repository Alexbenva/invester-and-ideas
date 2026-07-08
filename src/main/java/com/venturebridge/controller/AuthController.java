package com.venturebridge.controller;

import com.venturebridge.dto.ChangePasswordRequest;
import com.venturebridge.dto.ForgotPasswordRequest;
import com.venturebridge.dto.RegisterRequest;
import com.venturebridge.service.AuthService;
import com.venturebridge.util.CurrentUserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.venturebridge.exception.DuplicateResourceException;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final AuthService authService;
    private final CurrentUserUtil currentUserUtil;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            authService.register(request);
        } catch (DuplicateResourceException exception) {
            bindingResult.rejectValue("email", "duplicate.email", exception.getMessage());
            return "auth/register";
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("confirmPassword", "mismatch.password", exception.getMessage());
            return "auth/register";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Account created successfully. Please login.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new com.venturebridge.dto.LoginRequest());
        return "auth/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }
        String token = authService.requestPasswordReset(request);
        redirectAttributes.addFlashAttribute("successMessage", "Reset token generated: " + token);
        return "redirect:/login";
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "auth/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/change-password";
        }
        authService.changePassword(currentUserUtil.getCurrentUser().getEmail(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
        return "redirect:/dashboard";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam(required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            authService.resetPassword(token, newPassword, confirmPassword);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("token", token);
            return "auth/reset-password";
        } catch (com.venturebridge.exception.ResourceNotFoundException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully. Please login.");
        return "redirect:/login";
    }
}