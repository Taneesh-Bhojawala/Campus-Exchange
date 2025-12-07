package com.campus.exchange.repository;

import com.campus.exchange.config.AppProperties;
import com.campus.exchange.model.BlockEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.cglib.core.Block;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
@Repository
public class BlockRepositoryJson {
    private final JsonUtils jsonUtils;
    private final String filePath;
    private final TypeReference<List<BlockEntry>> typeRef = new TypeReference<>() {};

    public BlockRepositoryJson(AppProperties props, JsonUtils jsonUtils){
        this.jsonUtils = jsonUtils;
        this.filePath = Paths.get(props.getDataFolder(),"blocks.json").toString();
    }

    public List<BlockEntry> findAll(){
        return jsonUtils.readList(filePath,typeRef);
    }

    public Optional<BlockEntry> findByItemAndUser(String itemID,String userID){
        //get the particular blocked user ---> itemID
        List<BlockEntry> blockEntries = findAll();
        for(BlockEntry entries:blockEntries){
            if(entries.getItemID().equals(itemID) && entries.getUserID().equals(userID)){
                return Optional.of(entries);
            }
        }
        return Optional.empty();
    }

    public void addBlockedUser(BlockEntry entry){
        List<BlockEntry> blockEntries = findAll();
        blockEntries.add(entry);
        jsonUtils.writeList(filePath,blockEntries);
    }

    public void removeExpiredUsers(){
        //a helpher function to run which updates the blocked users list
        long presentTime = System.currentTimeMillis();
        List<BlockEntry> blockedEntries = findAll();
        blockedEntries.removeIf(b -> b.getBlockedUntil() < presentTime);
        jsonUtils.writeList(filePath,blockedEntries);
    }
}
