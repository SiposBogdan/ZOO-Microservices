package com.example.userserver.service;

import com.example.userserver.domain.User;
import com.example.userserver.domain.UserType;
import com.example.userserver.domain.dto.UserDTO;
import com.example.userserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    /*** READ ***/
//    public List<UserDTO> getAll() {
//        return userRepository.findAll().stream()
//                .map(UserDTO::fromEntity)
//                .collect(Collectors.toList());
//    }
    public List<User> getAll(String type) {
        if (type == null || type.isBlank()) {
            return userRepository.findAll();
        }

        UserType userType;
        try {
            userType = UserType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid user type: " + type,
                    e
            );
        }

        return userRepository.findByUserType(userType);
    }

    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    /*** CREATE ***/
    public UserDTO create(UserDTO dto) {
        User u = UserDTO.toEntity(dto);
        String raw = dto.getPassword();
        u.setPassword(passwordEncoder.encode(raw));

        if (u.getUserType() == null) {
            u.setUserType(UserType.VISITOR);
        }

        User saved = userRepository.save(u);
        notificationService.notifyNewUser(saved, raw);
        return UserDTO.fromEntity(saved);
    }

    /*** UPDATE ***/
    public UserDTO update(Long id, UserDTO dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setUserType(dto.getUserType());

        // handle optional password change
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            String raw = dto.getPassword();
            existing.setPassword(passwordEncoder.encode(raw));
            notificationService.notifyOnUpdate(existing, true, raw);
        } else {
            notificationService.notifyOnUpdate(existing, false, null);
        }

        User saved = userRepository.save(existing);
        return UserDTO.fromEntity(saved);
    }

    /*** DELETE ***/
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    /*** CHANGE PASSWORD ***/
    public void changePassword(Long id, String newPassword) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);
        notificationService.notifyOnUpdate(u, true, newPassword);
    }

    /*** AUTHENTICATE (for non-JWT fallback) ***/
    public UserDTO authenticate(String username, String rawPassword) {
        User u = userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        return UserDTO.fromEntity(u);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByType(UserType type) {
        return userRepository.findByUserType(type);
    }

    @Transactional
    public void notifyUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Folosim notifyOnUpdate cu passwordChanged=false pentru a trimite
        // notificarea standard de update (poți ajusta textul în NotificationService)
        notificationService.notifyOnUpdate(user, false, null);
    }




}
