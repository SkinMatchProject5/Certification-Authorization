-- =====================================================
-- Skin Story Solver - User Database Schema (Updated)
-- 현재 JPA Entity 기반으로 업데이트된 사용자 데이터베이스 스키마
-- =====================================================

-- 1. 사용자 기본 정보 테이블 (현재 User Entity 기반)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 고유 ID',
    
    -- 기본 인증 정보
    email VARCHAR(255) NOT NULL UNIQUE COMMENT '이메일 (로그인 ID)',
    password VARCHAR(255) COMMENT '암호화된 비밀번호 (OAuth 사용자는 NULL 가능)',
    name VARCHAR(255) NOT NULL COMMENT '사용자 이름',
    nickname VARCHAR(255) COMMENT '닉네임',
    
    -- 프로필 정보
    profile_image VARCHAR(255) COMMENT '프로필 이미지 URL',
    
    -- 개인 정보
    gender VARCHAR(50) COMMENT '성별',
    birth_year VARCHAR(4) COMMENT '출생연도',
    nationality VARCHAR(100) COMMENT '국적',
    address VARCHAR(500) COMMENT '주소',
    
    -- 의료 정보
    allergies VARCHAR(1000) COMMENT '알레르기 정보',
    surgical_history VARCHAR(1000) COMMENT '수술 이력',
    
    -- OAuth 정보
    provider VARCHAR(50) COMMENT 'OAuth 제공자 (GOOGLE, NAVER 등)',
    provider_id VARCHAR(255) COMMENT 'OAuth 제공자의 사용자 ID',
    
    -- 권한
    role VARCHAR(50) NOT NULL DEFAULT 'USER' COMMENT '사용자 권한',
    
    -- 시스템 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    -- 인덱스
    INDEX idx_email (email),
    INDEX idx_provider_provider_id (provider, provider_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 기본 정보';

-- =====================================================
-- 2. Refresh Token 테이블
-- =====================================================

CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    token VARCHAR(500) NOT NULL UNIQUE COMMENT 'Refresh Token',
    expiry_date TIMESTAMP NOT NULL COMMENT '만료 일시',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expiry_date (expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Refresh Token 관리';

-- =====================================================
-- 3. 초기 데이터 삽입 (테스트용)
-- =====================================================

-- 기본 관리자 계정 (비밀번호는 암호화 필요)
-- INSERT INTO users (email, password, name, role, created_at, updated_at) 
-- VALUES ('admin@skincarestory.com', '$2a$10$encrypted_password_here', 'Administrator', 'ADMIN', NOW(), NOW());

-- =====================================================
-- 4. 추가적인 피부 관련 테이블들 (향후 확장용)
-- =====================================================

-- 사용자 피부 프로필 테이블 (향후 추가 예정)
CREATE TABLE user_skin_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    
    -- 피부 타입 정보
    skin_type ENUM('oily', 'dry', 'combination', 'sensitive', 'normal') COMMENT '피부 타입',
    skin_tone ENUM('very_light', 'light', 'medium_light', 'medium', 'medium_dark', 'dark', 'very_dark') COMMENT '피부톤',
    skin_undertone ENUM('cool', 'warm', 'neutral') COMMENT '피부 언더톤',
    
    -- 피부 고민 및 관심사 (JSON 배열)
    skin_concerns JSON COMMENT '피부 고민 (여드름, 색소침착, 주름 등)',
    product_allergies JSON COMMENT '제품 알레르기 정보',
    
    -- 현재 사용 제품 정보
    current_skincare_routine TEXT COMMENT '현재 스킨케어 루틴',
    preferred_brands JSON COMMENT '선호 브랜드 목록',
    
    -- 라이프스타일
    lifestyle_factors JSON COMMENT '라이프스타일 요인 (수면, 스트레스, 운동 등)',
    environmental_factors JSON COMMENT '환경 요인 (기후, 대기오염 등)',
    
    -- 시스템 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_skin_type (skin_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 피부 프로필';

-- 피부 분석 결과 테이블 (향후 추가 예정)
CREATE TABLE skin_analysis_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    
    -- 분석 정보
    analysis_type ENUM('camera', 'survey', 'ai_analysis') NOT NULL COMMENT '분석 유형',
    analysis_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '분석 일시',
    
    -- 분석 결과 (JSON 형태로 저장)
    analysis_results JSON COMMENT '분석 결과 데이터',
    confidence_score DECIMAL(3,2) COMMENT '신뢰도 점수 (0.00-1.00)',
    
    -- 추천 정보
    recommended_products JSON COMMENT '추천 제품 목록',
    recommended_routine TEXT COMMENT '추천 스킨케어 루틴',
    
    -- 이미지 정보 (카메라 분석의 경우)
    image_url VARCHAR(500) COMMENT '분석에 사용된 이미지 URL',
    image_metadata JSON COMMENT '이미지 메타데이터',
    
    -- 시스템 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_analysis_date (analysis_date),
    INDEX idx_analysis_type (analysis_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='피부 분석 결과';

-- 제품 정보 테이블 (향후 추가 예정)
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 기본 제품 정보
    name VARCHAR(255) NOT NULL COMMENT '제품명',
    brand VARCHAR(100) NOT NULL COMMENT '브랜드',
    category VARCHAR(100) NOT NULL COMMENT '카테고리',
    subcategory VARCHAR(100) COMMENT '서브카테고리',
    
    -- 제품 상세 정보
    description TEXT COMMENT '제품 설명',
    ingredients TEXT COMMENT '성분 정보',
    usage_instructions TEXT COMMENT '사용법',
    
    -- 가격 및 구매 정보
    price DECIMAL(10,2) COMMENT '가격',
    currency VARCHAR(3) DEFAULT 'KRW' COMMENT '통화',
    purchase_url VARCHAR(500) COMMENT '구매 링크',
    
    -- 제품 특성
    skin_types JSON COMMENT '적합한 피부 타입 목록',
    skin_concerns JSON COMMENT '해결 가능한 피부 고민 목록',
    
    -- 평점 및 리뷰
    average_rating DECIMAL(2,1) DEFAULT 0.0 COMMENT '평균 평점',
    review_count INT DEFAULT 0 COMMENT '리뷰 수',
    
    -- 이미지
    image_url VARCHAR(500) COMMENT '제품 이미지 URL',
    
    -- 상태
    is_active BOOLEAN DEFAULT TRUE COMMENT '활성 상태',
    is_recommended BOOLEAN DEFAULT FALSE COMMENT '추천 제품 여부',
    
    -- 시스템 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    INDEX idx_brand (brand),
    INDEX idx_category (category),
    INDEX idx_name (name),
    INDEX idx_is_active (is_active),
    INDEX idx_average_rating (average_rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='제품 정보';

-- 사용자 제품 리뷰 테이블 (향후 추가 예정)
CREATE TABLE user_product_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    product_id BIGINT NOT NULL COMMENT '제품 ID',
    
    -- 리뷰 내용
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5) COMMENT '평점 (1-5)',
    title VARCHAR(200) COMMENT '리뷰 제목',
    content TEXT COMMENT '리뷰 내용',
    
    -- 사용 기간 및 효과
    usage_period VARCHAR(50) COMMENT '사용 기간',
    effectiveness_rating INT CHECK (effectiveness_rating >= 1 AND effectiveness_rating <= 5) COMMENT '효과 평점',
    
    -- 추천 여부
    would_recommend BOOLEAN COMMENT '다른 사용자에게 추천 여부',
    
    -- 상태
    is_verified BOOLEAN DEFAULT FALSE COMMENT '인증된 구매자 리뷰 여부',
    is_public BOOLEAN DEFAULT TRUE COMMENT '공개 여부',
    
    -- 시스템 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_product_review (user_id, product_id),
    INDEX idx_product_id (product_id),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 제품 리뷰';

-- =====================================================
-- 5. 뷰(View) 생성
-- =====================================================

-- 사용자 기본 정보 뷰 (민감한 정보 제외)
CREATE VIEW v_user_public_info AS
SELECT 
    id,
    email,
    name,
    nickname,
    profile_image,
    gender,
    nationality,
    provider,
    role,
    created_at
FROM users 
WHERE role != 'DELETED';

-- 사용자 피부 프로필 요약 뷰
CREATE VIEW v_user_skin_summary AS
SELECT 
    u.id as user_id,
    u.name as user_name,
    usp.skin_type,
    usp.skin_tone,
    usp.skin_undertone,
    usp.updated_at as profile_updated_at
FROM users u
LEFT JOIN user_skin_profiles usp ON u.id = usp.user_id
WHERE u.role != 'DELETED';

-- =====================================================
-- 6. 인덱스 최적화
-- =====================================================

-- 복합 인덱스 추가
ALTER TABLE users ADD INDEX idx_provider_email (provider, email);
ALTER TABLE users ADD INDEX idx_role_created (role, created_at);

-- =====================================================
-- 7. 저장 프로시저 (선택사항)
-- =====================================================

DELIMITER //

-- 사용자 통계 조회 프로시저
CREATE PROCEDURE GetUserStatistics()
BEGIN
    SELECT 
        COUNT(*) as total_users,
        COUNT(CASE WHEN provider IS NULL THEN 1 END) as regular_users,
        COUNT(CASE WHEN provider = 'GOOGLE' THEN 1 END) as google_users,
        COUNT(CASE WHEN provider = 'NAVER' THEN 1 END) as naver_users,
        COUNT(CASE WHEN created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as new_users_30days
    FROM users 
    WHERE role != 'DELETED';
END //

DELIMITER ;

-- =====================================================
-- 8. 트리거 (데이터 무결성 보장)
-- =====================================================

DELIMITER //

-- 사용자 삭제 시 관련 데이터 정리 트리거
CREATE TRIGGER before_user_delete
    BEFORE UPDATE ON users
    FOR EACH ROW
BEGIN
    IF NEW.role = 'DELETED' AND OLD.role != 'DELETED' THEN
        SET NEW.email = CONCAT('deleted_', OLD.id, '_', OLD.email);
        SET NEW.updated_at = NOW();
    END IF;
END //

DELIMITER ;

-- =====================================================
-- 설치 및 확인 쿼리
-- =====================================================

-- 테이블 생성 확인
-- SHOW TABLES;

-- 테이블 구조 확인
-- DESCRIBE users;
-- DESCRIBE refresh_token;
-- DESCRIBE user_skin_profiles;

-- 제약조건 확인
-- SELECT * FROM information_schema.TABLE_CONSTRAINTS 
-- WHERE TABLE_SCHEMA = 'skincare_db';
