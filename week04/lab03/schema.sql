-- src/main/resources/schema.sql
-- H2 프로파일에서 서버 시작 시 자동 실행 (application-h2.yml의 sql.init.mode: always)
-- MySQL에서는 실행되지 않음 (sql.init.mode: never)

CREATE TABLE IF NOT EXISTS student (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(50) NOT NULL,
    major VARCHAR(50)
);

-- 테스트용 초기 데이터
INSERT INTO student (name, major) VALUES ('홍길동', 'IT경영');
INSERT INTO student (name, major) VALUES ('김영희', '컴퓨터공학');
INSERT INTO student (name, major) VALUES ('이철수', '정보통신');
