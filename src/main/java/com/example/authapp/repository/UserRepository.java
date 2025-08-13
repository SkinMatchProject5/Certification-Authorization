package com.example.authapp.repository;

import com.example.authapp.entity.Provider;
import com.example.authapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 제공자와 제공자 ID로 사용자 조회 (OAuth 로그인 시 사용)
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 제공자와 제공자 ID 존재 여부 확인
    boolean existsByProviderAndProviderId(Provider provider, String providerId);

    // 특정 제공자로 가입한 사용자들 조회
    @Query("SELECT u FROM User u WHERE u.provider = :provider")
    java.util.List<User> findAllByProvider(@Param("provider") Provider provider);

    // 이메일과 제공자로 사용자 조회 (동일 이메일, 다른 제공자 처리용)
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.provider = :provider")
    Optional<User> findByEmailAndProvider(@Param("email") String email, @Param("provider") Provider provider);
}