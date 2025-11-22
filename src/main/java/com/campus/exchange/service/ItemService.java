package com.campus.exchange.service;

import com.campus.exchange.model.Item;
import com.campus.exchange.repository.ItemRepositoryJson;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemRepositoryJson itemRepo;

    public ItemService(ItemRepositoryJson itemRepo) {
        this.itemRepo = itemRepo;
    }

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

    public List<Item> Filter(String category) throws IOException{
        List<Item> list = itemRepo.findAll();
        List<Item> filteredItems = new ArrayList<>();

        for(Item item : list){
            if(item.getCategory().equals(category)){
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    public Optional<Item> findById(String itemId) throws IOException{
        List<Item> list = itemRepo.findAll();
        for(Item item : list){
            if(item.getItemId().equals(itemId)){
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }


}
