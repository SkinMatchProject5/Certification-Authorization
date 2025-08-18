# SkinMatch Backend

SkinMatch 프로젝트의 백엔드 서버입니다.

## 🚀 시작하기

### 필수 요구사항
- Java 17 이상
- Gradle 7.0 이상

### 설치 및 실행

1. **프로젝트 클론**
   ```bash
   git clone https://github.com/SkinMatchProject5/skinmatch-back.git
   cd skinmatch-back
   ```

2. **로컬 설정 파일 생성**
   
   `src/main/resources/application-local.yml` 파일을 생성하고 다음 내용을 추가:
   
   ```yaml
   spring:
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
   ```

3. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

4. **접속**
   - 서버: http://localhost:8081

## 🔐 OAuth 설정

### Google OAuth 설정
1. [Google Cloud Console](https://console.cloud.google.com/)에서 프로젝트 생성
2. OAuth 2.0 클라이언트 ID 생성
3. 승인된 리디렉션 URI 추가: `http://localhost:8081/login/oauth2/code/google`

### Naver OAuth 설정
1. [네이버 개발자 센터](https://developers.naver.com/)에서 애플리케이션 등록
2. 서비스 URL: `http://localhost:8081`
3. Callback URL: `http://localhost:8081/login/oauth2/code/naver`

## 🗃️ 데이터베이스

- **개발환경**: MySQL 8.0+
- **접속정보**: 
  - URL: `jdbc:mysql://localhost:3306/skincare_db`
  - 사용자명: `root`
  - 비밀번호: `1234`
  - 데이터베이스: `skincare_db`

### MySQL 설정
1. MySQL 8.0+ 설치
2. 데이터베이스 생성:
   ```sql
   CREATE DATABASE skincare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

## 📡 API 엔드포인트

### 인증
- `POST /api/auth/login` - 로그인
- `POST /api/auth/signup` - 회원가입
- `GET /api/auth/me` - 현재 사용자 정보
- `POST /api/auth/refresh` - 토큰 갱신
- `POST /api/auth/logout` - 로그아웃

### OAuth
- `GET /oauth2/authorization/google` - Google 로그인
- `GET /oauth2/authorization/naver` - Naver 로그인

# 🎯 Skin Story Solver API 상세 명세서

## 📋 프로젝트 정보

| 항목 | 내용 |
|------|------|
| **프로젝트명** | Skin Story Solver |
| **서비스 설명** | AI 기반 피부 분석 및 병원 추천 플랫폼 |
| **API 버전** | v1.0.0 |
| **서버 포트** | 8081 |
| **베이스 URL** | `http://localhost:8081` |
| **문서 버전** | 2025-08-18 |
| **Swagger UI** | `http://localhost:8081/swagger-ui/index.html` |

---

## 🏗️ 시스템 아키텍처

```
📱 Frontend (React)     🔐 Auth Service        📷 Camera Service
   Port: 5173      ←→     Port: 8081      ←→      Port: 8000
                              ↓
                         🗄️ MySQL DB
                           Port: 3306
```

---

## 🔐 인증 시스템

### JWT 토큰 기반 인증
- **Access Token**: 24시간 유효
- **Refresh Token**: 7일 유효
- **헤더 형식**: `Authorization: Bearer {access_token}`

### 지원하는 로그인 방식
1. **일반 로그인**: 이메일 + 비밀번호
2. **소셜 로그인**: Google OAuth, Naver OAuth

---

## 📊 API 엔드포인트 목록

### 🔑 Authentication (인증 관련)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| POST | `/api/auth/signup` | 회원가입 | ❌ |
| POST | `/api/auth/login` | 로그인 | ❌ |
| POST | `/api/auth/refresh` | 토큰 재발급 | ❌ |
| POST | `/api/auth/logout` | 로그아웃 | ❌ |
| GET | `/api/auth/me` | 현재 사용자 정보 조회 | ✅ |
| POST | `/api/auth/validate` | 토큰 유효성 검증 | ✅ |
| GET | `/api/auth/oauth/{provider}` | OAuth 로그인 URL 조회 | ❌ |

### 🔗 OAuth (소셜 로그인)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| GET | `/api/oauth/providers` | 지원 OAuth 제공자 목록 | ❌ |
| GET | `/api/oauth/url/{provider}` | OAuth 로그인 URL 조회 | ❌ |

### 👤 User Management (사용자 관리)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|-----------|
| GET | `/api/users/profile` | 프로필 조회 | ✅ |
| GET | `/api/users/{userId}` | 특정 사용자 조회 (관리자) | ✅ |
| PUT | `/api/users/profile` | 프로필 전체 업데이트 | ✅ |
| PUT | `/api/users/profile/basic` | 기본 정보 업데이트 | ✅ |

---

## 📝 상세 API 명세

### 🔑 Authentication APIs

#### 1. 회원가입
```http
POST /api/auth/signup
Content-Type: application/json
```

**요청 Body:**
```json
{
  "username": "홍길동",
  "email": "hongildong@example.com",
  "password": "password123!",
  "confirmPassword": "password123!",
  "address": "서울특별시 강남구 테헤란로 123"
}
```

**응답:**
```json
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "data": {
    "id": 1,
    "email": "hongildong@example.com",
    "name": "홍길동",
    "role": "USER",
    "provider": "LOCAL",
    "createdAt": "2025-08-18T10:30:00Z"
  }
}
```

**유효성 검사:**
- `username`: 2-20자, 필수
- `email`: 유효한 이메일 형식, 필수
- `password`: 6-50자, 필수
- `confirmPassword`: password와 일치, 필수
- `address`: 선택사항

**에러 응답:**
```json
{
  "success": false,
  "message": "회원가입에 실패했습니다.",
  "error": "이미 존재하는 이메일입니다."
}
```

---

#### 2. 로그인
```http
POST /api/auth/login
Content-Type: application/json
```

**요청 Body:**
```json
{
  "email": "hongildong@example.com",
  "password": "password123!"
}
```

**응답:**
```json
{
  "success": true,
  "message": "로그인이 완료되었습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "email": "hongildong@example.com",
      "name": "홍길동",
      "role": "USER",
      "provider": "LOCAL"
    }
  }
}
```

---

#### 3. 토큰 재발급
```http
POST /api/auth/refresh
Content-Type: application/json
```

**요청 Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**응답:**
```json
{
  "success": true,
  "message": "토큰이 재발급되었습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400
  }
}
```

---

#### 4. 로그아웃
```http
POST /api/auth/logout
Content-Type: application/json
```

**요청 Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**응답:**
```json
{
  "success": true,
  "message": "로그아웃되었습니다."
}
```

---

#### 5. 현재 사용자 정보 조회
```http
GET /api/auth/me
Authorization: Bearer {accessToken}
```

**응답:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "hongildong@example.com",
    "name": "홍길동",
    "nickname": "길동이",
    "profileImage": "https://example.com/profile/1.jpg",
    "role": "USER",
    "provider": "LOCAL",
    "gender": "MALE",
    "birthYear": "1990",
    "nationality": "KR",
    "allergies": "땅콩, 갑각류",
    "surgicalHistory": "없음",
    "createdAt": "2025-08-18T10:30:00Z",
    "updatedAt": "2025-08-18T10:30:00Z"
  }
}
```

---

#### 6. 토큰 유효성 검증
```http
POST /api/auth/validate
Authorization: Bearer {accessToken}
```

**응답:**
```json
{
  "success": true,
  "message": "토큰 검증 완료",
  "data": true
}
```

---

### 🔗 OAuth APIs

#### 1. OAuth 제공자 목록 조회
```http
GET /api/oauth/providers
```

**응답:**
```json
{
  "success": true,
  "message": "지원하는 OAuth 제공자 목록",
  "data": {
    "google": {
      "name": "Google",
      "url": "/api/oauth/url/google",
      "available": "true"
    },
    "naver": {
      "name": "Naver",
      "url": "/api/oauth/url/naver",
      "available": "true"
    }
  }
}
```

---

#### 2. OAuth 로그인 URL 조회
```http
GET /api/oauth/url/{provider}
```

**Path Parameters:**
- `provider`: `google` | `naver`

**응답:**
```json
{
  "success": true,
  "message": "google OAuth URL",
  "data": {
    "provider": "google",
    "loginUrl": "http://localhost:8081/oauth2/authorization/google",
    "url": "http://localhost:8081/oauth2/authorization/google"
  }
}
```

**OAuth 로그인 플로우:**
1. 클라이언트가 이 URL로 사용자를 리다이렉트
2. 사용자가 OAuth 제공자에서 로그인
3. 제공자가 콜백 URL로 인증 코드 전송
4. 서버가 토큰 발급 후 프론트엔드로 리다이렉트

---

### 👤 User Management APIs

#### 1. 프로필 조회
```http
GET /api/users/profile
Authorization: Bearer {accessToken}
```

**응답:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "hongildong@example.com",
    "name": "홍길동",
    "nickname": "길동이",
    "profileImage": "https://example.com/profile/1.jpg",
    "role": "USER",
    "provider": "LOCAL",
    "gender": "MALE",
    "birthYear": "1990",
    "nationality": "KR",
    "allergies": "땅콩, 갑각류",
    "surgicalHistory": "없음",
    "createdAt": "2025-08-18T10:30:00Z",
    "updatedAt": "2025-08-18T10:30:00Z"
  }
}
```

---

#### 2. 특정 사용자 조회 (관리자용)
```http
GET /api/users/{userId}
Authorization: Bearer {accessToken}
```

**Path Parameters:**
- `userId`: 사용자 ID (숫자)

**권한**: ADMIN 역할만 접근 가능

**응답:** 위와 동일

---

#### 3. 프로필 전체 업데이트
```http
PUT /api/users/profile
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**요청 Body:**
```json
{
  "name": "홍길동",
  "nickname": "새길동",
  "profileImage": "https://example.com/new-profile.jpg",
  "gender": "MALE",
  "birthYear": "1990",
  "nationality": "KR",
  "allergies": "새우, 땅콩",
  "surgicalHistory": "2023년 피부 레이저 시술"
}
```

**응답:**
```json
{
  "success": true,
  "message": "프로필이 업데이트되었습니다.",
  "data": {
    // 업데이트된 사용자 정보
  }
}
```

---

#### 4. 기본 정보 업데이트
```http
PUT /api/users/profile/basic?name=새이름&profileImage=https://new-image.jpg
Authorization: Bearer {accessToken}
```

**Query Parameters:**
- `name`: 사용자 이름 (선택)
- `profileImage`: 프로필 이미지 URL (선택)

---

## 📊 공통 응답 형식

### ✅ 성공 응답
```json
{
  "success": true,
  "message": "성공 메시지",
  "data": {
    // 응답 데이터
  }
}
```

### ❌ 실패 응답
```json
{
  "success": false,
  "message": "에러 개요",
  "error": "상세 에러 메시지"
}
```

---

## 🚦 HTTP 상태 코드

| 코드 | 의미 | 사용 상황 |
|------|------|-----------|
| **200** | OK | 요청 성공 |
| **201** | Created | 리소스 생성 성공 |
| **400** | Bad Request | 잘못된 요청 (유효성 검사 실패) |
| **401** | Unauthorized | 인증 실패 (토큰 없음/만료) |
| **403** | Forbidden | 권한 없음 |
| **404** | Not Found | 리소스를 찾을 수 없음 |
| **409** | Conflict | 리소스 중복 (이미 존재하는 이메일) |
| **500** | Internal Server Error | 서버 내부 오류 |

---

## 🔧 개발 환경 설정

### Backend 기술 스택
```yaml
Java: 21
Spring Boot: 3.3.5
SpringDoc OpenAPI: 2.5.0
JWT Library: io.jsonwebtoken:jjwt-api:0.12.6
Database: MySQL 8.0
Port: 8081
```

### 환경 변수
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/skincare_db
    username: root
    password: 1234
  
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
          naver:
            client-id: ${NAVER_CLIENT_ID}
            client-secret: ${NAVER_CLIENT_SECRET}

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400  # 24시간
  refresh-expiration: 604800  # 7일
```

---

## 🧪 테스트 가이드

### Postman 컬렉션 사용법

1. **회원가입 테스트**
   ```bash
   POST http://localhost:8081/api/auth/signup
   # Body에 회원가입 정보 입력
   ```

2. **로그인 후 토큰 저장**
   ```bash
   POST http://localhost:8081/api/auth/login
   # 응답에서 accessToken을 환경변수로 저장
   ```

3. **인증이 필요한 API 테스트**
   ```bash
   GET http://localhost:8081/api/auth/me
   Authorization: Bearer {{accessToken}}
   ```

### 테스트 시나리오

#### 🔄 일반 로그인 플로우
1. 회원가입 → 2. 로그인 → 3. 프로필 조회 → 4. 프로필 수정 → 5. 로그아웃

#### 🔗 OAuth 로그인 플로우
1. OAuth URL 조회 → 2. 브라우저에서 OAuth 로그인 → 3. 콜백 처리 → 4. 토큰 발급

#### 🔄 토큰 갱신 플로우
1. 로그인 → 2. 토큰 만료 대기 → 3. 토큰 재발급 → 4. 새 토큰으로 API 호출

---

## 🚨 주의사항 및 제한사항

### 보안
- 🔒 모든 비밀번호는 BCrypt로 암호화 저장
- 🔒 JWT Secret Key는 256비트 이상 권장
- 🔒 HTTPS 환경에서만 운영 (프로덕션)
- 🔒 CORS 설정으로 허용된 도메인만 접근 가능

### 제한사항
- 📊 이메일 중복 불가
- 📊 사용자명 2-20자 제한
- 📊 비밀번호 6-50자 제한
- 📊 토큰 만료시간: Access Token 24시간, Refresh Token 7일

### 에러 처리
- 🔍 모든 API 응답은 동일한 형식 사용
- 🔍 에러 발생 시 로그에 상세 정보 기록
- 🔍 클라이언트에는 보안상 민감한 정보 제외하고 응답

---

## 📝 업데이트 이력

| 버전 | 날짜 | 담당자 | 변경사항 |
|------|------|--------|----------|
| v1.0.0 | 2025-08-18 | Backend Team | 초기 API 명세서 작성 |

---

## 🔗 관련 링크

- **Swagger UI**: http://localhost:8081/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs
- **Health Check**: http://localhost:8081/actuator/health
- **GitHub Repository**: [프로젝트 저장소 링크]

---

## 🛡️ 보안 설정

- JWT 기반 인증
- CORS 설정으로 프론트엔드 연동 지원
- OAuth 2.0 소셜 로그인 지원 (Google, Naver)

## 📝 환경변수

프로덕션 환경에서는 다음 환경변수를 설정하세요:

```bash
export GOOGLE_CLIENT_ID=your_google_client_id
export GOOGLE_CLIENT_SECRET=your_google_client_secret
export NAVER_CLIENT_ID=your_naver_client_id
export NAVER_CLIENT_SECRET=your_naver_client_secret
export JWT_SECRET=your_jwt_secret_key
```

## 🔧 개발자 정보

- Spring Boot 3.x
- Spring Security 6.x
- Spring Data JPA
- MySQL 8.0+
- JWT Authentication
- OAuth 2.0