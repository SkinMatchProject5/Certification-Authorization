package com.example.authapp.dto.response;

import com.example.authapp.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserProfileResponse {
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String profileImage;
    private String gender;
    private String birthYear;
    private String nationality;
    private String allergies;
    private String surgicalHistory;
    private String provider;
    private String role;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .gender(user.getGender())
                .birthYear(user.getBirthYear())
                .nationality(user.getNationality())
                .allergies(user.getAllergies())
                .surgicalHistory(user.getSurgicalHistory())
                .provider(user.getProvider() != null ? user.getProvider().getValue() : "REGULAR")
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}