package com.venturebridge.controller;
import com.venturebridge.dto.InvestorProfileRequest;
import com.venturebridge.entity.Bookmark;
import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import com.venturebridge.service.BookmarkService;
import com.venturebridge.service.DashboardService;
import com.venturebridge.service.InvestorProfileService;
import com.venturebridge.service.InterestService;
import com.venturebridge.service.StartupService;
import com.venturebridge.util.CurrentUserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/investor")
public class InvestorController {

    private final DashboardService dashboardService;
    private final InvestorProfileService investorProfileService;
    private final StartupService startupService;
    private final InterestService interestService;
    private final BookmarkService bookmarkService;
    private final CurrentUserUtil currentUserUtil;
    private final com.venturebridge.repository.BookmarkRepository bookmarkRepository;
    private final com.venturebridge.repository.InterestRepository interestRepository;
    private final com.venturebridge.service.NotificationService notificationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User currentUser = currentUserUtil.getCurrentUser();
        model.addAttribute("stats", dashboardService.buildInvestorStats(currentUser));
        com.venturebridge.entity.InvestorProfile profile = null;
        try {
            profile = investorProfileService.getProfile(currentUser);
        } catch (com.venturebridge.exception.ResourceNotFoundException exception) {
            // Gracefully ignore - new user has no profile yet
        }
        model.addAttribute("profile", profile);
        model.addAttribute("bookmarks", bookmarkService.findBookmarks(currentUser));
        model.addAttribute("requests", interestService.findByInvestor(currentUser));
        model.addAttribute("notifications", notificationService.findForUser(currentUser));
        
        java.util.List<Startup> allStartups = startupService.findAllStartups();
        java.util.List<Startup> recommended = allStartups.stream()
                .filter(Startup::isActive)
                .limit(3)
                .collect(java.util.stream.Collectors.toList());
        java.util.List<Startup> trending = allStartups.stream()
                .filter(s -> s.isActive() && s.getVerificationStatus() == com.venturebridge.entity.VerificationStatus.VERIFIED)
                .limit(3)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("recommendedStartups", recommended);
        model.addAttribute("trendingStartups", trending);

        return "dashboard/investor-dashboard";
    }

    @GetMapping("/profile")
    public String profileForm(Model model) {
        User currentUser = currentUserUtil.getCurrentUser();
        InvestorProfileRequest request = new InvestorProfileRequest();
        try {
            com.venturebridge.entity.InvestorProfile profile = investorProfileService.getProfile(currentUser);
            request.setOrganization(profile.getOrganization());
            request.setInvestmentMin(profile.getInvestmentMin());
            request.setInvestmentMax(profile.getInvestmentMax());
            request.setExperience(profile.getExperience());
            request.setBio(profile.getBio());
            request.setIndustriesInterested(profile.getIndustriesInterested());
        } catch (com.venturebridge.exception.ResourceNotFoundException exception) {
            // Clean request if profile is not found
        }
        model.addAttribute("investorProfileRequest", request);
        return "investor/profile";
    }

    @PostMapping("/profile")
    public String saveProfile(@Valid @ModelAttribute("investorProfileRequest") InvestorProfileRequest request,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "investor/profile";
        }
        investorProfileService.upsertProfile(currentUserUtil.getCurrentUser(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Investor profile saved successfully.");
        return "redirect:/investor/dashboard";
    }

    @GetMapping("/startups")
    public String browseStartups(@RequestParam(value = "q", required = false) String q,
                                 @RequestParam(value = "industry", required = false) String industry,
                                 @RequestParam(value = "stage", required = false) String stage,
                                 @RequestParam(value = "minFunding", required = false) java.math.BigDecimal minFunding,
                                 @RequestParam(value = "maxFunding", required = false) java.math.BigDecimal maxFunding,
                                 @RequestParam(value = "location", required = false) String location,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Startup> startupsPage = startupService.browseStartups(q, industry, stage, minFunding, maxFunding, location, pageable);
        model.addAttribute("startupsPage", startupsPage);
        model.addAttribute("query", q);
        model.addAttribute("industry", industry);
        model.addAttribute("stage", stage);
        model.addAttribute("minFunding", minFunding);
        model.addAttribute("maxFunding", maxFunding);
        model.addAttribute("location", location);
        return "startup/list";
    }

    @GetMapping("/startups/{id}")
    public String startupDetails(@PathVariable Long id, Model model) {
        Startup startup = startupService.getStartupById(id);
        model.addAttribute("startup", startup);
        User currentUser = currentUserUtil.getCurrentUser();
        
        com.venturebridge.entity.Interest interest = interestRepository.findByStartupAndInvestor(startup, currentUser).orElse(null);
        boolean hasInterest = interest != null && interest.getStatus() == com.venturebridge.entity.InterestStatus.PENDING;
        boolean hasAcceptedInterest = interest != null && interest.getStatus() == com.venturebridge.entity.InterestStatus.ACCEPTED;
        boolean hasBookmark = bookmarkRepository.findByInvestorAndStartup(currentUser, startup).isPresent();
        
        model.addAttribute("hasInterest", hasInterest);
        model.addAttribute("hasAcceptedInterest", hasAcceptedInterest);
        model.addAttribute("hasBookmark", hasBookmark);
        model.addAttribute("interestStatus", interest != null ? interest.getStatus().name() : "NONE");
        return "startup/details";
    }

    @PostMapping("/startups/{id}/interest")
    public String expressInterest(@PathVariable Long id, 
                                  @RequestParam(required = false) String message,
                                  @RequestParam(required = false) java.math.BigDecimal expectedInvestment,
                                  RedirectAttributes redirectAttributes) {
        interestService.expressInterest(currentUserUtil.getCurrentUser(), startupService.getStartupById(id), message, expectedInvestment);
        redirectAttributes.addFlashAttribute("successMessage", "Interest expressed successfully.");
        return "redirect:/investor/startups/" + id;
    }

    @PostMapping("/startups/{id}/cancel-interest")
    public String cancelInterest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        interestService.cancelInterest(currentUserUtil.getCurrentUser(), startupService.getStartupById(id));
        redirectAttributes.addFlashAttribute("successMessage", "Interest cancelled successfully.");
        return "redirect:/investor/startups/" + id;
    }

    @PostMapping("/startups/{id}/bookmark")
    public String bookmarkStartup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookmarkService.bookmarkStartup(currentUserUtil.getCurrentUser(), startupService.getStartupById(id));
        redirectAttributes.addFlashAttribute("successMessage", "Startup bookmarked successfully.");
        return "redirect:/investor/startups/" + id;
    }

    @PostMapping("/startups/{id}/unbookmark")
    public String unbookmarkStartup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookmarkService.removeBookmark(currentUserUtil.getCurrentUser(), startupService.getStartupById(id));
        redirectAttributes.addFlashAttribute("successMessage", "Startup removed from bookmarks.");
        return "redirect:/investor/startups/" + id;
    }

    @GetMapping("/bookmarks")
    public String bookmarks(Model model) {
        User currentUser = currentUserUtil.getCurrentUser();
        model.addAttribute("bookmarks", bookmarkService.findBookmarks(currentUser));
        return "investor/bookmarks";
    }

    @GetMapping("/requests")
    public String requests(Model model) {
        User currentUser = currentUserUtil.getCurrentUser();
        model.addAttribute("requests", interestService.findByInvestor(currentUser));
        return "investor/requests";
    }
}