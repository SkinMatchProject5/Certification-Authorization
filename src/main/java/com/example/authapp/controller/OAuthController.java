package com.example.authapp.controller;

import com.example.authapp.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {

    /**
     * OAuth 로그인 URL 제공
     */
    @GetMapping("/url/{provider}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getOAuthUrl(@PathVariable String provider) {
        try {
            String baseUrl = "http://localhost:8081";  // 올바른 포트로 수정
            String loginUrl = baseUrl + "/oauth2/authorization/" + provider.toLowerCase();
            
            Map<String, String> response = new HashMap<>();
            response.put("provider", provider.toLowerCase());
            response.put("loginUrl", loginUrl);
            response.put("url", loginUrl);  // 프론트엔드에서 기대하는 필드 추가
            
            log.info("OAuth URL requested for provider: {}", provider);
            
            return ResponseEntity.ok(
                ApiResponse.success(provider + " OAuth URL", response)
            );
        } catch (Exception e) {
            log.error("Error getting OAuth URL for provider: {}", provider, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("OAuth URL 생성에 실패했습니다.", e.getMessage()));
        }
    }

    /**
     * 지원하는 OAuth 제공자 목록
     */
    @GetMapping("/providers")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOAuthProviders() {
        Map<String, Object> providers = new HashMap<>();
        
        // Google
        Map<String, String> google = new HashMap<>();
        google.put("name", "Google");
        google.put("url", "/api/oauth/url/google");
        google.put("available", "true");
        
        // Naver
        Map<String, String> naver = new HashMap<>();
        naver.put("name", "Naver");
        naver.put("url", "/api/oauth/url/naver");
        naver.put("available", "true");
        
        providers.put("google", google);
        providers.put("naver", naver);
        
        return ResponseEntity.ok(
            ApiResponse.success("지원하는 OAuth 제공자 목록", providers)
        );
    }
}
