package com.example.authapp.controller;

import com.example.authapp.dto.request.UpdateProfileRequest;
import com.example.authapp.dto.response.ApiResponse;
import com.example.authapp.dto.response.UserProfileResponse;
import com.example.authapp.entity.User;
import com.example.authapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 프로필 조회
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal User user) {
        try {
            UserProfileResponse profile = UserProfileResponse.from(user);
            return ResponseEntity.ok(ApiResponse.success(profile));
        } catch (Exception e) {
            log.error("Get user profile failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("프로필 조회에 실패했습니다.", e.getMessage()));
        }
    }

    /**
     * 특정 사용자 ID로 프로필 조회 (관리자용)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            UserProfileResponse profile = UserProfileResponse.from(user);
            return ResponseEntity.ok(ApiResponse.success(profile));
        } catch (Exception e) {
            log.error("Get user by id failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("사용자 조회에 실패했습니다.", e.getMessage()));
        }
    }

    /**
     * 사용자 정보 업데이트 (전체 프로필)
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileRequest request) {
        try {
            User updatedUser = userService.updateUserProfile(user.getId(), request);
            
            UserProfileResponse profile = UserProfileResponse.from(updatedUser);
            return ResponseEntity.ok(ApiResponse.success("프로필이 업데이트되었습니다.", profile));
        } catch (Exception e) {
            log.error("Update user profile failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("프로필 업데이트에 실패했습니다.", e.getMessage()));
        }
    }

    /**
     * 사용자 기본 정보 업데이트 (기존 API 유지)
     */
    @PutMapping("/profile/basic")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateBasicProfile(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String profileImage) {
        try {
            User updatedUser = userService.updateUser(
                    user.getId(),
                    name != null ? name : user.getName(),
                    profileImage != null ? profileImage : user.getProfileImage()
            );
            
            UserProfileResponse profile = UserProfileResponse.from(updatedUser);
            return ResponseEntity.ok(ApiResponse.success("프로필이 업데이트되었습니다.", profile));
        } catch (Exception e) {
            log.error("Update user profile failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("프로필 업데이트에 실패했습니다.", e.getMessage()));
        }
    }
}
