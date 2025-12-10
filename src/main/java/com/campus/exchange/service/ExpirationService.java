package com.campus.exchange.service;

import com.campus.exchange.model.Item;
import com.campus.exchange.model.PendingSignup;
import com.campus.exchange.repository.ItemRepositoryJson;
import com.campus.exchange.repository.PendingSignupRepositoryJson;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service responsible for background cleanup tasks.
 * It periodically checks for expired items and pending signups (OTPs)
 * It removes them from the system to keep data clean.
 */


@Service
public class ExpirationService {

    private final ItemRepositoryJson itemRepository;
    private final PendingSignupRepositoryJson pendingSignupRepository;
    private final NotificationService notificationService;
    private final CustomLogger logger;
    private final FileStorageService fileStorageService;

    //Time in milliseconds after which item expires (10 minutes for demo purpose)
    private static final long EXPIRATION_MS = 600000L;

    public ExpirationService(ItemRepositoryJson itemRepository,
                             PendingSignupRepositoryJson pendingSignupRepository,
                             NotificationService notificationService,
                             CustomLogger logger,
                             FileStorageService fileStorageService) {
        this.itemRepository = itemRepository;
        this.pendingSignupRepository = pendingSignupRepository;
        this.notificationService = notificationService;
        this.logger = logger;
        this.fileStorageService = fileStorageService;
    }

    //This function runs automatically every 30 seconds
    @Scheduled(fixedDelayString = "${app.expiration.check-interval-ms:30000}")
    public void scheduledExpiryTask() {
        expireOldItems();
        cleanupPendingSignups();
    }

    /**
     * Checks all items in the repository.
     * If an item is 'LISTED' or 'PENDING' and is older than EXPIRATION_MS,
     * it deletes the item, its associated image, and notifies the owner.
     */
    public void expireOldItems() {
        try {
            List<Item> items = itemRepository.findAll();

            for (Item item : items) {
                String status = item.getStatus();
                if (status == null) continue;

                boolean candidate = "LISTED".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status);
                if (!candidate) continue;
                long now = Instant.now().toEpochMilli();
                long createdAt = item.getCreatedAt();
                if (now - createdAt >= EXPIRATION_MS) {
                    try {
                        if (item.getImagePath() != null && !item.getImagePath().isBlank()) {
                            fileStorageService.delete(item.getImagePath());
                        }
                        itemRepository.deleteById(item.getItemId());
                        logger.log("ExpirationService", "Expired and deleted item: " + item.getItemId());

                        notificationService.notifyItemExpired(item.getItemId(), item.getListerId(), item.getTitle());

                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.log("ExpirationService", "Error expiring item: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks all pending signups (users verifying OTP).
     * If the OTP validity period has passed, the pending entry is removed.
     */
    public void cleanupPendingSignups() {
        try {
            List<PendingSignup> pendings = pendingSignupRepository.findAll();
            long now = Instant.now().toEpochMilli();

            for (PendingSignup p : pendings) {
                long expiresAt = p.getExpiresAt();
                if (expiresAt <= 0) continue;

                if (expiresAt <= now) {
                    try {
                        pendingSignupRepository.deleteByEmail(p.getEmail());
                        logger.log("ExpirationService", "Pending Signup Deleted/Expired: " + p.getEmail());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        logger.log("ExpirationService", "Error deleting signup: " + ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}