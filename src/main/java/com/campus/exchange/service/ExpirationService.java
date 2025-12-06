package com.campus.exchange.service;

import com.campus.exchange.model.Item;
import com.campus.exchange.model.PendingSignup;
import com.campus.exchange.repository.ItemRepositoryJson;
import com.campus.exchange.repository.PendingSignupRepositoryJson;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//import java.io.IOException;
import java.time.Instant;
import java.util.List;


/**
 * ExpirationService:
 * - Expires item listings automatically after 5 minutes (sets status = "EXPIRED" and notifies owner)
 * - Deletes pending signup entries older than 5 minutes (if OTP not verified)
 *
 * This class uses a scheduled task that runs every minute and performs both cleanup tasks.
 */
@Service
public class ExpirationService {

    private final ItemRepositoryJson itemRepository;
    private final PendingSignupRepositoryJson pendingSignupRepository;
    private final NotificationService notificationService;
    private final CustomLogger logger;

    private static final long EXPIRATION_MS = 10L * 10L * 100L;

    public ExpirationService(ItemRepositoryJson itemRepository,
                             PendingSignupRepositoryJson pendingSignupRepository,
                             NotificationService notificationService, CustomLogger logger) {
        this.itemRepository = itemRepository;
        this.pendingSignupRepository = pendingSignupRepository;
        this.notificationService = notificationService;
        this.logger = logger;
    }

    /**
    Scheduler runs every minute. Runs both functions. They both check time stamps.
     */
    @Scheduled(fixedDelayString = "${app.expiration.check-interval-ms:30000}")
    public void scheduledExpiryTask() {
//        System.out.println("Scheduling expiry task");
        expireOldItems();
        cleanupPendingSignups();
    }

    /**
     * Expires items older than EXPIRATION_MS (Here 5 minutes).
     * Marks them with status "EXPIRED", updates the repository, and notifies the lister.
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
                long createdAt = item.getCreatedAt(); // epoch millis
                if (now - createdAt >= EXPIRATION_MS) {
                    // notify owner
                    try {
                        itemRepository.deleteById(item.getItemId());
                        notificationService.notifyItemExpired(item.getItemId(), item.getListerId(), item.getTitle());
                        logger.log("ExpirationService", "Item " + item.getItemId() + " has been expired.");

                    } catch (Exception e) {
                        // don't fail the entire loop for a single notification failure
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            // repository read/write error
            e.printStackTrace();
        }
    }

    /**
     * Deletes pending signups whose expiresAt <= now (OTP not verified in time).
     * We rely on PendingSignup.expiresAt being set by AuthService when OTP is created.
     */
    public void cleanupPendingSignups() {
        try {
            List<PendingSignup> pendings = pendingSignupRepository.findAll();
            long now = Instant.now().toEpochMilli();

            for (PendingSignup p : pendings) {
                // If expiresAt is not set, skip. AuthService should set expiresAt when creating OTP.
                long expiresAt = p.getExpiresAt();
                if (expiresAt <= 0) continue;

                if (expiresAt <= now) {
                    // remove pending entry (user didn't verify OTP in time)
                    try {
                        pendingSignupRepository.deleteByEmail(p.getEmail());
                        // optional: print/log so you can demonstrate deletion in console
                        logger.log("ExpirationService", "PendingSignup " + p.getEmail() + " has been deleted.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
