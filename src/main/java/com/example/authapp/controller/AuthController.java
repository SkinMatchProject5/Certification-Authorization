package com.example.authapp.controller;

import com.example.authapp.dto.request.LoginRequest;
import com.example.authapp.dto.request.SignupRequest;
import com.example.authapp.dto.request.TokenRequest;
import com.example.authapp.dto.response.ApiResponse;
import com.example.authapp.dto.response.LoginResponse;
import com.example.authapp.dto.response.TokenInfo;
import com.example.authapp.dto.response.UserProfileResponse;
import com.example.authapp.entity.User;
import com.example.authapp.service.AuthService;
import com.example.authapp.service.JwtService;
import com.example.authapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * 일반 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserProfileResponse>> signup(@Valid @RequestBody SignupRequest request) {
        try {
            User user = authService.signup(request);
            UserProfileResponse userProfile = UserProfileResponse.from(user);
            return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", userProfile));
        } catch (Exception e) {
            log.error("Signup failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("회원가입에 실패했습니다.", e.getMessage()));
        }
    }

    /**
     * 일반 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = authService.regularLogin(request);
            return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", loginResponse));
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("로그인에 실패했습니다.", e.getMessage()));
        }
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenInfo>> refreshToken(@Valid @RequestBody TokenRequest tokenRequest) {
        try {
            TokenInfo tokenInfo = authService.refreshToken(tokenRequest.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.success("토큰이 재발급되었습니다.", tokenInfo));
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("토큰 재발급에 실패했습니다.", e.getMessage()));
        }
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody TokenRequest tokenRequest) {
        try {
            authService.logout(tokenRequest.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다."));
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("로그아웃에 실패했습니다.", e.getMessage()));
        }
    }

    /**
     * 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
            @AuthenticationPrincipal User user) {
        try {
            UserProfileResponse userProfile = UserProfileResponse.from(user);
            return ResponseEntity.ok(ApiResponse.success(userProfile));
        } catch (Exception e) {
            log.error("Get current user failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("사용자 정보 조회에 실패했습니다.", e.getMessage()));
        }
    }

    /**
     * 토큰 유효성 검증
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = jwtService.extractTokenFromHeader(authHeader);
            
            if (token == null) {
                return ResponseEntity.ok(ApiResponse.success("토큰이 없습니다.", false));
            }
            
            boolean isValid = authService.validateToken(token);
            return ResponseEntity.ok(ApiResponse.success("토큰 검증 완료", isValid));
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success("토큰이 유효하지 않습니다.", false));
        }
    }

    /**
     * OAuth 로그인 링크 제공
     */
    @GetMapping("/oauth/{provider}")
    public ResponseEntity<ApiResponse<String>> getOAuthLoginUrl(@PathVariable String provider) {
        String loginUrl = "/oauth2/authorization/" + provider.toLowerCase();
        return ResponseEntity.ok(ApiResponse.success(provider + " 로그인 URL", loginUrl));
    }
}
