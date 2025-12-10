package com.campus.exchange.repository;

import com.campus.exchange.config.AppProperties;
import com.campus.exchange.model.Notification;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for handling Notification data stored in notifications.json.
 * Functions performed:
 * Read all notifications from JSON
 * Find all notifications of a particular user using userId
 * update notification read = true once read by the receiver
 *
 * This repository does NOT perform any logic.
 * This class only performs JSON read/write operations.
 */
@Repository
public class NotificationRepositoryJson {
    private final JsonUtils json;
    private final String path;
    private final TypeReference<List<Notification>> type = new TypeReference<>() {};

    public NotificationRepositoryJson(AppProperties props, JsonUtils jsonUtils){
        this.path = String.valueOf(Path.of(props.getDataFolder(), "notifications.json"));
        this.json =  jsonUtils;
    }

    public List<Notification> findAll() throws IOException {
        return json.readList(path,type);
    }

    public List<Notification> findByUser(String userId) throws IOException{
        List<Notification> list = this.findAll();
        List<Notification> result = new ArrayList<>();

        for(Notification notification : list){
            if(notification.getUserId().equals(userId)){
                result.add(notification);
            }
        }
        return result;
    }

    public void addNotification(Notification notification) throws IOException{
        List<Notification> list = this.findAll();
        list.add(notification);
        json.writeList(path,list);
    }
    public void updateNotification(String notificationId) throws IOException{
        List<Notification> list = this.findAll();
        for(int idx = 0;idx<list.size();idx++){
            if(list.get(idx).getId().equals(notificationId)){
                list.get(idx).setRead(true);
                json.writeList(path,list);
                return;
            }
        }
    }

}
