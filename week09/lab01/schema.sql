-- Week 09 — MyBatis CRUD
-- schema.sql — MySQL 테이블 생성 + 테스트 데이터
-- 프로젝트 경로: 프로젝트 루트 또는 sql/
-- 실행: MySQL Workbench 또는 터미널에서 실행

-- ============================================
-- 데이터베이스 생성 (이미 존재하면 무시)
-- ============================================
CREATE DATABASE IF NOT EXISTS swframework
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE swframework;

-- ============================================
-- 학생 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS student (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    major      VARCHAR(100),
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 테스트 데이터 삽입
-- ============================================
INSERT INTO student (name, email, major) VALUES
    ('홍길동', 'hong@tukorea.ac.kr', 'IT경영전공'),
    ('김철수', 'kim@tukorea.ac.kr', 'IT경영전공'),
    ('이영희', 'lee@tukorea.ac.kr', 'IT경영전공');
