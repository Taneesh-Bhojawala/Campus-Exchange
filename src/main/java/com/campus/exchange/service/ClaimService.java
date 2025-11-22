package com.campus.exchange.service;

import com.campus.exchange.model.BlockEntry;
import com.campus.exchange.model.Claim;
import com.campus.exchange.model.Item;
import com.campus.exchange.model.User;
import com.campus.exchange.repository.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.classfile.CodeBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClaimService {
    private ClaimRepositoryJson claimRepositoryJson;
    private ItemRepositoryJson itemRepositoryJson;
    private BlockRepositoryJson blockRepositoryJson;
    private NotificationService notificationService;
    private UserRepositoryJson userRepositoryJson;

    private final long SEVEN_DAYS_TIME= 7L * 24 * 60*60*1000;

    public ClaimService(ClaimRepositoryJson claimRepositoryJson, ItemRepositoryJson itemRepositoryJson, BlockRepositoryJson blockRepositoryJson, NotificationService notificationService, UserRepositoryJson userRepositoryJson) {
        this.claimRepositoryJson = claimRepositoryJson;
        this.itemRepositoryJson = itemRepositoryJson;
        this.blockRepositoryJson = blockRepositoryJson;
        this.notificationService = notificationService;
        this.userRepositoryJson = userRepositoryJson;
    }

    public Claim createClaim(String itemID,String userID) throws Exception {
        blockRepositoryJson.removeExpiredUsers();
         //now we will check if the user is claiming the item listed by him or not
        Optional<Item> itemOptional = itemRepositoryJson.findById(itemID);
        if(itemOptional.isEmpty()){
            throw new NoSuchElementException("Item not found!");
        }
        Item item = itemOptional.get();
        //now we will check if that item is available for listing
        if(!item.getStatus().equals("ACTIVE")){
            throw new IllegalAccessException("This item is not available for claiming/listing");
        }

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

        claimRepositoryJson.save(claim);
        /*
        * here we will place the code for notification
        *
        * */

        return claim;
    }

    public Claim acceptClaim(Claim claim) throws Exception{
        String itemID = claim.getClaimID();
        Optional<Claim>claimOptional = claimRepositoryJson.findByItemID(itemID);
        if(claimOptional.isEmpty()){
            throw new Exception("Claim not found.");
        }
        Claim claim1 = claimOptional.get();
        Optional<Item> optionalItem = itemRepositoryJson.findById(itemID);
        if(optionalItem.isEmpty()){
            throw new Exception("Item is not listed");
        }
        Item item = optionalItem.get();
        item.setStatus("WAITING");
        itemRepositoryJson.update(item);
        claim1.setStatus("ACCEPTED");
        claimRepositoryJson.updateClaimList(claim1);
         /*
         * here we will have the code for accepted claim
         * */
        return claim1;
    }
    public Claim rejectClaim(Claim claim) throws Exception{
        String itemID = claim.getClaimID();
        Optional<Claim>claimOptional = claimRepositoryJson.findByItemID(itemID);
        if(claimOptional.isEmpty()){
            throw new Exception("Claim not found.");
        }
        Claim claim1 = claimOptional.get();
        claim1.setStatus("REJECTED");
        claimRepositoryJson.deleteByID(itemID);
//        claimRepositoryJson.updateClaimList(claim1);
        /*
         * here we will have the code for accepted claim
         * */

//      BlockEntry userBlocked = new BlockEntry(itemID,claim.getClaimerID(),timeBlocked);
//      blockRepositoryJson.addBlockedUser(userBlocked);
        return claim1;
    }

    public Item relistClaim(Claim claim) throws Exception{
        String itemID = claim.getClaimID();
        Optional<Item> OptionalItem = itemRepositoryJson.findById(itemID);
//        Optional<Claim>claimOptional = claimRepositoryJson.findByItemID(itemID);
        if(OptionalItem.isEmpty()){
            throw new Exception("Item not found.");
        }
        Item item = OptionalItem.get();
        item.setStatus("LISTED");
        itemRepositoryJson.update(item);
        /*
        * block the user due to which lister had tp relist the item
        * */
        long timeBlockedUntil = SEVEN_DAYS_TIME + System.currentTimeMillis();
        BlockEntry userBlocked = new BlockEntry(itemID,claim.getClaimerID(),timeBlockedUntil);
        blockRepositoryJson.addBlockedUser(userBlocked);
        return item;
    }
    public Item completeDeal(String itemID) throws Exception{
        Optional<Item> optionalItem = itemRepositoryJson.findById(itemID);
        if(optionalItem.isEmpty()){
            throw new Exception("Item not found");
        }
        Item item = optionalItem.get();
        item.setStatus("CLAIMED");
        itemRepositoryJson.update(item);
        return item;
    }

}
