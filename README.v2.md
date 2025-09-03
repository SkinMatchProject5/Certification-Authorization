# SkinMatch 인증/인가 백엔드 (Certification-Authorization)

SkinMatch 프로젝트의 인증(Authentication)·인가(Authorization) 백엔드 서비스입니다. JWT 기반 인증과 OAuth 2.0 소셜 로그인(Google, Naver)을 제공하며, 관리자용 통계/사용자 관리 API를 포함합니다.

## 요구 사항
- Java 17+
- Gradle 8.x
- MySQL 8.0+

## 빠른 시작
1) 소스 가져오기
```bash
git clone <이 저장소 URL>
cd Certification-Authorization
```

2) DB 생성 (로컬 예시)
```sql
CREATE DATABASE skincare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3) 로컬 설정 파일 생성
- `src/main/resources/application-local.yml`를 만들고 다음 값을 환경에 맞게 채웁니다.
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/skincare_db?serverTimezone=UTC&characterEncoding=utf8
    username: root
    password: your_password
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
          naver:
            client-id: YOUR_NAVER_CLIENT_ID
            client-secret: YOUR_NAVER_CLIENT_SECRET

jwt:
  secret: YOUR_JWT_SECRET_KEY_HERE_256_BITS_OR_MORE
server:
  port: 8081
```

4) 애플리케이션 실행
```bash
./gradlew bootRun
```

5) 접속
- 서버: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`

## 환경 변수(예시)
```bash
export GOOGLE_CLIENT_ID=your_google_client_id
export GOOGLE_CLIENT_SECRET=your_google_client_secret
export NAVER_CLIENT_ID=your_naver_client_id
export NAVER_CLIENT_SECRET=your_naver_client_secret
export JWT_SECRET=your_jwt_secret_key
export SPRING_PROFILES_ACTIVE=local   # prod에서는 dev 컨트롤러 비활성화
```

---

## API 명세서

아래 명세는 실제 컨트롤러/DTO 기준으로 정리되었습니다. 공통적으로 응답은 `ApiResponse<T>` 래퍼로 반환됩니다.

- 성공: `{ "success": true, "message": string, "data": T }`
- 실패: `{ "success": false, "message": string, "error": string }`

### 인증 Authentication (`/api/auth`)

- POST `/api/auth/signup` 회원가입
  - Body(JSON): `SignupRequest`
    - `username`(string, required)
    - `nickname`(string, optional)
    - `email`(string, required)
    - `password`(string, required)
    - `confirmPassword`(string, required)
    - `address`(string, optional)
  - Response: `ApiResponse<UserProfileResponse>`

- POST `/api/auth/login` 로그인
  - Body(JSON): `LoginRequest`
    - `loginId`(string, required)  예: `user123` 또는 `user@example.com`
    - `password`(string, required)
  - Response: `ApiResponse<LoginResponse>`
    - `accessToken`(string), `refreshToken`(string), `user`(id, email, name, profileImage, provider, role)

- POST `/api/auth/refresh` 토큰 재발급
  - Body(JSON): `TokenRequest` { `refreshToken`: string }
  - Response: `ApiResponse<TokenInfo>`
    - `accessToken`, `refreshToken`, `accessTokenExpiresIn`, `refreshTokenExpiresIn`

- POST `/api/auth/logout` 로그아웃 (리프레시 토큰 무효화)
  - Body(JSON): `TokenRequest` { `refreshToken`: string }
  - Response: `ApiResponse<Void>`

- GET `/api/auth/me` 내 정보 조회
  - Auth: Bearer JWT
  - Response: `ApiResponse<UserProfileResponse>`

- POST `/api/auth/validate` 토큰 유효성 검사
  - Header: `Authorization: Bearer <token>`
  - Response: `ApiResponse<boolean>`

- GET `/api/auth/oauth/{provider}` OAuth 로그인 엔드포인트 링크 제공
  - Path: `provider` in [`google`, `naver`]
  - Response: `ApiResponse<string>` (예: `/oauth2/authorization/google`)

### OAuth (`/api/oauth`)

- GET `/api/oauth/providers` 지원 OAuth 제공자 목록
  - Response: `ApiResponse<Map>`
    - 예: `{ google: { name, url, available }, naver: { ... } }`

- GET `/api/oauth/url/{provider}` OAuth 로그인 URL 조회
  - Path: `provider` in [`google`, `naver`]
  - Response: `ApiResponse<{ provider, loginUrl, url }>`

### 사용자 User (`/api/users`) [JWT 필요]

- GET `/api/users/profile` 내 프로필 조회
  - Response: `ApiResponse<UserProfileResponse>`

- GET `/api/users/{userId}` 특정 사용자 조회
  - Response: `ApiResponse<UserProfileResponse>`

- PUT `/api/users/profile` 프로필 업데이트(이미지 포함)
  - Content-Type: `multipart/form-data`
  - Fields: `name`(string), `nickname`(string), `gender`(string), `birthYear`(string), `nationality`(string), `profileImage`(file, optional)
  - Response: `ApiResponse<UserProfileResponse>`

- PUT `/api/users/profile/basic` 기본 정보 업데이트
  - Params: `name`(string, optional), `profileImage`(string, optional)
  - Response: `ApiResponse<UserProfileResponse>`

### 관리자 Admin (`/api/admin`) [JWT + ROLE_ADMIN]

- GET `/api/admin/stats` 관리자 통계 조회
  - Response: `ApiResponse<AdminStatsResponse>`
    - `totalUsers`, `onlineUsers`, `recentlyActiveUsers`, `newUsersToday`, `totalAnalyses`, `analysesToday`

- GET `/api/admin/users` 사용자 목록 조회(검색/페이징)
  - Query: `page`(default 0), `size`(default 20), `search`(string), `status`(all|active|inactive), `sortBy`(default createdAt), `sortDirection`(asc|desc)
  - Response: `ApiResponse<Page<UserProfileResponse>>`

- PATCH `/api/admin/users/{userId}/status` 사용자 활성/비활성 토글
  - Response: `ApiResponse<UserProfileResponse>`

- DELETE `/api/admin/users/{userId}` 사용자 삭제
  - Response: `ApiResponse<Void>`

- PUT `/api/admin/users/{userId}/profile-image` 사용자 프로필 이미지 변경
  - Content-Type: `multipart/form-data`
  - Field: `profileImage`(file, required)
  - Response: `ApiResponse<UserProfileResponse>`

### 개발용 Dev (`/api/dev`) [프로필 `!prod`에서만 활성]

- GET `/api/dev/users` 모든 사용자 요약 목록
- POST `/api/dev/create-admin?email=` 지정 이메일을 관리자 승격(없으면 임시 관리자 생성)
- POST `/api/dev/promote-user/{userId}` 사용자 관리자 승격
- POST `/api/dev/create-default-admin` 기본 관리자 생성 (`admin@skincarestory.com` / `admin123`)
- POST `/api/dev/create-test-user` 테스트 사용자 생성
- POST `/api/dev/fix-online-status` 온라인 상태/분석 카운트 정합성 정리

---

## DB 스키마

아래는 실제 엔티티와 `database-schema/user-schema.sql` 기준 요약입니다.

### 테이블: `users`
- `id` BIGINT PK, AUTO_INCREMENT
- `email` VARCHAR(255) NOT NULL UNIQUE
- `username` VARCHAR(255) UNIQUE
- `name` VARCHAR(255) NOT NULL
- `nickname` VARCHAR(255)
- `profile_image` VARCHAR(255)
- `gender` VARCHAR(50)
- `birth_year` VARCHAR(4)
- `nationality` VARCHAR(100)
- `password` VARCHAR(255)
- `address` VARCHAR(500)
- `provider` ENUM('GOOGLE','NAVER') NULL
- `provider_id` VARCHAR(255)
- `role` ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER'
- `active` BOOLEAN NOT NULL DEFAULT TRUE
- `last_login_at` TIMESTAMP NULL
- `is_online` BOOLEAN NOT NULL DEFAULT FALSE
- `analysis_count` INT NOT NULL DEFAULT 0
- `last_analysis_at` TIMESTAMP NULL
- `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- Indexes: `(email)`, `(username)`, `(provider, provider_id)`, `(active)`, `(is_online)`, `(last_login_at)`, `(analysis_count)`, `(last_analysis_at)`, `(created_at)`

### 테이블: `refresh_tokens`
- `id` BIGINT PK, AUTO_INCREMENT
- `token` VARCHAR(500) NOT NULL UNIQUE
- `user_id` BIGINT NOT NULL FK → `users(id)` ON DELETE CASCADE
- `expires_at` TIMESTAMP NOT NULL
- `created_at` TIMESTAMP NOT NULL (기본: NOW)
- Indexes: `(token)`, `(user_id)`, `(expires_at)`

### 테이블: `uploaded_files`
- `id` BIGINT PK, AUTO_INCREMENT
- `user_id` BIGINT NULL FK → `users(id)` ON DELETE SET NULL
- `original_filename` VARCHAR(255) NOT NULL
- `stored_filename` VARCHAR(255) NOT NULL
- `file_path` VARCHAR(500) NOT NULL
- `file_size` BIGINT NOT NULL
- `content_type` VARCHAR(100)
- `file_type` ENUM('PROFILE_IMAGE','ANALYSIS_IMAGE','GENERAL') NOT NULL DEFAULT 'GENERAL'
- `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- Indexes: `(user_id)`, `(file_type)`, `(created_at)`

### 테이블: `skin_analysis_results`
- `id` BIGINT PK, AUTO_INCREMENT
- `user_id` BIGINT NOT NULL FK → `users(id)` ON DELETE CASCADE
- `analysis_type` ENUM('CAMERA','SURVEY','AI_ANALYSIS') NOT NULL
- `image_file_id` BIGINT NULL FK → `uploaded_files(id)` ON DELETE SET NULL
- `result_data` JSON
- `confidence_score` DECIMAL(5,4)
- `analysis_duration_ms` INT
- `model_version` VARCHAR(50)
- `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- Indexes: `(user_id)`, `(analysis_type)`, `(created_at)`

### 뷰/프로시저(옵션)
- `admin_stats_view`: 사용자 수/온라인 수/최근활동/일간 신규/분석 집계 등 요약
- 사용자 상태 관리 및 통계 갱신용 저장 프로시저 예시 포함 (`UpdateUserLoginStatus`, `UpdateUserLogoutStatus`, `UpdateUserAnalysisStats`, `CleanupInactiveUsers`)

---

## 보안/권한
- 인증: JWT Bearer (`Authorization: Bearer <access_token>`)
- 권한: `ROLE_ADMIN`만 관리자 API 접근 가능
- Dev API: `@Profile("!prod")` 환경에서만 활성화
- CORS/Swagger: `SecurityConfig`, `SwaggerConfig` 기반 설정

## 기술 스택
- Spring Boot 3.x, Spring Security 6.x + JWT
- Spring Data JPA (MySQL)
- OAuth 2.0 (Google, Naver)
- SpringDoc OpenAPI 3 (Swagger UI)

## 운영 팁
- 리프레시 토큰은 DB(`refresh_tokens`)에 저장/검증됩니다. 탈취 위험 방지를 위해 HTTPS와 짧은 액세스 토큰 만료를 권장합니다.
- 프로필 이미지 업로드 시 저장소(FileStorageService) 구현에 맞게 퍼블릭 URL을 프런트로 전달합니다.
- 관리자 목록 조회는 검색어/상태/정렬·페이징을 지원합니다.

