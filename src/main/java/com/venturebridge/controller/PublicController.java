package com.venturebridge.controller;

import com.venturebridge.entity.Role;
import com.venturebridge.entity.User;
import com.venturebridge.entity.VerificationStatus;
import com.venturebridge.entity.InterestStatus;
import com.venturebridge.repository.UserRepository;
import com.venturebridge.repository.StartupRepository;
import com.venturebridge.repository.InterestRepository;
import com.venturebridge.util.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PublicController {

    private final CurrentUserUtil currentUserUtil;
    private final UserRepository userRepository;
    private final StartupRepository startupRepository;
    private final InterestRepository interestRepository;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("totalStartups", startupRepository.count());
        model.addAttribute("totalInvestors", userRepository.countByRole(Role.ROLE_INVESTOR));
        model.addAttribute("totalFounders", userRepository.countByRole(Role.ROLE_FOUNDER));
        model.addAttribute("totalVerifiedStartups", startupRepository.countByVerificationStatus(VerificationStatus.VERIFIED));
        model.addAttribute("totalActiveConnections", interestRepository.countByStatus(InterestStatus.ACCEPTED));
        
        java.util.List<com.venturebridge.entity.Startup> featured = startupRepository.findAllWithFounder().stream()
                .filter(s -> s.isActive() && s.getVerificationStatus() == VerificationStatus.VERIFIED)
                .limit(3)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("featuredStartups", featured);

        return "public/home";
    }

    @GetMapping("/about")
    public String about() {
        return "public/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "public/contact";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        User currentUser = currentUserUtil.getCurrentUser();
        Role role = currentUser.getRole();
        if (role == Role.ROLE_ADMIN) {
            return "redirect:/admin/dashboard";
        }
        if (role == Role.ROLE_FOUNDER) {
            return "redirect:/founder/dashboard";
        }
        return "redirect:/investor/dashboard";
    }
}