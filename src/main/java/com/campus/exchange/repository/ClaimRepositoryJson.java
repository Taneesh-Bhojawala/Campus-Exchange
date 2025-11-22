package com.campus.exchange.repository;

import com.campus.exchange.config.AppProperties;
import com.campus.exchange.model.Claim;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClaimRepositoryJson {
    private JsonUtils jsonUtils;
    private String filePath;
    private TypeReference<List<Claim>> typeRef = new TypeReference<>() {};

    public ClaimRepositoryJson(JsonUtils jsonUtils,AppProperties props){
        this.jsonUtils = jsonUtils;
        this.filePath = Paths.get(props.getDataFolder(),"claims.json").toString();
    }

    public List<Claim> findAll(){
        return jsonUtils.readList(filePath,typeRef);
    }

    public Optional<Claim> findByID(String claimID){
        List<Claim> claimList = findAll();
        for(Claim claim: claimList){
            if(claim.getClaimID().equals(claimID)){
                return Optional.of(claim);
            }
        }
        return Optional.empty();
    }
    public List<Claim> findByItemID(String itemID){
        List<Claim> claimList = findAll();
        List<Claim> result = new ArrayList<>();
        for(Claim claim: claimList){
            if(claim.getClaimID().equals(itemID)){
                result.add(claim);
            }
        }
        return result;
    }
    public List<Claim> findByClaimerId(String claimerId) {
        List<Claim> result = new ArrayList<>();
        List<Claim> all = findAll();
        for (Claim c : all) {
            if (c.getClaimerID().equals(claimerId)) {
                result.add(c);
            }
        }
        return result;
    }

    public void save(Claim claim) {
        List<Claim> claimList = findAll();
        if (claim.getClaimID() == null || claim.getClaimID().isEmpty()) {
            claim.setClaimID(UUID.randomUUID().toString());
        }

        claimList.add(claim);
        jsonUtils.writeList(filePath, claimList);
    }

    public void updateClaimList(Claim claim){
        List<Claim> claimList = findAll();
        int i = 0;
        boolean visited = false;
        for(Claim claim1:claimList){
            if(claim.getClaimID().equals(claim1.getClaimID())){
                claimList.set(i,claim);
                visited = true;
                break;
            }
            i++;
        }
        if(!visited) {
            claimList.add(claim);
        }
        jsonUtils.writeList(filePath,claimList);
    }

    public void deleteByID(String claimID){
        List<Claim> all = findAll();
        all.removeIf(c -> c.getClaimID().equals(claimID));
        jsonUtils.writeList(filePath, all);
    }
}
