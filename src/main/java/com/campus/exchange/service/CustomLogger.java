package com.campus.exchange.service;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CustomLogger {
    // The file will be created in your project root folder
    private static final String LOG_FILE = "my_dev_logs.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * It writes a message to the text file.
     * synchronized is used so two services don't write at the exact same time and mess up the text.
     */
    public synchronized void log(String sourceClass, String message) {
        // Try-with-resources automatically closes the file after writing
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            String time = LocalDateTime.now().format(formatter);
            // Format: [2025-12-06 14:00:00] [AuthService] : User logged in
            pw.println("[" + time + "] [" + sourceClass + "] : " + message);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}