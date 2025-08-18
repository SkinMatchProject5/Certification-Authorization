package com.example.authapp.controller;

import com.example.authapp.dto.request.UpdateProfileRequest;
import com.example.authapp.dto.response.ApiResponse;
import com.example.authapp.dto.response.UserProfileResponse;
import com.example.authapp.entity.User;
import com.example.authapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "사용자 관리 관련 API")
@SecurityRequirement(name = "JWT")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "사용자 프로필 조회",
        description = "현재 로그인한 사용자의 프로필 정보 조회"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 조회 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "조회 실패")
    })
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            UserProfileResponse profile = UserProfileResponse.from(user);
            return ResponseEntity.ok(ApiResponse.success(profile));
        } catch (Exception e) {
            log.error("Get user profile failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("프로필 조회에 실패했습니다.", e.getMessage()));
        }
    }

    @Operation(
        summary = "특정 사용자 프로필 조회",
        description = "사용자 ID로 특정 사용자의 프로필 조회 (관리자용)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(
        @Parameter(description = "사용자 ID", example = "1")
        @PathVariable Long userId) {
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

    @Operation(
        summary = "사용자 프로필 업데이트",
        description = "사용자의 전체 프로필 정보 업데이트"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 업데이트 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "업데이트 실패")
    })
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "업데이트할 프로필 정보",
                content = @Content(
                    schema = @Schema(implementation = UpdateProfileRequest.class),
                    examples = @ExampleObject(
                        name = "프로필 업데이트 예시",
                        value = "{\n" +
                               "  \"name\": \"홍길동\",\n" +
                               "  \"nickname\": \"길동이\",\n" +
                               "  \"profileImage\": \"https://example.com/profile.jpg\",\n" +
                               "  \"gender\": \"MALE\",\n" +
                               "  \"birthYear\": \"1990\",\n" +
                               "  \"nationality\": \"KR\",\n" +
                               "  \"allergies\": \"땅콩, 갑각류\",\n" +
                               "  \"surgicalHistory\": \"없음\"\n" +
                               "}"
                    )
                )
            )
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

    @Operation(
        summary = "사용자 기본 정보 업데이트",
        description = "사용자의 기본 정보만 업데이트 (이름, 프로필 이미지)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "기본 정보 업데이트 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "업데이트 실패")
    })
    @PutMapping("/profile/basic")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateBasicProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "사용자 이름", example = "홍길동")
            @RequestParam(required = false) String name,
            @Parameter(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
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
