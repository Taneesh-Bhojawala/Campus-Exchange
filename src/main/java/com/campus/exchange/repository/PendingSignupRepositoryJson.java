package com.campus.exchange.repository;

import com.campus.exchange.config.AppProperties;
import com.campus.exchange.model.PendingSignup;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * This Repository is for handling file read and write for the file pending_signups.json
 * Functions:
 * Read file and form complete list
 * Find user by email id
 * Delete user by email id
 * Save user to file
 *
 * There is no logic for checking of otp and all implemented here.
 * This is only talking to the json file pending_signup.*/
@Repository
public class PendingSignupRepositoryJson
{
    private JsonUtils jsonUtils;
    private AppProperties appProperties;

    public PendingSignupRepositoryJson(JsonUtils jsonUtils, AppProperties appProperties)
    {
        this.jsonUtils = jsonUtils;
        this.appProperties = appProperties;
    }

    private String pendingFilePath()
    {
        return Paths.get(appProperties.getDataFolder(), "pending_signup.json").toString();
    }

    public List<PendingSignup> findAll()
    {
        return jsonUtils.readList(pendingFilePath(), new TypeReference<List<PendingSignup>>() {});
    }

    public void save(PendingSignup pendingSignup)
    {
        List<PendingSignup> pendingUsers = findAll();
        pendingUsers.add(pendingSignup);
        jsonUtils.writeList(pendingFilePath(), pendingUsers);
    }

    public Optional<PendingSignup> findByEmail(String email) throws IOException
    {
        List<PendingSignup> pendingUsers = findAll();
        for(PendingSignup pendingUser : pendingUsers)
        {
            if(pendingUser.getMail().equals(email))
            {
                return Optional.of(pendingUser);
            }
        }
        return Optional.empty();
    }

    public void deleteByEmail(String email) throws IOException
    {
        List<PendingSignup> pendingUsers = findAll();
        pendingUsers.removeIf(p -> p.getMail().equals(email));
        jsonUtils.writeList(pendingFilePath(), pendingUsers);
    }
}
