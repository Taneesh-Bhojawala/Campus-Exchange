package com.campus.exchange.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
* Loads custom configuration values from application.properties using the prefix "app".
 */

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /*
    Path to folders where all data files are stored (acting as database)
     */
    private String dataFolder;
    /*
    Directory where images uploaded by users will be stored (for item listings)
     */
    private String uploadDir;

    /*
    getters and setters
     */
    public String getDataFolder() {
        return dataFolder;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setDataFolder(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
