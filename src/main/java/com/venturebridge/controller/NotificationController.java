package com.venturebridge.controller;

import com.venturebridge.service.NotificationService;
import com.venturebridge.util.CurrentUserUtil;
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
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserUtil currentUserUtil;

    @GetMapping
    public String list(Model model) {
        var currentUser = currentUserUtil.getCurrentUser();
        model.addAttribute("notifications", notificationService.findForUser(currentUser));
        model.addAttribute("unreadCount", notificationService.countUnread(currentUser));
        return "notification/list";
    }

    @PostMapping("/{id}/read")
    public String markRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.markAsRead(id, currentUserUtil.getCurrentUser());
        redirectAttributes.addFlashAttribute("successMessage", "Notification marked as read.");
        return "redirect:/notifications";
    }
}