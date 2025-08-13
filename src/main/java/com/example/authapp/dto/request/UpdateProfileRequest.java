package com.example.authapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    private String name;
    private String nickname;
    private String profileImage;
    private String gender;
    private String birthYear;
    private String nationality;
    private String allergies;
    private String surgicalHistory;
}