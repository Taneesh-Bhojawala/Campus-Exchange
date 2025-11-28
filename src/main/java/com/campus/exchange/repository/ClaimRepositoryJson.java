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
            if(claim.getClaimId().equals(claimID)){
                return Optional.of(claim);
            }
        }
        return Optional.empty();
    }
    public Optional<Claim> findByItemId(String itemID){
        List<Claim> claimList = findAll();
        for(Claim claim: claimList){
            if(claim.getItemId().equals(itemID)){
                return Optional.of(claim);
            }
        }
        return Optional.empty();
    }
    public List<Claim> findByClaimerId(String claimerId) {
        List<Claim> result = new ArrayList<>();
        List<Claim> all = findAll();
        for (Claim c : all) {
            if (c.getClaimerId().equals(claimerId)) {
                result.add(c);
            }
        }
        return result;
    }

    public void save(Claim claim) {
        List<Claim> claimList = findAll();
        if (claim.getClaimId() == null || claim.getClaimId().isEmpty()) {
            claim.setClaimId(UUID.randomUUID().toString());
        }

        claimList.add(claim);
        jsonUtils.writeList(filePath, claimList);
    }

    public void updateClaimList(Claim claim){
        List<Claim> claimList = findAll();
        int i = 0;
        boolean visited = false;
        for(Claim claim1:claimList){
            if(claim.getClaimId().equals(claim1.getClaimId())){
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
        all.removeIf(c -> c.getClaimId().equals(claimID));
        jsonUtils.writeList(filePath, all);
    }
}
