# Week 04 — IoC/DI 실습 코드

## 실습 구성

| 실습 | 주제 | 핵심 개념 |
|---|---|---|
| lab01 | 인터페이스 기반 DI | 생성자 주입, @Service, 인터페이스 DI (**week03/lab3 확장**) |
| lab02 | @Primary로 구현체 전환 | @Primary, 동일 인터페이스 다중 구현체, 기본 빈 지정 |
| lab03 | Profile별 DB 설정 분리 | spring.profiles.active, H2/MySQL 환경 전환 |

> ♻️ = week03과 동일, ⚡ = week03에서 확장/변경, 🆕 = 신규

## lab01 — 인터페이스 기반 DI (실습 1, 30분)

> week03/lab3에서 구현한 `GreetingService`(구체 클래스) + `GreetingController`를
> **인터페이스 기반 DI**로 확장한 실습입니다.
> - week03: `GreetingService`가 구체 클래스 → 구현 교체 시 Controller 수정 필요
> - week04: `GreetingService`를 인터페이스로 분리 + `KoreanGreetingService` 구현체 생성

```
lab01/
├── GreetingService.java           ← 인터페이스 정의 (service/)               ⚡ week03/lab3 → 인터페이스로 변경
├── KoreanGreetingService.java     ← @Service 자동 등록 (service/)            🆕 신규
├── GreetingController.java        ← 인터페이스 기반 생성자 주입 (controller/) ⚡ week03/lab3 → 인터페이스 타입으로 변경
└── greeting.html                  ← 인사 페이지 템플릿 (templates/)           ♻️ week03/lab3과 동일
```

**테스트**: `http://localhost:8080/greeting?name=홍길동`

### 핵심 학습 포인트
1. `GreetingService` 인터페이스 타입으로 DI → 구현체 교체 시 Controller 코드 변경 없음
2. 생성자 주입 + `final` 필드로 불변성 보장
3. 생성자 1개 → `@Autowired` 생략 가능 (Spring Boot 3.x)

## lab02 — @Primary로 구현체 전환 (실습 2 일부, 15분)

> lab01에 `EnglishGreetingService`를 추가하고 `@Primary`로 기본 주입 대상을 전환하는 실습입니다.
> Controller 코드 변경 없이 어노테이션 하나로 구현체가 교체되는 것을 체험합니다.

```
lab02/
└── EnglishGreetingService.java    ← @Service + @Primary (service/)   🆕 신규 (lab01에 추가하여 사용)
```

**테스트**: lab01의 `GreetingController` 그대로 사용 — `http://localhost:8080/greeting?name=홍길동` (영어 인사로 변경됨)

### 핵심 학습 포인트
1. `@Primary`로 동일 인터페이스의 기본 빈 지정
2. Controller 코드 수정 없이 `@Primary` 어노테이션만으로 구현체 교체
3. `@Primary` 위치를 Korean ↔ English 간 옮기며 결과 변화 체험

### 도전: @Qualifier로 특정 구현체 선택
```java
public GreetingController(@Qualifier("koreanGreetingService") GreetingService svc)
```
- `@Primary`보다 `@Qualifier`가 우선!
- 빈 이름 규칙: 클래스명 첫 글자를 소문자로 변환 (예: `KoreanGreetingService` → `"koreanGreetingService"`)

## lab03 — Profile별 DB 설정 분리 (실습 2 일부, 25분)

```
lab03/
├── application.yml                ← 공통 설정 (resources/)                     ⚡ week03/lab4 확장 (프로파일 추가)
├── application-h2.yml             ← H2 인메모리 DB 설정 (resources/)            🆕 신규
├── application-mysql.yml          ← MySQL DB 설정 (resources/)                🆕 신규
└── schema.sql                     ← H2 초기 스키마 + 테스트 데이터 (resources/)    ⚡ week03/lab4 확장 (초기 데이터 추가)
```

### MySQL 사전 준비

MySQL 프로파일로 실행하려면 로컬에 MySQL이 설치되어 있어야 합니다.

```sql
-- MySQL 접속 후 DB 생성
CREATE DATABASE lecture_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

> H2는 인메모리 DB이므로 별도 설치 없이 바로 사용 가능합니다.

### 설정 파일 예시

**application.yml** — 공통 설정 (기본 프로파일: h2)
```yaml
spring:
  profiles:
    active: h2          # 기본값 — --args 없이 실행하면 H2 사용
```

**application-h2.yml**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true      # http://localhost:8080/h2-console 에서 확인 가능
  sql:
    init:
      mode: always       # schema.sql 자동 실행
```

**application-mysql.yml**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lecture_db
    username: root
    password: 1234       # 본인 MySQL 비밀번호로 변경
    driver-class-name: com.mysql.cj.jdbc.Driver
  sql:
    init:
      mode: never        # MySQL은 schema.sql 자동 실행 안 함
  jpa:
    hibernate:
      ddl-auto: update   # 테이블 자동 생성/갱신
```

### build.gradle

라이브러리 등록
```groovy
runtimeOnly 'com.h2database:h2'
runtimeOnly 'com.mysql:mysql-connector-j'
```

### schema.sql 호환성 참고

`schema.sql`은 H2 프로파일 전용입니다. MySQL 프로파일에서는 `sql.init.mode: never`로 비활성화하고, JPA `ddl-auto: update`가 테이블을 자동 생성합니다.
H2 전용 문법(예: `GENERATED ALWAYS AS IDENTITY`)이 포함되어 있으면 MySQL에서 에러가 발생할 수 있으므로 프로파일을 분리하여 관리합니다.

### 테스트
```bash
# H2 프로파일 (기본 — --args 없이도 동작)
./gradlew bootRun
# H2 프로파일 (명시적)
./gradlew bootRun --args='--spring.profiles.active=h2'
# MySQL 프로파일
./gradlew bootRun --args='--spring.profiles.active=mysql'
```

### 핵심 학습 포인트
1. `spring.profiles.active`로 환경별 설정 파일 자동 로드
2. H2(개발) / MySQL(운영) 프로파일 분리로 DB 환경 전환
3. 코드 변경 없이 설정 파일만으로 DB 교체
4. `schema.sql` 호환성 문제를 프로파일 분리로 해결

## 기술 스택

- Java 21
- Spring Boot 3.5.x
- Thymeleaf
- Lombok
- Gradle
- H2 Database (개발/테스트)
- MySQL (운영 환경 실습)
