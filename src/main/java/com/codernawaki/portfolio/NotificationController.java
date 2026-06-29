package com.codernawaki.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NotificationController {

    private static final int NOTIFICATIONS_PER_PAGE = 10;

    private final BlogNotificationService notificationService;
    private final PortfolioProperties portfolioProperties;

    @Autowired
    public NotificationController(BlogNotificationService notificationService, PortfolioProperties portfolioProperties) {
        this.notificationService = notificationService;
        this.portfolioProperties = portfolioProperties;
    }

    @GetMapping("/notifications")
    public String notifications(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        int safePage = Math.max(page, 0);
        Page<BlogNotificationView> notificationsPage = notificationService.getNotifications(
                PageRequest.of(safePage, NOTIFICATIONS_PER_PAGE));

        model.addAttribute("notificationsPage", notificationsPage);
        model.addAttribute("notifications", notificationsPage.getContent());
        model.addAttribute("pageTitle", "Notifications | " + portfolioProperties.getDisplayName());
        model.addAttribute("pageDescription",
                "Recent blog publication notifications on " + portfolioProperties.getDisplayName() + ".");
        model.addAttribute("pageUrl", portfolioProperties.getSiteUrl() + "/notifications");
        return "notifications/list";
    }
}
