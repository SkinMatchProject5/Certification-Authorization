package com.example.authapp.service;

import com.example.authapp.dto.oauth.OAuthUserInfo;
import com.example.authapp.dto.request.UpdateProfileRequest;
import com.example.authapp.entity.Provider;
import com.example.authapp.entity.User;
import com.example.authapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 사용자 ID로 조회
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // 이메일로 사용자 조회
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 제공자와 제공자 ID로 사용자 조회
    public Optional<User> findByProviderAndProviderId(Provider provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    // OAuth 사용자 생성 또는 업데이트
    @Transactional
    public User createOrUpdateOAuthUser(OAuthUserInfo oAuthUserInfo) {
        Provider provider = oAuthUserInfo.getProvider();
        String providerId = oAuthUserInfo.getProviderId();
        String email = oAuthUserInfo.getEmail();

        // 1. 제공자 ID로 기존 사용자 조회
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(provider, providerId);

        if (existingUser.isPresent()) {
            // 기존 사용자 정보 업데이트
            User user = existingUser.get();
            user.updateBasicProfile(oAuthUserInfo.getName(), oAuthUserInfo.getProfileImage());
            log.info("Updated existing user: {} from provider: {}", email, provider);
            return userRepository.save(user);
        }

        // 2. Kakao의 경우 가상 이메일 중복 확인 스킵
        if (!email.endsWith("@kakao.local")) {
            Optional<User> userWithSameEmail = userRepository.findByEmail(email);
            if (userWithSameEmail.isPresent()) {
                log.warn("User with same email exists with different provider. Email: {}, Existing provider: {}, New provider: {}",
                        email, userWithSameEmail.get().getProvider(), provider);
                // 여기서는 새로운 사용자로 생성하지만, 비즈니스 로직에 따라 변경 가능
            }
        }

        // 3. 새 사용자 생성
        User newUser = createUserByProvider(provider, oAuthUserInfo);
        User savedUser = userRepository.save(newUser);
        log.info("Created new user: {} from provider: {}", email, provider);

        return savedUser;
    }

    // 제공자별 사용자 생성
    private User createUserByProvider(Provider provider, OAuthUserInfo oAuthUserInfo) {
        return switch (provider) {
            case GOOGLE -> User.createGoogleUser(
                    oAuthUserInfo.getEmail(),
                    oAuthUserInfo.getName(),
                    oAuthUserInfo.getProfileImage(),
                    oAuthUserInfo.getProviderId()
            );
            case NAVER -> User.createNaverUser(
                    oAuthUserInfo.getEmail(),
                    oAuthUserInfo.getName(),
                    oAuthUserInfo.getProfileImage(),
                    oAuthUserInfo.getProviderId()
            );
        };
    }

    // 사용자 정보 업데이트 (기존 - 기본 정보만)
    @Transactional
    public User updateUser(Long userId, String name, String profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        user.updateBasicProfile(name, profileImage);
        return userRepository.save(user);
    }

    // 사용자 전체 프로필 업데이트 (새로운)
    @Transactional
    public User updateUserProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        user.updateProfile(
                request.getName(),
                request.getNickname(),
                request.getProfileImage(),
                request.getGender(),
                request.getBirthYear(),
                request.getNationality(),
                request.getAllergies(),
                request.getSurgicalHistory()
        );
        
        log.info("Updated user profile for user ID: {}", userId);
        return userRepository.save(user);
    }

    // 사용자 존재 여부 확인
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByProviderAndProviderId(Provider provider, String providerId) {
        return userRepository.existsByProviderAndProviderId(provider, providerId);
    }

    // 사용자 저장
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}