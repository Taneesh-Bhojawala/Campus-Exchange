package com.campus.exchange.service;

import com.campus.exchange.model.Notification;
import com.campus.exchange.model.User;
import com.campus.exchange.repository.NotificationRepositoryJson;
import com.campus.exchange.repository.UserRepositoryJson;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {
    NotificationRepositoryJson notificationJson;
    UserRepositoryJson userJson;

    public NotificationService(NotificationRepositoryJson notificationJson, UserRepositoryJson userJson) {
        this.notificationJson = notificationJson;
        this.userJson = userJson;
    }

    public void NotifyClaimCreated(String itemId,String listerId,String claimerId,String itemTitle) throws IOException {
        Optional<User> lister = userJson.findById(listerId);
        Optional<User> claimer = userJson.findById(claimerId);
        if(lister.isEmpty() || claimer.isEmpty()){
            return;
        }
        String messageToLister = claimer.get().getName() + " has requested to claim your item" + itemTitle + ".";
        Notification listerNotification = new Notification(UUID.randomUUID().toString(),listerId,"CLAIM_CREATED",
                                                            "NEW CLAIM REQUEST",messageToLister,
                                                            System.currentTimeMillis(),false);
        String messageToClaimer = "Your claim request for "+ itemTitle + " has been submitted.";
        Notification claimerNotification = new Notification(UUID.randomUUID().toString(),claimerId,"CLAIM_CREATED",
                                            "CLAIM SUBMITTED",messageToClaimer,
                                                System.currentTimeMillis(), false);
        notificationJson.addNotification(listerNotification);
        notificationJson.addNotification(claimerNotification);
    }

    public void notifyClaimAccepted(String itemId,String listerId,String claimerId,String itemTitle) throws IOException{
        Optional<User> lister = userJson.findById(listerId);
        Optional<User> claimer = userJson.findById(claimerId);
        if(lister.isEmpty() || claimer.isEmpty()){
            return;
        }
        String messageToLister = "You accepted " + claimer.get().getName() +  " claim on " +itemTitle + " .\n" +
                "They can now contact you at " + lister.get().getEmail()+ " .\n" +
                "Their email: "+ claimer.get().getEmail()+ " .\n" +
                "Their hostel number: " + claimer.get().getHostelNumber() + " .";
        Notification listerNotification = new Notification(UUID.randomUUID().toString(),listerId,"CLAIM_ACCEPTED",
                "Claim Accepted Successfully",messageToLister,
                System.currentTimeMillis(),false);
        String messageToClaimer = "Good news! Your claim on " + itemTitle + " has been accepted.\n" +
                "You may now contact the owner at: " + lister.get().getEmail() + " to coordinate pickup.\n" +
                "Their hostel number: " + claimer.get().getHostelNumber() + " .\n";
        Notification claimerNotification = new Notification(UUID.randomUUID().toString(),claimerId,"CLAIM_ACCEPTED",
                "Claim Accepted!",messageToClaimer,
                System.currentTimeMillis(), false);
        notificationJson.addNotification(listerNotification);
        notificationJson.addNotification(claimerNotification);
    }
    public void notifyClaimRejected(String itemId,String listerId,String claimerId,String itemTitle) throws IOException{
        Optional<User> claimer = userJson.findById(claimerId);
        if(claimer.isEmpty()){
            return;
        }
        String messageToClaimer = "Your claim for " + itemTitle + " was not accepted.\n" +
                "You may still explore other available listings.";
        Notification claimerNotification = new Notification(UUID.randomUUID().toString(),claimerId,"CLAIM_REJECTED",
                "Claim Rejected",messageToClaimer,
                System.currentTimeMillis(), false);
        notificationJson.addNotification(claimerNotification);
    }
    public void notifyItemExpired(String itemId,String listerId, String itemTitle) throws IOException{
        Optional<User> lister = userJson.findById(listerId);
        if(lister.isEmpty()){
            return;
        }
        String messageToLister = "Your item "+ itemTitle +" has expired and is no longer visible on the platform.\n" +
                "If it's still available, you may relist it anytime.";
        Notification listerNotification = new Notification(UUID.randomUUID().toString(),listerId,"ITEM_EXPIRED",
                "Listing Expired",messageToLister,
                System.currentTimeMillis(),false);
        notificationJson.addNotification(listerNotification);
    }
    public List<Notification> showAllNotification(String userId) throws IOException{
        List<Notification> list = notificationJson.findAll();
        List<Notification> result = new ArrayList<>();

        for(Notification notification : list){
            if(notification.getUserId().equals(userId)){
                result.add(notification);
                notificationJson.updateNotification(notification.getId());
            }
        }
        return result;
    }
}
