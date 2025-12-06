package com.campus.exchange.controller;

import com.campus.exchange.model.Notification;
import com.campus.exchange.service.AuthService;
import com.campus.exchange.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    // Constructor-based dependency injection including AuthService
    public NotificationController(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getNotificationsForUser(
            @PathVariable String userId,
            @RequestHeader("Auth-Token") String token) {
        try {
            // 2. Verify Session via AuthService
            // This will throw an Exception if the token is invalid or expired.
            String loggedInUserId = authService.verifySession(token);

            // 3. Authorization Check: Ensure user can only view their own notifications
            if (!loggedInUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You can only view your own notifications.");
            }

            // 4. Fetch Notifications
            List<Notification> notifications = notificationService.showAllNotification(userId);
            return ResponseEntity.ok(notifications);

        } catch (Exception e) {
            // Handle Auth errors (thrown by AuthService)
            String msg = e.getMessage();
            if (msg != null && (msg.contains("Session") || msg.contains("token") || msg.contains("Invalid"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
            }

            // Fallback for IO or other unexpected errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching notifications: " + e.getMessage());
        }
    }
}