package com.campus.exchange.service;

import com.campus.exchange.model.BlockEntry;
import com.campus.exchange.model.Claim;
import com.campus.exchange.model.Item;
import com.campus.exchange.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClaimService {
    private final ClaimRepositoryJson claimRepositoryJson;
    private final ItemRepositoryJson itemRepositoryJson;
    private final BlockRepositoryJson blockRepositoryJson;
    private final ItemService itemService;
    private final CustomLogger logger;
    private final NotificationService notificationService;
    public ClaimService(ClaimRepositoryJson claimRepositoryJson, ItemRepositoryJson itemRepositoryJson, BlockRepositoryJson blockRepositoryJson, ItemService itemService, CustomLogger logger, NotificationService notificationService) {
        this.claimRepositoryJson = claimRepositoryJson;
        this.itemRepositoryJson = itemRepositoryJson;
        this.blockRepositoryJson = blockRepositoryJson;
        this.itemService = itemService;
        this.logger = logger;
        this.notificationService = notificationService;
    }
//    private ItemService itemService = new ItemService(itemRepositoryJson);
    public Claim createClaim(String itemID,String userID) throws Exception {
        logger.log("ClaimService", "Claim initiated for itemID " + itemID + " by userID " + userID);
        blockRepositoryJson.removeExpiredUsers();
         //now we will check if the user is claiming the item listed by him or not
        Optional<Item> itemOptional = itemRepositoryJson.findById(itemID);
        if(itemOptional.isEmpty()){
            throw new NoSuchElementException("Item not found!");
        }
        Item item = itemOptional.get();
        //now we will check if that item is available for listing
//        if(!item.getStatus().equals("LISTED") || !item.getStatus().equals("PENDING")){
//            throw new IllegalAccessException("This item is not available for claiming/listing");
//        }

        //now we will check if the user is blacklisted from buying that item or not
        Optional<BlockEntry> blockEntryOptional = blockRepositoryJson.findByItemAndUser(itemID,userID);

        if(blockEntryOptional.isPresent()){
            throw new Exception("User " + userID + " is blocked for purchasing " + itemID);
        }

        //now we have to check if the person who claimed it is the owner or not
        if(item.getListerId().equals(userID)){
            throw new Exception("User cannot claim the item listed by themselves.");
        }

        //now we have verified all the conditions and are ready to request to claim that particular item

        Claim claim  = new Claim(UUID.randomUUID().toString(),itemID,userID,item.getListerId(),"WAITING",System.currentTimeMillis());
        itemService.updateStatus("PENDING",itemID);
        claimRepositoryJson.save(claim);
        /*
        * here we will place the code for notification
        * */

        notificationService.NotifyClaimCreated(itemID,claim.getListerId(),claim.getClaimerId(),item.getTitle());
        logger.log("ClaimService", "Claim created for itemID " + itemID);
        return claim;
    }

    public Claim acceptClaim(Claim claim) throws Exception{
        String itemID = claim.getItemId();
        Optional<Claim>claimOptional = claimRepositoryJson.findByItemId(itemID);
        if(claimOptional.isEmpty()){
            throw new Exception("Claim not found.");
        }
        Claim claim1 = claimOptional.get();
        Optional<Item> optionalItem = itemRepositoryJson.findById(itemID);
        if(optionalItem.isEmpty()){
            throw new Exception("Item is not listed");
        }
        itemService.updateStatus("PENDING",itemID);
        claim1.setStatus("ACCEPTED");
        claimRepositoryJson.updateClaimList(claim1);
         /*
         * here we will have the code for accepted claim
         * */
        Optional<Item> itemOptional = itemRepositoryJson.findById(itemID);
        if(itemOptional.isEmpty()){
            throw new NoSuchElementException("Item not found!");
        }
        Item item = itemOptional.get();
        notificationService.notifyClaimAccepted(itemID,claim.getListerId(),claim.getClaimerId(),item.getTitle());
        logger.log("ClaimService", "Claim accepted for itemID " + itemID);
        return claim1;
    }
    public Claim rejectClaim(Claim claim) throws Exception{
        String itemID = claim.getItemId();
        Optional<Claim>claimOptional = claimRepositoryJson.findByItemId(itemID);
        if(claimOptional.isEmpty()){
            throw new Exception("Claim not found.");
        }
        Claim claim1 = claimOptional.get();
        claim1.setStatus("REJECTED");
        claimRepositoryJson.deleteByID(itemID);
        /*
         * here we will have the code for accepted claim
         * */
        Optional<Item> itemOptional = itemRepositoryJson.findById(itemID);
        if(itemOptional.isEmpty()){
            throw new NoSuchElementException("Item not found!");
        }
        Item item = itemOptional.get();
        notificationService.notifyClaimRejected(itemID,claim.getListerId(),claim.getClaimerId(),item.getTitle());
        logger.log("ClaimService", "Claim rejected for itemID " + itemID);
        return claim1;
    }

    public void relistItem(Claim claim) throws Exception{
        String itemID = claim.getItemId();
        Optional<Item> OptionalItem = itemRepositoryJson.findById(itemID);
//        Optional<Claim>claimOptional = claimRepositoryJson.findByItemID(itemID);
        if(OptionalItem.isEmpty()){
            throw new Exception("Item not found.");
        }
        itemService.updateStatus("LISTED",itemID);
        /*
        * block the user due to which lister had tp relist the item
        * */
        long SEVEN_DAYS_TIME = 7L * 24 * 60 * 60 * 1000;
        long timeBlockedUntil = SEVEN_DAYS_TIME + System.currentTimeMillis();
        BlockEntry userBlocked = new BlockEntry(itemID,claim.getClaimerId(),timeBlockedUntil);
        blockRepositoryJson.addBlockedUser(userBlocked);
        logger.log("ClaimService", "Item relisted for itemID " + itemID);
    }
    public void completeDeal(String itemID) throws Exception{
        Optional<Item> optionalItem = itemRepositoryJson.findById(itemID);
        if(optionalItem.isEmpty()){
            throw new Exception("Item not found");
        }
        itemService.updateStatus("CLAIMED",itemID);
        logger.log("ClaimService", "Claim completed for itemID " + itemID);
    }
    public List<Claim> getAllClaims(String listerID){
        List<Claim> claimList = claimRepositoryJson.findAll();
        List<Claim> claimList1 = new ArrayList<>();
        for(Claim claim:claimList){
            if(claim.getListerId().equals(listerID)){
                claimList1.add(claim);
            }
        }
        return claimList1;
    }
}
