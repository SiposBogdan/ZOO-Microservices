package com.example.userserver.service;

import com.example.userserver.domain.User;
import com.example.userserver.domain.UserType;
import com.example.userserver.repository.UserRepository;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
public class CSVExporter {

    private final UserRepository userRepository;

    public CSVExporter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Exports users (filtered by userType dacă nu este null) în CSV.
     */
    public byte[] exportUsersToCsv(UserType type) {
        List<User> users = (type != null)
                ? userRepository.findByUserType(type)
                : userRepository.findAll();

        try (var baos = new ByteArrayOutputStream();
             var osw = new OutputStreamWriter(baos);
             var writer = new CSVWriter(osw)) {

            // Header
            writer.writeNext(new String[]{"ID", "Username", "Email", "Phone", "UserType"});

            // Rows
            for (User u : users) {
                writer.writeNext(new String[]{
                        String.valueOf(u.getId()),
                        u.getUsername(),
                        u.getEmail(),
                        u.getPhone() != null ? u.getPhone() : "",
                        u.getUserType().name()
                });
            }
            writer.flush();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export users to CSV", e);
        }
    }
}