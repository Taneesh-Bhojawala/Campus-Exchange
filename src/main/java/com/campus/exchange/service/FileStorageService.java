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

    public FileStorageService(AppProperties props) {
        try {
            this.uploadDir = Paths.get(props.getUploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(this.uploadDir);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize upload directory", e);
        }
    }

    /**
     * Store uploaded image and return public path (e.g. "/uploads/uuid.jpg")
     */
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

            return "/uploads/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    /**
     * Delete stored file using public path or filename.
     */
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

            return Files.deleteIfExists(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    /**
     * Load file as a Resource for download.
     */
    public Resource loadAsResource(String fileName) {
        try {
            if (fileName == null || fileName.isBlank()) {
                throw new RuntimeException("Filename empty");
            }

            String clean = Paths.get(fileName).getFileName().toString();
            Path file = uploadDir.resolve(clean).normalize();

            if (!file.getParent().equals(uploadDir)) {
                throw new RuntimeException("Invalid file access");
            }

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + clean);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL loading file", e);
        } catch (Exception e) {
            throw new RuntimeException("Could not load file", e);
        }
    }

    /**
     * Get absolute path of stored file.
     */
    public Path getAbsolutePath(String fileName) {
        if (fileName == null) return null;
        String clean = Paths.get(fileName).getFileName().toString();
        return uploadDir.resolve(clean).normalize();
    }
}
