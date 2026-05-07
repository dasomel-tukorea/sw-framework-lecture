-- Week 09 — Lab 03 H2 인메모리 DB용 schema
-- 위치: src/main/resources/sql/schema-h2.sql
-- 실행: ./gradlew bootRun --args='--spring.profiles.active=h2'

DROP TABLE IF EXISTS student;

CREATE TABLE student (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    major      VARCHAR(100),
    -- H2: TIMESTAMP / MySQL: DATETIME — H2 MODE=MYSQL이면 양쪽 호환
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
