package com.example.userserver.domain.dto;

import com.example.userserver.domain.User;
import com.example.userserver.domain.UserType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private UserType userType;
    private String password; // only populated when you really need it (e.g. debugging)

    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .password(user.getPassword())   // include the hashed password
                .build();
    }

    public static User toEntity(UserDTO dto) {
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .userType(dto.getUserType())
                .password(dto.getPassword())
                .build();
    }
}
