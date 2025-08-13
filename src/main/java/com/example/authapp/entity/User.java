package com.example.authapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    private String nickname;

    private String profileImage;

    private String gender;

    @Column(name = "birth_year")
    private String birthYear;

    private String nationality;

    @Column(length = 1000)
    private String allergies;

    @Column(name = "surgical_history", length = 1000)
    private String surgicalHistory;

    @Column(name = "password")
    private String password; // 일반 회원가입용 비밀번호

    private String address; // 주소 필드 추가

    @Enumerated(EnumType.STRING)
    private Provider provider; // nullable로 변경 (일반 회원가입은 null)

    @Column(name = "provider_id")
    private String providerId; // nullable로 변경

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Builder
    public User(String email, String name, String nickname, String profileImage,
                String password, String address,
                String gender, String birthYear, String nationality, 
                String allergies, String surgicalHistory,
                Provider provider, String providerId, Role role) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.password = password;
        this.address = address;
        this.gender = gender;
        this.birthYear = birthYear;
        this.nationality = nationality;
        this.allergies = allergies;
        this.surgicalHistory = surgicalHistory;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role != null ? role : Role.USER;
    }

    // 일반 회원가입용 사용자 생성
    public static User createRegularUser(String email, String name, String password, String address) {
        return User.builder()
                .email(email)
                .name(name)
                .password(password)
                .address(address)
                .provider(null) // 일반 회원가입은 provider가 null
                .providerId(null)
                .role(Role.USER)
                .build();
    }

    // 사용자 정보 업데이트 (전체)
    public void updateProfile(String name, String nickname, String profileImage,
                            String gender, String birthYear, String nationality,
                            String allergies, String surgicalHistory) {
        if (name != null) this.name = name;
        if (nickname != null) this.nickname = nickname;
        if (profileImage != null) this.profileImage = profileImage;
        if (gender != null) this.gender = gender;
        if (birthYear != null) this.birthYear = birthYear;
        if (nationality != null) this.nationality = nationality;
        if (allergies != null) this.allergies = allergies;
        if (surgicalHistory != null) this.surgicalHistory = surgicalHistory;
    }

    // 기본 사용자 정보 업데이트 (기존 메서드 유지)
    public void updateBasicProfile(String name, String profileImage) {
        if (name != null) this.name = name;
        if (profileImage != null) this.profileImage = profileImage;
    }

    // OAuth 제공자별 사용자 생성 팩토리 메서드
    public static User createGoogleUser(String email, String name, String profileImage, String providerId) {
        return User.builder()
                .email(email)
                .name(name)
                .profileImage(profileImage)
                .provider(Provider.GOOGLE)
                .providerId(providerId)
                .role(Role.USER)
                .build();
    }

    public static User createNaverUser(String email, String name, String profileImage, String providerId) {
        return User.builder()
                .email(email)
                .name(name)
                .profileImage(profileImage)
                .provider(Provider.NAVER)
                .providerId(providerId)
                .role(Role.USER)
                .build();
    }

}