package com.campus.exchange.controller;

import com.campus.exchange.model.Item;
import com.campus.exchange.service.FileStorageService;
import com.campus.exchange.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** handles HTTP requests coming from frontend/postman in the path "/api/items" */
@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;
    private final FileStorageService storage;

    /** constructor*/
    public ItemController(ItemService itemService,FileStorageService storage){
        this.itemService = itemService;
        this.storage = storage;
    }

    /** This endpoint expects multipart form data because image upload is included.*/
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createItem(
            @RequestParam String listerId,
            @RequestParam int quantity,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam Double price,
            @RequestPart MultipartFile image
    ){
        try{
            String imagePath = storage.store(image);

            Item item = new Item();
            item.setItemId(UUID.randomUUID().toString());
            item.setListerId(listerId);
            item.setQuantity(quantity);
            item.setTitle(title);
            item.setDescription(description);
            item.setCategory(category);
            item.setPrice(price);
            item.setImagePath(imagePath);

            Item created = itemService.createItem(item);
            return ResponseEntity.status(201).body(created);
        }catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving item: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }
    /** gets all the items from items.json*/
    @GetMapping
    public ResponseEntity<?> getItems(@RequestParam(required = false) String category, @RequestParam String college){
        try{
            List<Item> items;
            if(category != null && !category.isBlank()){
                items = itemService.filter(category);
            }
            else{
                items = itemService.getAllItems(college);
            }
            return ResponseEntity.ok(items);
        }catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading items: " + e.getMessage());
        }
    }

    /** finds items by there id*/
    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable String id){
        try{
            Optional<Item> opt = itemService.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(opt.get());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading item: " + e.getMessage());
        }
    }

    /** changes status of the item: CLAIMED,LISTED,PENDING*/
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {
        try {
            itemService.updateStatus(status, id);
            return ResponseEntity.ok("Status updated to: " + status);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error updating status: " + e.getMessage());
        }
    }

    /** shows all the items listed by the user with the userid given*/
    @GetMapping("/listed/{userId}")
    public ResponseEntity<?> getListedItems(@PathVariable String userId) {
        try {
            List<Item> items = itemService.ListedItems(userId);
            return ResponseEntity.ok(items);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading items: " + e.getMessage());
        }
    }
}
