package com.example.userserver.controller;

import com.example.userserver.domain.User;
import com.example.userserver.domain.UserType;
import com.example.userserver.domain.dto.UserDTO;
import com.example.userserver.service.CSVExporter;
import com.example.userserver.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final CSVExporter csvExporter;

    @Data
    public static class PasswordUpdateRequest {
        private String newPassword;
    }

    /*** READ ***/
//    @GetMapping
//    public List<UserDTO> getAll() {
//        return userService.getAll();
//    }
    @GetMapping(value = {"", "/"})
    public List<UserDTO> getAll(@RequestParam(required = false) String type) {
        return userService.getAll(type)
                .stream()
                .map(UserDTO::fromEntity)
                .toList();
    }
//    public List<UserDTO> getAll(@RequestParam(required = false) String type) {
//        if (type != null) {
//            try {
//                UserType userType = UserType.valueOf(type.toUpperCase());
//                return userService.findByType(userType)
//                        .stream()
//                        .map(UserDTO::fromEntity)
//                        .toList();
//            } catch (IllegalArgumentException e) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user type: " + type);
//            }
//        } else {
//            return userService.findAll()
//                    .stream()
//                    .map(UserDTO::fromEntity)
//                    .toList();
//        }
//    }



    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /*** CREATE ***/
    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) {
        UserDTO created = userService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    /*** UPDATE ***/
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(
            @PathVariable Long id,
            @RequestBody UserDTO dto
    ) {
        try {
            return ResponseEntity.ok(userService.update(id, dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /*** DELETE ***/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(value = "type", required = false) UserType type) {
        byte[] data = csvExporter.exportUsersToCsv(type);
        String filename = "users" + (type != null ? "_" + type.name() : "") + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(data);
    }

    /*** CHANGE PASSWORD ***/
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody PasswordUpdateRequest req
    ) {
        try {
            userService.changePassword(id, req.getNewPassword());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/notify")
    public ResponseEntity<Void> notifyUser(@PathVariable Long id) {
        userService.notifyUser(id);
        return ResponseEntity.noContent().build();
    }

}
