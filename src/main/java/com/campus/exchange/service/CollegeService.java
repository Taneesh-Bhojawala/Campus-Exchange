package com.campus.exchange.service;

import com.campus.exchange.config.AppProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<List<String>> typeReference = new TypeReference<List<String>>(){};
}
