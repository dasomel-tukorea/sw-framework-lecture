# Lab 04 — H2 프로필 전환

> "팀원마다 다른 환경 → MySQL 없이도 즉시 빌드 가능"

## 파일

| 파일 | 적용 위치 |
|---|---|
| `application-h2.yml` | `src/main/resources/` |
| `application-mysql.yml` | `src/main/resources/` |
| `schema-h2.sql` | `src/main/resources/sql/` |
| `data.sql` | `src/main/resources/sql/` |

## 핵심 개념

> 같은 코드, 다른 yml — Spring Profile = 환경 분리의 표준

```
   Application Code (변하지 않음)
         │
         ├── application-h2.yml     → 메모리 H2 (개발·테스트)
         ├── application-mysql.yml  → MySQL (운영)
         └── application-test.yml   → 단위 테스트 (선택)
```

## 실행

```bash
# H2 모드 (MySQL 설치 불필요)
./gradlew bootRun --args='--spring.profiles.active=h2'

# MySQL 모드
./gradlew bootRun --args='--spring.profiles.active=mysql'
```

## 확인 포인트

- [ ] `http://localhost:8080/h2-console` 접속
  - JDBC URL: `jdbc:h2:mem:swframework`
  - User: `sa` / Password: 공백
- [ ] `SELECT * FROM student` → 3건 표시
- [ ] `http://localhost:8080/students` 목록도 동일하게 동작
- [ ] 종료 후 재실행 시 H2는 데이터 초기화됨 (메모리 모드 특성)

## 트러블슈팅

| 증상 | 원인 | 해결 |
|---|---|---|
| `Table not found` | schema-h2.sql 위치 또는 자동 실행 누락 | `application-h2.yml`의 `spring.sql.init.mode: always` 확인 |
| `H2 Console 404` | h2.console 비활성 | `spring.h2.console.enabled: true` + 재시작 |
| `Syntax error in SQL` | MySQL 전용 구문 사용 | `ENGINE=InnoDB` 제거, `DATETIME` → `TIMESTAMP` |
| `Wrong user/password` | H2 sa 계정 | password 빈값으로 |

## 실무에서

- **로컬 개발**: H2 (빠르게 시작·종료)
- **CI 자동 테스트**: H2 인메모리 (격리된 환경)
- **운영**: MySQL/PostgreSQL (영구 저장 + 복구)
- **운영 DB 비밀번호**: 절대 yml에 평문으로 두지 않음 → 환경변수 또는 Vault 주입

## 보안 한 줄

```yaml
# application-mysql.yml (운영용)
spring:
  datasource:
    password: ${DB_PASSWORD}   # ← 환경변수 주입
```

→ `.gitignore`에 운영용 yml 추가 / GitHub Actions에서 Secret으로 주입.
