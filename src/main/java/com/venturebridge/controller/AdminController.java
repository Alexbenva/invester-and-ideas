package com.venturebridge.controller;

import com.venturebridge.service.AdminService;
import com.venturebridge.service.DashboardService;
import com.venturebridge.service.StartupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final DashboardService dashboardService;
    private final StartupService startupService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardService.buildAdminStats());
        model.addAttribute("users", adminService.findAllUsers());
        model.addAttribute("startups", startupService.findAllStartups());
        return "dashboard/admin-dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", adminService.findAllUsers());
        model.addAttribute("founders", adminService.findFounders());
        model.addAttribute("investors", adminService.findInvestors());
        return "admin/users";
    }

    @GetMapping("/startups")
    public String startups(Model model) {
        model.addAttribute("startups", startupService.findAllStartups());
        return "admin/startups";
    }

    @PostMapping("/startups/{id}/verify")
    public String verifyStartup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.verifyStartup(id);
        redirectAttributes.addFlashAttribute("successMessage", "Startup verified successfully.");
        return "redirect:/admin/startups";
    }

    @PostMapping("/startups/{id}/spam")
    public String markSpam(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.markStartupAsSpam(id);
        redirectAttributes.addFlashAttribute("successMessage", "Startup marked as spam.");
        return "redirect:/admin/startups";
    }

    @PostMapping("/startups/{id}/delete")
    public String deleteStartup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteStartup(id);
        redirectAttributes.addFlashAttribute("successMessage", "Startup deleted successfully.");
        return "redirect:/admin/startups";
    }
}