package com.venturebridge.controller;

import com.venturebridge.dto.StartupRequest;
import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import com.venturebridge.service.DashboardService;
import com.venturebridge.service.StartupService;
import com.venturebridge.util.CurrentUserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.venturebridge.service.InterestService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/founder")
public class FounderController {

    private final StartupService startupService;
    private final DashboardService dashboardService;
    private final CurrentUserUtil currentUserUtil;
    private final InterestService interestService;
    private final com.venturebridge.service.NotificationService notificationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User currentUser = currentUserUtil.getCurrentUser();
        model.addAttribute("stats", dashboardService.buildFounderStats(currentUser));
        model.addAttribute("startups", startupService.getStartupsByFounder(currentUser));
        model.addAttribute("requests", interestService.findByFounder(currentUser));
        model.addAttribute("notifications", notificationService.findForUser(currentUser));
        return "dashboard/founder-dashboard";
    }

    @GetMapping("/startups")
    public String startups(Model model) {
        User currentUser = currentUserUtil.getCurrentUser();
        model.addAttribute("startupsPage", new PageImpl<>(startupService.getStartupsByFounder(currentUser)));
        return "startup/list";
    }

    @GetMapping("/startups/new")
    public String createStartupForm(Model model) {
        model.addAttribute("startupRequest", new StartupRequest());
        return "startup/form";
    }

    @PostMapping("/startups")
    public String createStartup(@Valid @ModelAttribute("startupRequest") StartupRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "startup/form";
        }
        startupService.createStartup(request, currentUserUtil.getCurrentUser());
        redirectAttributes.addFlashAttribute("successMessage", "Startup created successfully.");
        return "redirect:/founder/startups";
    }

    @GetMapping("/startups/{id}/edit")
    public String editStartupForm(@PathVariable Long id, Model model) {
        Startup startup = startupService.getStartupByIdForFounder(id, currentUserUtil.getCurrentUser());
        StartupRequest request = new StartupRequest();
        request.setTitle(startup.getTitle());
        request.setDescription(startup.getDescription());
        request.setIndustry(startup.getIndustry());
        request.setProblemStatement(startup.getProblemStatement());
        request.setSolution(startup.getSolution());
        request.setFundRequired(startup.getFundRequired());
        request.setEquityOffered(startup.getEquityOffered());
        request.setStage(startup.getStage());
        request.setLocation(startup.getLocation());
        request.setBusinessModel(startup.getBusinessModel());
        request.setFinancialDetails(startup.getFinancialDetails());
        model.addAttribute("startupRequest", request);
        model.addAttribute("startupId", id);
        return "startup/form";
    }

    @PostMapping("/startups/{id}")
    public String updateStartup(@PathVariable Long id,
                                @Valid @ModelAttribute("startupRequest") StartupRequest request,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("startupId", id);
            return "startup/form";
        }
        startupService.updateStartup(id, request, currentUserUtil.getCurrentUser());
        redirectAttributes.addFlashAttribute("successMessage", "Startup updated successfully.");
        return "redirect:/founder/startups";
    }

    @PostMapping("/startups/{id}/delete")
    public String deleteStartup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        startupService.deleteStartup(id, currentUserUtil.getCurrentUser());
        redirectAttributes.addFlashAttribute("successMessage", "Startup removed successfully.");
        return "redirect:/founder/startups";
    }

    @GetMapping("/requests")
    public String requests(Model model) {
        User currentUser = currentUserUtil.getCurrentUser();
        model.addAttribute("requests", interestService.findPendingByFounder(currentUser));
        return "founder/requests";
    }

    @PostMapping("/requests/{id}/accept")
    public String acceptRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        interestService.acceptInterest(id, currentUserUtil.getCurrentUser());
        redirectAttributes.addFlashAttribute("successMessage", "Request accepted successfully.");
        return "redirect:/founder/requests";
    }

    @PostMapping("/requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        interestService.rejectInterest(id, currentUserUtil.getCurrentUser());
        redirectAttributes.addFlashAttribute("successMessage", "Request rejected successfully.");
        return "redirect:/founder/requests";
    }
}