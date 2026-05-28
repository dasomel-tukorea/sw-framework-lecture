-- schema.sql — Docker Compose 초기 스키마 (MySQL 8.x)
-- Week 12 — Docker 컨테이너화 배포 (Lab 05: 앱 + DB)
--
-- docker-compose.yml 의 db 서비스가 이 파일을
-- /docker-entrypoint-initdb.d/ 에 마운트하여 "최초 1회" 자동 실행한다.
-- (volumes 의 db-data 가 비어 있을 때만 실행됨)
--
-- 도메인: swframework 의 student(학생) + member(회원) — W09~W11 과 동일.
--   · board/users 테이블이 아니라 student/member 다 (게시판 도메인 아님).
--   · 테이블만 생성하고, 시드 데이터는 앱의 DataInitializer 가
--     기동 시 student 33건 + member(BCrypt 해시) 를 채운다.

CREATE DATABASE IF NOT EXISTS swframework
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE swframework;

-- ============================================
-- 학생 테이블 (CRUD·페이징·검색·정렬 대상)
-- Week 09 — MyBatis 마이그레이션 / Week 11 — 첨부파일 컬럼 추가
-- ============================================
CREATE TABLE IF NOT EXISTS student (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '학생 PK',
    name            VARCHAR(100) NOT NULL                COMMENT '이름',
    student_id      VARCHAR(20)  NOT NULL                COMMENT '학번',
    email           VARCHAR(200) NULL                    COMMENT '이메일',
    major           VARCHAR(100) NULL                    COMMENT '전공',
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    attachment_name VARCHAR(255) NULL                    COMMENT '원본 첨부파일명 (W11 Lab04)',
    saved_name      VARCHAR(255) NULL                    COMMENT '서버 저장 파일명 (UUID, W11 Lab04)',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='학생 테이블';

-- ============================================
-- 회원 테이블 (로그인/회원가입 — HashMap → DB 영구 저장)
-- Week 07 — 세션 로그인 / Week 11 Lab05 — member 테이블 마이그레이션
-- password 는 BCrypt 해시(60자)이므로 VARCHAR(255). 평문 저장 금지.
-- ============================================
CREATE TABLE IF NOT EXISTS member (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '회원 PK',
    login_id   VARCHAR(50)  NOT NULL UNIQUE         COMMENT '로그인 아이디',
    password   VARCHAR(255) NOT NULL                COMMENT 'BCrypt 해시 (W07 PasswordUtil)',
    name       VARCHAR(50)  NOT NULL                COMMENT '이름',
    email      VARCHAR(100) NULL                    COMMENT '이메일 (선택)',
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '권한 (USER/ADMIN)',
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회원 테이블';

-- ── 시드 데이터 안내 ─────────────────────────────────────────
-- 학생/회원 초기 데이터는 앱 기동 시 DataInitializer 가 자동 삽입한다.
--   · student 33건 (페이징 10건/페이지 동작 확인용)
--   · member  admin/guest (PasswordUtil.encode 로 BCrypt 해시 저장)
-- 따라서 여기서 평문 비밀번호로 INSERT 하지 않는다.
-- (SQL 로 직접 회원을 넣으려면 반드시 BCrypt 해시 문자열을 넣어야
--  PasswordUtil.matches() 로그인 검증이 통과한다.)
