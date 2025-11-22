package com.campus.exchange.controller;

import com.campus.exchange.model.Notification;
import com.campus.exchange.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * NotificationController
 *
 * SRS Mapping:
 *   Section 4.4 Notifications
 *   Method: GET
 *   Endpoint: /api/notifications/{userId}
 *
 * Responsibility:
 *   - Expose a REST endpoint to fetch all notifications for a given user.
 *   - Delegate all business logic to NotificationService.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // Constructor-based dependency injection
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * GET /api/notifications/{userId}
     *
     * Returns all notifications for the given user.
     * Also marks them as read (NotificationService updates JSON internally).
     *
     * @param userId ID of the user whose notifications are requested
     * @return 200 OK with List<Notification> on success
     *         500 Internal Server Error if underlying IO fails
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getNotificationsForUser(@PathVariable String userId) {
        try {
            List<Notification> notifications = notificationService.showAllNotification(userId);
            return ResponseEntity.ok(notifications);
        } catch (IOException e) {
            // Any JSON read/write problem from NotificationRepositoryJson
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch notifications for userId=" + userId);
        } catch (Exception e) {
            // Fallback for any unexpected error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while fetching notifications");
        }
    }
}
