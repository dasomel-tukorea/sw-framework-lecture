# 9주차 핵심 용어집

처음 접하는 개념은 **비유**로 감을 잡고, **정확한 정의**로 마무리.

---

## 1. JDBC (Java Database Connectivity)

> "표준 운전면허 — DB 회사가 달라도 같은 운전법으로 운전 가능"

Java 표준 API로, DB 종류(MySQL·Oracle·PostgreSQL)와 상관없이 **공통 인터페이스**로 접근하게 해준다. 단, 매번 Connection·PreparedStatement·ResultSet을 직접 다루기 때문에 **반복 코드가 길다**.

```
Connection → PreparedStatement → execute → ResultSet → 매핑 → 자원 해제
```

> 통증: 자원 해제(`close()`) 누락 → 커넥션 풀 고갈 → **운영 첫 장애**

---

## 2. MyBatis

> "운전기사 — 운전 면허(JDBC)는 그대로지만, 운전은 알아서 해줌"

JDBC 위에 **SQL Mapper 계층**을 얹은 프레임워크. SQL을 XML에 분리하고, Java 메서드와 1:1 매핑한다. 자원 해제·결과 매핑·예외 처리를 자동화.

```
Java: mapper.findById(id);       ← 1줄
XML:  <select id="findById" resultType="Student">
        SELECT * FROM student WHERE id = #{id}
      </select>
```

---

## 3. SqlSessionFactory · SqlSession

| 용어 | 비유 | 정의 |
|---|---|---|
| **SqlSessionFactory** | 출장 기사 파견 회사 | DB 연결 정보를 보관하고 SqlSession을 발급. 앱 전체에서 1개. |
| **SqlSession** | 출장 기사 1명 | 한 트랜잭션 동안 SQL을 실행하는 단위. ThreadLocal 자원. |

> Spring Boot가 `@Transactional` AOP로 **자동 생성·반환** — 우리는 인터페이스만 호출.

---

## 4. Mapper Interface · Mapper XML

> "전화번호부와 통화" — Interface는 번호(메서드 이름), XML은 통화 내용(SQL)

| 항목 | Interface | XML |
|---|---|---|
| 위치 | `mapper/StudentMapper.java` | `resources/mapper/StudentMapper.xml` |
| 역할 | 호출 창구 | SQL 정의 |
| 매핑 키 | 메서드 이름 | `id` 속성 |
| 매핑 규칙 | namespace = FQCN | namespace = Interface 풀 경로 |

> namespace 한 글자만 틀려도 **`Invalid bound statement`** 예외.

---

## 5. 커넥션 풀 (DBCP, Database Connection Pool)

> "택시 정거장 — 빈 택시를 미리 세워두고 손님이 오면 즉시 보냄"

DB 연결을 매 요청마다 만들면 비용이 크다(TCP 핸드셰이크·인증). **앱 시작 시 N개를 미리 만들어 풀에 보관**하고, 요청 시 즉시 대여 → 사용 후 풀로 반환.

| 단계 | 설명 |
|---|---|
| ① 풀 초기화 | 앱 시작 시 기본 10개 (HikariCP 기준) |
| ② 요청 도착 | Controller → Service 진입 |
| ③ 커넥션 대여 | 풀에서 유휴 커넥션 가져옴 |
| ④ SQL 실행 | PreparedStatement |
| ⑤ 풀로 반환 | `close()`는 종료가 아니라 **반환** |

> 풀 고갈 = 사용자가 보는 첫 번째 장애. 모니터링 필수.

---

## 6. HikariCP

Spring Boot **2.x부터 기본 채택**된 커넥션 풀 구현체. 가볍고 빠름. 별도 설정 없이 동작.

```yaml
spring:
  datasource:
    url: jdbc:mysql://...
    # → Spring Boot가 HikariCP를 자동 구성, 기본 풀 크기 10
```

---

## 7. PreparedStatement vs Statement

> "공항 보안검색 — Statement는 통과시켜 보고, Prepared는 미리 정해진 모양만 통과"

| 항목 | Statement | PreparedStatement |
|---|---|---|
| 방식 | 문자열 결합 | `?` 위치 바인딩 |
| 보안 | SQL Injection 노출 | 자동 이스케이프 (안전) |
| 성능 | 매번 SQL 파싱 | 컴파일 결과 캐시 |

```java
// ❌ 위험: 입력값이 SQL의 일부가 됨
"SELECT * FROM student WHERE name='" + input + "'"
// 입력 "'; DROP TABLE student; --" 시 → 테이블 삭제

// ✓ 안전: 입력값은 데이터로만 처리
"SELECT * FROM student WHERE name = ?" + setString(1, input)
```

> **MyBatis 매핑**: `#{}` = PreparedStatement(안전) / `${}` = 문자열 치환(위험)

---

## 8. `#{}` vs `${}`

| 표기 | 의미 | 사용 시점 |
|---|---|---|
| `#{name}` | PreparedStatement의 `?`로 변환 | **사용자 입력 → 무조건 이쪽** |
| `${name}` | 문자열 그대로 치환 | 동적 컬럼명·정렬 키워드 한정 |

> "헷갈리면 `#{}` 써라. `${}`는 코드에서 만든 화이트리스트만."

---

## 9. Spring Profile

> "옷장 — 같은 사람이 상황(개발/운영)에 맞춰 옷 갈아입기"

**같은 코드 + 다른 yml**로 환경별 설정을 분리. 실행 옵션으로 활성 프로필을 지정.

```bash
./gradlew bootRun --args='--spring.profiles.active=h2'    # H2 메모리
./gradlew bootRun --args='--spring.profiles.active=mysql' # MySQL 운영
```

> 운영 비밀번호는 yml에 두지 않고 **환경변수**(`${DB_PASSWORD}`)로 주입.

---

## 10. H2 Database

> "임시 칠판 — 앱 종료 시 데이터 사라짐, 빠르게 시작·종료"

Java 인메모리 DB. 별도 설치 불필요. 개발·테스트·CI 환경에 최적.

| 특징 | 설명 |
|---|---|
| 메모리 모드 | `jdbc:h2:mem:swframework` — 종료 시 초기화 |
| MySQL 호환 | `MODE=MYSQL` 옵션으로 SQL 호환성 보장 |
| Web Console | `/h2-console` 엔드포인트로 GUI 확인 |

---

## 11. ERD (Entity Relationship Diagram)

> "건물 설계도 — 시공 전에 모두가 같은 그림을 본다"

테이블 간 관계를 시각화하는 DB 설계 도구. **개발자·DBA·기획자가 공유하는 공용 언어**.

| Mermaid | 의미 | 예시 |
|---|---|---|
| `||--||` | 1:1 | 회원 ↔ 프로필 |
| `||--o{` | 1:N | 회원(1) ↔ 게시글(N) |
| `}o--o{` | N:M | 학생 ↔ 강좌 (중간 테이블) |

---

## 12. Domain · DTO · Entity

| 용어 | 역할 | 예시 |
|---|---|---|
| **Domain** | DB 테이블과 매핑되는 객체 | `Student.java` |
| **DTO** (Data Transfer Object) | 계층 간 데이터 전달 객체 | `BoardDTO.java` (Form/View용) |
| **Entity** | (JPA 용어) 영속 컨텍스트 관리 객체 | 본 강의에선 사용 안 함 |

> 작은 프로젝트는 Domain만으로 충분. **DTO 분리는 Form·API 응답이 복잡할 때**.

---

## 13. PRG 패턴 (Post-Redirect-Get)

> "은행 ATM — 출금 누른 뒤 새로고침해도 또 출금되지 않게"

POST 처리 후 **302 Redirect**로 GET을 유도. 새로고침 시 POST 재전송을 방지.

```
POST /students  (등록)
   ↓
302 Redirect
   ↓
GET /students   (목록)   ← 새로고침해도 GET만 재실행
```

---

## 14. `@Transactional`

> "왕복 비행 — 가는 길에 사고나면 자동으로 출발지로 돌아옴"

메서드 진입 시 트랜잭션 시작, 종료 시 commit. **언체크드 예외 발생 시 자동 rollback**.

| 속성 | 기본값 | 의미 |
|---|---|---|
| `readOnly` | `false` | true 시 잠금 X · dirty checking 비활성 |
| `propagation` | `REQUIRED` | 기존 트랜잭션 합류 |
| `rollbackFor` | `RuntimeException` | 체크드 예외도 롤백하려면 명시 |

> Spring AOP 프록시가 동작 (W05 연결).

---

## 15. 1:10:100 법칙

| 단계 | 비용 |
|---|---|
| 분석 단계 발견 | 1 |
| 구현 단계 발견 | 10 |
| 배포 후 발견 | 100 |

> 오늘 ERD 한 줄을 잘못 그으면 W14에서 100배 비싸진다.
