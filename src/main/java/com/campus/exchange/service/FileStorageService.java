package com.campus.exchange.service;

import com.campus.exchange.config.AppProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

/**
 * FileStorageService (NO IOException in method signatures)
 *
 * Functions:
 *  Save uploaded images to a directory
 *  Generate unique filenames (UUID)
 *  Return "/uploads/filename.ext" public path
 *  Provide file deletion + load-as-resource
 *
 * All errors throw RuntimeException
 */
@Service
public class FileStorageService {

    private final Path uploadDir;
    private final CustomLogger logger;

    public FileStorageService(AppProperties props, CustomLogger logger) {
        this.logger = logger;
        try {
            this.uploadDir = Paths.get(props.getUploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(this.uploadDir);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize upload directory", e);
        }
    }

    //This stores the image uploaded and returns the public path
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            original = Paths.get(original).getFileName().toString();

            String ext = "";
            int idx = original.lastIndexOf('.');
            if (idx >= 0) ext = original.substring(idx);

            String filename = UUID.randomUUID().toString() + ext;

            Path target = uploadDir.resolve(filename).normalize();
            if (!target.getParent().equals(uploadDir)) {
                throw new RuntimeException("Invalid file path");
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
//            System.out.println("[FileStorageService] Image uploaded successfully");
            logger.log("FileStorageService", "Image uploaded successfully");

            return "/uploads/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    //Deletes stored file using path name
    public boolean delete(String publicOrFileName) {
        if (publicOrFileName == null || publicOrFileName.isBlank()) return false;

        try {

            String fileName = publicOrFileName;
            if (fileName.startsWith("/")) {
                int idx = fileName.lastIndexOf('/');
                fileName = fileName.substring(idx + 1);
            }

            Path target = uploadDir.resolve(fileName).normalize();

            if (!target.getParent().equals(uploadDir)) {
                throw new RuntimeException("Invalid file delete request");
            }
            logger.log("FileStorageService", "file deleted: " + fileName);
            return Files.deleteIfExists(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

}
