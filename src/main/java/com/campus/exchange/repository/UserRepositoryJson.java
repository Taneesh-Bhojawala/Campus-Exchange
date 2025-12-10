package com.campus.exchange.repository;

import com.campus.exchange.config.AppProperties;
import com.campus.exchange.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for handling User data stored in users.json.
 * Functions performed:
 * Read all users from JSON
 * Find user by ID or email
 * Save a new user
 * Update an existing user
 * Delete user by ID (optional)
 *
 * This repository does NOT perform any logic (validation, OTP, login).
 * This class only performs JSON read/write operations.
 */

@Repository
public class UserRepositoryJson {

    private final JsonUtils jsonUtils;

    private final String filePath;

    private final TypeReference<List<User>> typeRef =  new TypeReference<>() {};

    public UserRepositoryJson(AppProperties appProperties, JsonUtils jsonUtils) {
//        Path userFile = Path.of(appProperties.getDataFolder(), "users.json");
        this.jsonUtils = jsonUtils;
        this.filePath = Paths.get(appProperties.getDataFolder(), "users.json").toString();
    }

    public List<User> findAll() throws IOException {
        List<User> users = jsonUtils.readList(filePath, typeRef);
        if(users != null){
            return users;
        }
        else{
            return new ArrayList<>();
    }
        }

    public Optional<User> findById(String id) throws IOException {
        if (id == null) return Optional.empty();
        return findAll().stream()
                .filter(u -> id.equals(u.getUserId()))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) throws IOException {
        if (email == null) return Optional.empty();
        String target = email.trim().toLowerCase();
        return findAll().stream()
                .filter(u -> u.getEmail() != null &&
                        u.getEmail().trim().toLowerCase().equals(target))
                .findFirst();
    }

    public void save(User user) throws IOException {
        if (user == null) return;
        List<User> users = findAll();
        users.add(user);
        jsonUtils.writeList(filePath, users);
    }

    public void update(User user) throws IOException {
        if (user == null || user.getUserId() == null) return;

        List<User> users = findAll();
        boolean replaced = false;

        for (int i = 0; i < users.size(); i++) {
            if (user.getUserId().equals(users.get(i).getUserId())) {
                users.set(i, user);
                replaced = true;
                break;
            }
        }

        if (!replaced) users.add(user);

        jsonUtils.writeList(filePath, users);
    }

    public void deleteById(String id) throws IOException {
        if (id == null) return;

        List<User> users = findAll();
        boolean removed = users.removeIf(u -> id.equals(u.getUserId()));

        if (removed) jsonUtils.writeList(filePath, users);
    }
}



