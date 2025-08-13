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