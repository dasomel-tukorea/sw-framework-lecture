-- data.sql — H2 인메모리 DB 초기 데이터
-- Week 09 — MyBatis CRUD
-- 경로: src/main/resources/data.sql
-- spring.sql.init.mode: always 설정 시 앱 시작 때 자동 실행

INSERT INTO student (name, email, major) VALUES ('홍길동', 'hong@tukorea.ac.kr', 'IT경영전공');
INSERT INTO student (name, email, major) VALUES ('김철수', 'kim@tukorea.ac.kr', 'IT경영전공');
INSERT INTO student (name, email, major) VALUES ('이영희', 'lee@tukorea.ac.kr', 'IT경영전공');
