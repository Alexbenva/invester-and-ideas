package com.venturebridge.controller;

import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import com.venturebridge.service.StartupService;
import com.venturebridge.service.UserService;
import com.venturebridge.util.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class StartupController {

    private final StartupService startupService;
    private final UserService userService;
    private final CurrentUserUtil currentUserUtil;
    private final com.venturebridge.repository.BookmarkRepository bookmarkRepository;
    private final com.venturebridge.repository.InterestRepository interestRepository;

    @GetMapping("/startups")
    public String listStartups(@RequestParam(value = "q", required = false) String q,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Startup> startupsPage = startupService.browseStartups(q, pageable);
        model.addAttribute("startupsPage", startupsPage);
        model.addAttribute("query", q);
        return "startup/list";
    }

    @GetMapping("/startups/{id}")
    public String startupDetails(@PathVariable Long id, Model model) {
        Startup startup = startupService.getStartupById(id);
        model.addAttribute("startup", startup);

        User currentUser = null;
        try {
            currentUser = currentUserUtil.getCurrentUser();
        } catch (Exception exception) {
            // Gracefully ignore - anonymous visitor
        }

        boolean hasInterest = false;
        boolean hasAcceptedInterest = false;
        boolean hasBookmark = false;
        String interestStatus = "NONE";
        if (currentUser != null && currentUser.getRole() == com.venturebridge.entity.Role.ROLE_INVESTOR) {
            com.venturebridge.entity.Interest interest = interestRepository.findByStartupAndInvestor(startup, currentUser).orElse(null);
            hasInterest = interest != null && interest.getStatus() == com.venturebridge.entity.InterestStatus.PENDING;
            hasAcceptedInterest = interest != null && interest.getStatus() == com.venturebridge.entity.InterestStatus.ACCEPTED;
            hasBookmark = bookmarkRepository.findByInvestorAndStartup(currentUser, startup).isPresent();
            interestStatus = interest != null ? interest.getStatus().name() : "NONE";
        }
        model.addAttribute("hasInterest", hasInterest);
        model.addAttribute("hasAcceptedInterest", hasAcceptedInterest);
        model.addAttribute("hasBookmark", hasBookmark);
        model.addAttribute("interestStatus", interestStatus);
        return "startup/details";
    }
}