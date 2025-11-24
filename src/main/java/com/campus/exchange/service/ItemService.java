package com.campus.exchange.service;

import com.campus.exchange.model.Item;
import com.campus.exchange.repository.ItemRepositoryJson;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** service layer for items*/
@Service
public class ItemService {
    private final ItemRepositoryJson itemRepo;

    /** constructor*/
    public ItemService(ItemRepositoryJson itemRepo) {
        this.itemRepo = itemRepo;
    }

    /** creates new item and calls .save() method of itemRepo which saves item to the json file*/
    public Item createItem(Item item) throws IOException{
        if(item.getCreatedAt() == 0L){
            item.setCreatedAt(System.currentTimeMillis());
        }
        if(item.getStatus() == null || item.getStatus().isBlank()){
            item.setStatus("LISTED");
        }
        itemRepo.save(item);
        return item;
    }

    /** filters the item according to the category given and returns filtered items list*/
    public List<Item> filter(String category) throws IOException{
        List<Item> list = itemRepo.findAll();
        List<Item> filteredItems = new ArrayList<>();

        for(Item item : list){
            if(item.getCategory().equals(category)){
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    /** returns item with item id = itemId*/
    public Optional<Item> findById(String itemId) throws IOException{
        List<Item> list = itemRepo.findAll();
        for(Item item : list){
            if(item.getItemId().equals(itemId)){
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
    /** changes the status of the item*/
    public void updateStatus(String status, String itemId) throws IOException{
        List<Item> list = itemRepo.findAll();
        for(int idx =0;idx<list.size();idx++){
            if(list.get(idx).getItemId().equals(itemId)){
                list.get(idx).setStatus(status);
                itemRepo.update(list.get(idx));
                return;
            }
        }
    }
    /** returns all the items listed by the user  with user id = userId*/
    public List<Item> ListedItems(String userId) throws IOException{
        List<Item> list = itemRepo.findAll();
        List<Item> listedItem = new ArrayList<>();

        for(Item item : list){
            if(item.getListerId().equals(userId)){
                listedItem.add(item);
            }
        }
        return listedItem;
    }

    /** returns all the items in the items.json file*/
    public List<Item> getAllItems(String collegeName) throws IOException{
        List<Item> list = itemRepo.findAll();
        List<Item> result = new ArrayList<>();

        for(Item item : list){
            if(item.getCollege().equals(collegeName)){
                result.add(item);
            }
        }
        return result;
    }
}
