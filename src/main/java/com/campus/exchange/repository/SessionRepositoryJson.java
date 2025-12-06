package com.campus.exchange.repository;


import com.campus.exchange.config.AppProperties;
import com.campus.exchange.model.PendingSignup;
import com.campus.exchange.model.Sessions;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class SessionRepositoryJson {
    private JsonUtils jsonUtils;
    private String sessionFilePath;


    public SessionRepositoryJson(JsonUtils jsonUtils, AppProperties appProperties) {
        this.jsonUtils = jsonUtils;
        this.sessionFilePath = appProperties.getDataFolder() + "/sessions.json";
    }

    public List<Sessions> findAll() throws IOException{
        return jsonUtils.readList(sessionFilePath, new TypeReference<List<Sessions>>(){});
    }

    public void save(Sessions session) throws IOException {
        List<Sessions> sessions = findAll();
        sessions.add(session);
        jsonUtils.writeList(sessionFilePath,sessions);
    }

    public Optional<Sessions> findByToken(String token) throws IOException{
        return findAll().stream()
                .filter(s -> s.getToken().equals(token))
                .findFirst();
    }

    public void deleteToken(String token) throws IOException{
        List<Sessions> sessions = findAll();
        sessions.removeIf(s -> s.getToken().equals(token));
        jsonUtils.writeList(sessionFilePath, sessions);
    }
}
