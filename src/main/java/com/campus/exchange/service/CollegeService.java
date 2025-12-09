package com.campus.exchange.service;

import com.campus.exchange.config.AppProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollegeService {
    private Path filePath;
    private final CustomLogger logger;
    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<List<String>> typeReference = new TypeReference<List<String>>(){};
    public CollegeService(AppProperties props, CustomLogger logger){
        this.filePath = Path.of(props.getDataFolder(),"colleges.json");
        this.logger = logger;
    }
    public List<String> getAll() throws Exception{
        //check if the file path exists or not
        // At the top
        logger.log("CollegeService", "Reading college list from file");
        if(!Files.exists(filePath)){
            throw new Exception("colleges.json does not exist");
        }
        // then return the list of colleges
        List<String> list = mapper.readValue(filePath.toFile(),typeReference);
        return list;
    }
    public boolean checkListedCollege(String collegeName) throws Exception{
        //convert all the college names to lowercase with no whitecase characters
        String modifiedCollege = collegeName.toLowerCase().trim().replaceAll("\\s+","");
        List<String>colleges = getAll();
        for(String college:colleges){
            if(modifiedCollege.equals(college.toLowerCase().trim().replaceAll("\\s+",""))){
                return true;
            }
        }
        return false;
    }
}
