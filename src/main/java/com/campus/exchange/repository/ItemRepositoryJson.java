package com.campus.exchange.repository;

import com.campus.exchange.config.AppProperties;
import com.campus.exchange.model.Item;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemRepositoryJson {

    /** object of JsonUtils repo which is a template for read and write methods for json files */
    private final JsonUtils json;

    /** path string of item.json file */
    private final String path;

    /** type = Item */
    private final TypeReference<List<Item>> type = new TypeReference<>() {};

    /** Constructor */
    public ItemRepositoryJson(AppProperties props,JsonUtils jsonUtils){
        this.path = String.valueOf(Path.of(props.getDataFolder(), "items.json"));
        this.json =  jsonUtils;
    }

    /** returns all the item present in items.json*/
    public List<Item> findAll() throws IOException{
        return json.readList(path,type);
    }
    public Optional<Item> findById(String itemId) throws IOException {
        for(Item item :  this.findAll()){
            if(item.getItemId().equals(itemId)){
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    /** adds new item object to the json file */
    public void save(Item item) throws IOException{
        List<Item> list = this.findAll();
        list.add(item);
        json.writeList(path,list);
    }

    /** update the item with the same itemId to updatedItem.
     *  can be used for example to change status of the item from Listed to pending or claimed.
     */
    public void update(Item updatedItem) throws IOException{
        List<Item> list = this.findAll();
        for(int idx = 0; idx<list.size();idx++){
            if(list.get(idx).getItemId().equals(updatedItem.getItemId())){
                list.set(idx,updatedItem);
                json.writeList(path,list);
                return;
            }
        }
    }

    /** delete the item with item id = itemId
     * used when an item is expired or claimed
     */
    public void deleteById(String itemId) throws IOException{
        List<Item> list = this.findAll();
        for(int idx =0; idx<list.size(); idx++){
            if(list.get(idx).getItemId().equals(itemId)){
                list.remove(idx);
                json.writeList(path,list);
                return;
            }
        }
    }


}
