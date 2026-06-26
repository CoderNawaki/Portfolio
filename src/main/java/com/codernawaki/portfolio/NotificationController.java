package com.codernawaki.portfolio;

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
    private final PortfolioService portfolioService;

    public NotificationController(BlogNotificationService notificationService, PortfolioService portfolioService) {
        this.notificationService = notificationService;
        this.portfolioService = portfolioService;
    }

    @GetMapping("/notifications")
    public String notifications(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        PortfolioProperties props = portfolioService.getProperties();
        int safePage = Math.max(page, 0);
        Page<BlogNotification> notificationsPage = notificationService.getNotifications(
                PageRequest.of(safePage, NOTIFICATIONS_PER_PAGE));

        model.addAttribute("notificationsPage", notificationsPage);
        model.addAttribute("notifications", notificationsPage.getContent());
        model.addAttribute("pageTitle", "Notifications | " + props.getDisplayName());
        model.addAttribute("pageDescription", "Recent blog publication notifications on " + props.getDisplayName() + ".");
        model.addAttribute("pageUrl", props.getSiteUrl() + "/notifications");
        return "notifications/list";
    }
}
