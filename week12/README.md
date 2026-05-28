# Week 12 — Docker 컨테이너화 배포 (시연)

> **W11까지 완성된 student 관리 앱(swframework) 을 Docker 컨테이너로 패키징하고 배포한다.**
> 본 주차는 **실습이 아니라 시연(Demonstration)** 이다. 학생은 따라치기보다 **관찰·이해·질문** 에 집중하고, 강사 시연을 노트하며 한 흐름을 체득한다.
>
> **베이스 코드**: `/Users/m/Documents/IdeaProjects/40.tukorea/swframework` — student + member 도메인 (W11까지 완성)
> **참고 데모**: `/Users/m/Documents/IdeaProjects/40.tukorea/sw-framework-demo` — 강사 시연용 완성본

---

## 핵심 메시지 — 코드는 그대로, "실행 환경"만 통째로 패키징

W12 는 애플리케이션 코드를 **단 한 줄도 바꾸지 않는다.** W11까지 완성한 그대로의 `swframework` (student CRUD · 페이징 · 검색 · 정렬 · 파일 업로드 · member BCrypt 로그인) 를 **Dockerfile 한 장** 으로 이미지화하여, "내 컴퓨터에서는 되는데..." 문제를 없애는 것이 목표다.

| 항목 | swframework 현황 (W11까지) | W12 에서 추가 |
|---|---|---|
| 애플리케이션 코드 | student + member 완성 | **변경 없음** |
| 실행 환경 | 각자 PC 의 JDK · MySQL | **Dockerfile 로 JRE + 앱 묶어 이미지화** |
| DB | 로컬 MySQL / H2 | **docker-compose 로 MySQL 컨테이너 동반** |
| 설정 | application.yaml (h2 기본) | **application-docker.yml (env 주입)** |

---

## 4시간 강의 구성 — 한 흐름

| 시간 | 단계 | 내용 |
|---|---|---|
| ~80분 | **PART 1 · 이론** | Why Docker · 아키텍처 · VM vs Container · Dockerfile/Image/Container · 라이프사이클 · 레이어 캐시 · Compose 키워드 |
| ~30분 | **시연 A** | swframework JAR → Dockerfile → build → run → `:8080/students` |
| ~10분 | **시연 B** | `docker ps`·`logs`·`stop`/`start`/`rm` · STATUS 읽는 법 · 트러블슈팅 |
| ~15분 | **시연 C** | `.dockerignore` · `application-docker.yml` · `-e DB_HOST` 환경변수 주입 |
| (보강) | **(심화)** | Multi-stage Build (~290 → ~250MB) · Jib 플러그인 (Dockerfile 없이 빌드) |
| ~25분 | **시연 D** | `docker-compose.yml` 한 장 = 앱 + MySQL 동시 실행 |
| ~15분 | **마무리** | 흔한 실수 종합 · 핵심 5줄 요약 · W13 예고 (CI/CD · GitHub Actions · OSS 라이선스) |

### 파일 위치

- Docker 자산(`Dockerfile`·`Dockerfile.multi`·`docker-compose.yml`·`application-docker.yml`·`sql/schema.sql`·`week12.http`) 은 본 폴더(`week12/`) **루트** 에 있다 — 실제로 프로젝트 루트에서 동작해야 하기 때문이다.
- Jib `build.gradle` 스니펫은 본문 **"(심화) Jib 플러그인"** 섹션에 인라인으로 포함되어 있다.

---

## 8~12주차 연결 지도

```
W08 분석 → W09 ERD → W10 화면·API → W11 구현(페이징·파일·회원)
                                              ↓
                                   W12 배포 ← 이번 주 (시연)
                                   · student 앱을 Docker 이미지로 패키징
                                   · docker-compose 로 앱+DB 동시 실행
                                              ↓
                                   W13 품질·CI/CD · W14·W15 발표
```

---

## 시연 전 사전 점검 (강사·조교 1분 체크)

```bash
docker --version          # Docker version 24.x 이상
docker info               # Server: ... (정상)
java -version             # openjdk 21.x.x
./gradlew --version       # Gradle 8.x · JVM 21
```

> **`Server: ...` 가 보이지 않으면** Docker Desktop 실행 안 됨 → 강의 시작 전에 반드시 띄울 것.

---

# PART 1 · Docker 이론 (~80분)

> 암기가 아니라 **'왜 · 언제 · 어떤 문제'** 를 설명할 수 있어야 한다. 시연(PART 2) 에 들어가기 전 개념 토대.

## 1-1. 왜 Docker 인가 — '내 컴퓨터에서는 되는데...'

| 문제: 환경 차이 | 해결: 통째로 패키징 |
|---|---|
| 개발 PC: Java 21 / MySQL 8.0 | 앱 + 실행환경(JRE · 라이브러리 · 설정)을 하나의 이미지로 묶음 |
| 서버: Java 17 / MySQL 5.7 | 어디서 실행해도 동일 결과 |
| 환경변수 · 라이브러리 버전 불일치 | → **'내 컴퓨터에서 되면 어디서든 된다'** |
| → '배포하면 터지는' 고질적 문제 | 마이크로서비스 · CI/CD 의 기반 |

> 런타임을 표준화해 '환경 차이' 문제를 줄이는 것이 컨테이너화의 출발점이다.

## 1-2. Docker 아키텍처 — Client · Daemon · Registry

```
[Docker Client]  ──REST API──▶  [Docker Daemon]  ◀──pull/push──▶  [Docker Registry]
   CLI · docker                   dockerd                          Docker Hub · 사내
   build / run                    이미지 빌드 · 컨테이너 관리       이미지 저장·공유
```

사용자는 **Client(CLI)** 로 명령만 내리고, 실제 작업은 **Daemon** 이 수행한다. 이미지는 **Registry** 에서 받거나(`pull`) 올린다(`push`).

## 1-3. VM vs Container — 건물 비유

| | VM (가상머신) | Container |
|---|---|---|
| 비유 | 독립 주택 (집마다 OS) | 호텔 객실 (건물 OS 커널 공유) |
| 가상화 수준 | 하드웨어 수준 | 운영체제(OS) 수준 |
| 운영체제 | 독립적인 Guest OS | 호스트 OS 커널 공유 |
| 크기 | 수 GB | 수십~수백 MB |
| 시작 시간 | 수 분 | 수 초 (ms 급) |
| 리소스 사용량 | 높음 (5~10% 오버헤드) | 낮음 (1% 미만) |
| 이식성 | 상대적으로 낮음 | 매우 높음 |

> 한 줄 비유: **VM 은 독립 주택, Docker 는 효율적인 호텔 방.**

## 1-4. 핵심 3요소 — Dockerfile → Image → Container

| 용어 | 무엇 | 비유 |
|---|---|---|
| **Dockerfile** | 이미지를 만드는 명령어 텍스트 파일 | 레시피 |
| **Image** | 앱 + 실행환경이 묶인 **읽기 전용** 템플릿 | 붕어빵 틀 |
| **Container** | 이미지를 실행한 **인스턴스** | 구워진 붕어빵 |

> 한 줄: **Dockerfile(레시피) → `build` → Image(틀) → `run` → Container(제품).**
> 하나의 이미지로 여러 컨테이너 실행 가능. 컨테이너 삭제 시 내부 데이터 소멸(볼륨 미사용 시).

## 1-5. 컨테이너 라이프사이클

```
Created → Running → Paused → Stopped → Removed
docker     docker     docker    docker    docker
create     run/start  pause     stop      rm
```

| 상태 | 명령 | 의미 |
|---|---|---|
| Created | `docker create` | 생성만 됨, 미실행 |
| Running | `docker run` / `start` | 실행 중 |
| Paused | `docker pause` | 일시중단 (메모리 유지). `unpause` 로 복귀 |
| Stopped | `docker stop` | 종료 (파일은 보존, 재시작 가능) |
| Removed | `docker rm` | 완전 삭제 (내부 데이터 소멸) |

> `stop` = 잠시 멈춤 / `rm` = 완전 삭제. 상태를 알면 디버깅이 쉬워진다.

## 1-6. Dockerfile 핵심 명령어 — RUN(빌드) vs ENTRYPOINT(실행) 구분이 핵심

| 명령 | 시점 | 역할 |
|---|---|---|
| `FROM` | 빌드 | 베이스 이미지 (출발점). 예: `eclipse-temurin:21-jre` (JRE 만 → 경량) |
| `WORKDIR` / `COPY` | 빌드 | 작업 디렉토리 + 호스트 → 컨테이너 파일 복사 |
| `RUN` | **빌드** | 빌드 중 실행 (패키지 설치 등). 각 RUN 이 레이어 생성 |
| `ENV` / `EXPOSE` | 빌드/문서 | 환경변수 기본값 / 사용할 포트 명시 |
| `ENTRYPOINT` / `CMD` | **실행** | 컨테이너 시작 시 실행할 메인 명령 |

> **JVM 옵션(`-Dspring...`) 은 반드시 `-jar` 보다 앞에 둔다.**
> `["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]` ← 올바른 순서.
> 순서가 바뀌면 JVM 이 그 값을 JAR 파일명으로 해석해 즉시 `Exited (1)` 로 종료한다.

## 1-7. 레이어 / 캐시 최적화

```
↑ 자주 바뀜(위)        ← Dockerfile 아래쪽
   COPY *.jar app.jar
   COPY build.gradle
   RUN gradlew deps
   WORKDIR /app
   FROM temurin:21-jre
↓ 거의 안 바뀜(아래·캐시)  ← Dockerfile 위쪽
```

- **명령 하나 = 레이어 하나.** 변경분만 저장(증분).
- **위쪽** (베이스·의존성) = 잘 안 바뀌는 것 → 캐시 유지
- **아래쪽** (소스·JAR) = 자주 바뀌는 것 → 캐시 무효화 범위 최소화
- **변경 레이어 "이후" 는 모두 재빌드** → 자주 바뀌는 명령을 아래로

> Multi-stage Build · 의존성 분리 빌드의 출발점이 바로 이 레이어 캐시 원리.

## 1-8. 네트워크 / 스토리지

| 용어 | 무엇 | 주의 |
|---|---|---|
| **포트 매핑** | `-p 8080:8080` = 호스트:컨테이너 | 호스트 포트만 바꿔도 코드 수정 불필요 (`-p 8081:8080`) |
| **컨테이너 내부 localhost** | 컨테이너 **자기 자신** | 호스트 DB 는 `host.docker.internal`, compose 는 서비스명(`db`) |
| **Volume (볼륨)** | 컨테이너 밖 데이터 저장소 | 컨테이너 삭제돼도 데이터 보존 → **DB 컨테이너 필수** |

## 1-9. Docker Compose 키워드 미리보기

| 키 | 의미 |
|---|---|
| `services` | 실행할 컨테이너 목록 (app, db) |
| `image` / `build` | 기존 이미지 vs Dockerfile 빌드 |
| `ports` | 호스트:컨테이너 포트 |
| `environment` | 환경변수 (예: `DB_HOST: db`) |
| `depends_on` | 시작 **순서** (준비 완료 보장은 아님 → healthcheck 필요) |
| `volumes` | 데이터 영속화 |
| `healthcheck` | 컨테이너 '준비 완료' 판정 (예: `SELECT 1`) |

> 명령: `docker compose up -d --build` / `ps` / `logs -f` / `down` / `down -v`

---

# PART 2 · Docker 시연 (swframework)

> W11 student 앱(swframework) 을 — 단계를 쪼개지 않고 **한 흐름** 으로 따라간다.
> 접속 URL 은 `http://localhost:8080/students` (게시판 `/board/list` 가 아님).

## 시연 A — student 앱 컨테이너화 (~30분)

### 3단계 한 흐름

```
JAR 빌드  →  Dockerfile  →  build  →  run  →  /students
gradlew      설계도(파일)    이미지   컨테이너  접속 확인
```

### ① JAR 빌드 — `COPY` 는 "호스트에 이미 있는" JAR 를 복사한다

```bash
./gradlew clean bootJar
# → build/libs/swframework-0.0.1-SNAPSHOT.jar
```

> **자주 보는 실수**: `bootJar` 없이 바로 `docker build` 하면 `COPY failed: *.jar not found`. 반드시 JAR 먼저.

### ② 이미지 빌드 — `-t 이름:태그`, 마지막 `.` 은 빌드 컨텍스트

```bash
docker build -t swframework:1.0 .
```

빌드 로그를 관찰하며 **레이어가 한 줄씩 생성** 되는 모습을 보여준다.
다시 빌드해서 **캐시 적중(`CACHED`)** 으로 빌드가 거의 즉시 끝나는 것도 함께 시연한다.

### ③ 컨테이너 실행 — `-d` 백그라운드, `-p` 호스트:컨테이너, `--name` 이름

```bash
docker run -d -p 8080:8080 --name swframework-app swframework:1.0
```

브라우저: **<http://localhost:8080/students>** (학생 목록 화면).
**`/board/list` 는 W11 도메인에 없다.**

### Dockerfile 내용 (week12/Dockerfile)

```dockerfile
FROM eclipse-temurin:21-jre               # JRE 만 → 이미지 경량
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
```

---

## 시연 B — 확인 · 관리 · 트러블슈팅 (~10분)

### `docker ps` 와 `docker logs` 에서 시작

```bash
docker ps                       # 실행 중 (STATUS=Up ? PORTS=8080->8080 ?)
docker ps -a                    # 중지·종료 포함 전체
docker logs swframework-app     # 컨테이너 로그
docker logs -f swframework-app  # 실시간 로그 (Ctrl+C 종료)
docker inspect swframework-app  # 상태·네트워크·볼륨 상세 (JSON)
```

### STATUS 읽기 (학생 관찰 포인트)

| STATUS | 의미 | 대응 |
|---|---|---|
| `Up 5 minutes` | 정상 실행 (5분째) | OK |
| `Exited (0)` | 정상 종료 | OK |
| `Exited (1)` / `Exited (137)` | 비정상 종료 (오류 / SIGKILL) | `docker logs` 로 원인 확인 |
| `Restarting (N)` | 기동 실패 반복 | `docker logs` 필수 |
| `PORTS: 0.0.0.0:8080->8080/tcp` | 호스트 8080 ↔ 컨테이너 8080 | 정상 매핑 |

### 라이프사이클 명령

```bash
docker stop  swframework-app    # 중지 (파일·상태 보존)
docker start swframework-app    # 재시작
docker rm    swframework-app    # 삭제 (중지 상태에서만)
docker rmi   swframework:1.0    # 이미지 삭제 (사용 중 컨테이너 있으면 불가)
```

### DB 연결이 안 될 때 (시연 C 로 자연스럽게 연결)

> 컨테이너 내부 `localhost` 는 **컨테이너 자기 자신**. 호스트 DB 는 `host.docker.internal`.
> `Exited (1)` 이면 `docker logs` 로 원인 먼저 확인.

---

## 시연 C — 환경 분리 (`.dockerignore` · 프로파일 · 환경변수) (~15분)

### ① `.dockerignore` — 빌드 컨텍스트 다이어트

```gitignore
# .dockerignore (프로젝트 루트)
.git
.idea
*.md
src/                 # JAR 에 이미 포함됨
gradle/  gradlew
build/classes  build/tmp
```

> 효과: 컨텍스트 전송이 수십 MB → 수 MB 로 줄어 빌드 시간이 체감될 만큼 단축.

### ② 프로파일 구조 — 공통 위에 덮어쓰기

```
application.yaml          공통 설정 (기본 profile: h2, mybatis, multipart 10MB)
   └─ application-docker.yml   docker profile 일 때만 datasource override
```

`-Dspring.profiles.active=docker` (Dockerfile `ENTRYPOINT` 안) 또는 `docker run -e SPRING_PROFILES_ACTIVE=docker` 로 활성화.

### ③ 환경변수 주입 — 값이 없으면 콜론 뒤 기본값

```yaml
# application-docker.yml — ${VAR:default}
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:host.docker.internal}:3306/swframework?...
    username: ${DB_USER:root}
    password: ${DB_PASS:1234}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

```bash
# 실행 시점에 환경변수 주입 — 코드 수정 없이 접속 대상 변경
docker run -d -p 8080:8080 \
  -e DB_HOST=host.docker.internal -e DB_USER=root -e DB_PASS=1234 \
  --name swframework-app swframework:1.0
```

### "컨테이너 안의 DB 호스트 이름" 정리

| 시나리오 | DB_HOST 값 | 이유 |
|---|---|---|
| 단일 컨테이너 + **호스트 MySQL** | `host.docker.internal` | 컨테이너에서 호스트 머신을 가리키는 특수 DNS |
| Docker Compose (앱 + DB 컨테이너) | `db` (서비스명) | 같은 네트워크 서비스명이 곧 호스트명 |
| `localhost` | ❌ 거의 항상 잘못 | 컨테이너 안의 `localhost` = 컨테이너 **자기 자신** |

> 시연 시 일부러 `DB_HOST=localhost` 로 띄워서 **DB 연결 실패** 로그를 보여주면 학생들이 강하게 기억한다.
> **비밀번호 등 시크릿은 이미지에 굽지 않는다** — 실행 시점 환경변수로 전달.

---

## (심화 · 선택) Multi-stage Build — 이미지 크기 최적화

빌드 단계(JDK + Gradle) 와 실행 단계(JRE) 를 분리해 최종 이미지에서 빌드 도구 · 소스를 제거.

| 방식 | 포함 | 크기 |
|---|---|---|
| 단일 스테이지 (`Dockerfile`) | JRE + JAR | ~290MB |
| **Multi-stage (`Dockerfile.multi`)** | **JRE + JAR** (빌드 도구·소스 제거) | **~250MB** |

```dockerfile
# Dockerfile.multi
FROM eclipse-temurin:21-jdk AS builder         # Stage 1: 빌드 (JDK + Gradle)
WORKDIR /build
COPY gradlew . && COPY gradle gradle
COPY build.gradle settings.gradle .
RUN ./gradlew dependencies --no-daemon         # 의존성 레이어 캐시
COPY src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre                    # Stage 2: 실행 (JRE 만)
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar   # ← Stage 1 산출물만 선택 복사
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
```

```bash
# bootJar 없이 소스에서 바로 (컨테이너 내부에서 빌드)
docker build -f Dockerfile.multi -t swframework:multi .
docker images   # swframework:1.0 (~290MB) vs swframework:multi (~250MB)
```

> 효과: 다운로드↑, 저장공간↓, **공격 표면↓** (보안). CI/CD 에서 Dockerfile 하나로 빌드~배포 자급자족.

---

## (심화 · 선택) Jib 플러그인 — Dockerfile 없이 빌드

Google Jib(Gradle 플러그인) 으로 **Dockerfile 없이** Spring Boot 이미지를 빌드. CI/CD 자동화 환경에서 강점.

| 비교 | Dockerfile | **Jib** |
|---|---|---|
| Dockerfile 필요 | 필수 | **불필요** |
| Docker 데몬 필요 | 필요 | 레지스트리 푸시 시 **불필요** |
| 레이어 분리 | 직접 작성 | **자동 최적화** (클래스/리소스/의존성) |

### `build.gradle` 설정 — 아래 블록을 `swframework/build.gradle` 에 추가

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.12'
    id 'io.spring.dependency-management' version '1.1.7'
    id 'com.google.cloud.tools.jib' version '3.4.4'   // ← Jib 플러그인 추가
}

// ── Jib 설정 ──
jib {
    from { image = 'eclipse-temurin:21-jre' }          // 베이스 이미지 (JRE)
    to   { image = 'swframework:latest' }              // 생성할 이미지 이름:태그
    container {
        mainClass   = 'kr.ac.tukorea.swframework.SwframeworkApplication'
        ports       = ['8080']
        environment = ['SPRING_PROFILES_ACTIVE': 'docker']
        jvmFlags    = ['-Xms256m', '-Xmx512m']
    }
}
```

```bash
./gradlew jibDockerBuild        # 로컬 Docker 에 이미지 생성 (Docker Desktop 필요)
docker run -d -p 8080:8080 --name swframework-jib swframework:latest
# 레지스트리로 바로 푸시할 때는 ./gradlew jib (Docker Desktop 없이도 가능)
```

---

## 시연 D — Docker Compose (앱 + DB 동시 실행) (~25분)

`docker run` 을 여러 번 치는 대신 **YAML 한 장** 으로 Spring Boot 앱 + MySQL 을 함께 띄운다.

### 사용 파일 (week12/ 루트)

- `docker-compose.yml` — `app`(Spring Boot) + `db`(MySQL)
- `sql/schema.sql` — db 컨테이너 최초 1회 자동 실행 (student + member 테이블)
- `application-docker.yml` — 환경변수 주입 설정

### 실행

```bash
./gradlew clean bootJar
docker compose up -d --build     # 네트워크 자동 생성 + app + db 동시 시작
docker compose ps                # app, db 모두 STATUS=Up 확인
docker compose logs -f app       # Spring Boot 시작 로그
```

확인: <http://localhost:8080/students>

```bash
docker compose down              # 중지·삭제 (볼륨=데이터 보존)
docker compose down -v           # 볼륨까지 삭제 (DB 데이터 완전 소멸)
```

### docker-compose.yml 핵심 (week12/docker-compose.yml)

```yaml
services:
  db:
    image: mysql:8.0
    container_name: swframework-db
    environment:
      MYSQL_ROOT_PASSWORD: 1234
      MYSQL_DATABASE: swframework
    ports: ["3306:3306"]
    volumes:
      - db-data:/var/lib/mysql                                       # 데이터 영속화
      - ./sql/schema.sql:/docker-entrypoint-initdb.d/schema.sql      # 최초 1회 자동 실행

  app:
    build: .
    container_name: swframework-app
    ports: ["8080:8080"]
    environment:
      DB_HOST: db                       # ← 같은 네트워크의 서비스 이름
      DB_USER: root
      DB_PASS: 1234
      SPRING_PROFILES_ACTIVE: docker
    depends_on: [db]                    # ⚠️ 시작 순서만 보장 (준비 완료 보장 X)

volumes:
  db-data:
```

### `depends_on` 의 한계 → 시연 E 의 healthcheck 로 보완

- MySQL 컨테이너는 시작되어도 실제 `SELECT 1` 응답까지 수 초 걸린다.
- 그 사이에 app 이 먼저 떠서 DB 연결을 시도하면 실패할 수 있다.
- **해결책**: `healthcheck` + `condition: service_healthy` (운영에서 안정적 시작 보장).

---

# 마무리

## W12 핵심 5줄 요약

1. **Docker** — 앱 + 실행환경을 묶어 '내 컴퓨터에서 되면 어디서든 된다' 보장
2. **3단계** — Dockerfile(레시피) → Image(틀) → Container(제품)
3. **최적화** — Multi-stage Build · Jib 로 이미지 경량화 · 자동화
4. **Compose** — YAML 한 장으로 앱 + DB 멀티 컨테이너 (+ healthcheck · restart)
5. **트러블슈팅** — JAR 먼저 빌드 · 컨테이너 안 `localhost` 는 자기 자신 · 포트 충돌 · `depends_on` 한계

> W12 시연으로 **내 앱(swframework) 컨테이너화 + 멀티 컨테이너 운영** 을 한 흐름으로 경험했다.

## 흔한 실수 종합 (트러블슈팅 — 시연 중 자주 발생)

| 증상 | 원인 | 해결 |
|---|---|---|
| `COPY failed: *.jar not found` | JAR 빌드 안 함 | `./gradlew clean bootJar` 먼저 |
| 컨테이너에서 DB 연결 실패 | 컨테이너 내부 `localhost` = 자기 자신 | `host.docker.internal` 또는 compose 서비스명 `db` |
| `port is already allocated` | 8080 / 3306 사용 중 | 로컬 서비스 종료 또는 `-p 8081:8080` |
| `Exited (1)` 반복 (`Restarting`) | 앱 기동 실패 | `docker logs` 첫 줄부터 원인 추적 |
| `Exited (137)` | OOM Kill | Docker Desktop 메모리 확장 또는 `-m` 옵션 |
| 한글 파라미터 깨짐 | 인코딩 누락 | `application.yaml` 의 `charset: UTF-8` 가 상속됨 |
| compose 에서 앱이 DB 보다 먼저 기동 | `depends_on` 은 시작 순서만 보장 | `healthcheck` + `condition: service_healthy` |

> **두 줄 요약**: ① **JAR 먼저 빌드** ② **컨테이너 안의 `localhost` 는 호스트가 아니다.**

## 검증 (week12.http)

`week12.http` 에 다음 시나리오를 두었다. IntelliJ HTTP Client(또는 VSCode REST Client) 에서 실행해 컨테이너가 외부에서 접속 가능한지, W11 회귀가 정상인지 확인한다.

- **H** — 헬스 체크 (`/students` · 페이징 · 검색)
- **DB** — DB 라운드트립 (회원 로그인 · 상세 조회)
- **N** — 네거티브 (옛 `/board/list` 는 404, 첨부 다운로드 라우트 응답 도달)

## 다음 주(W13) 예고

> "오늘 만든 Docker 이미지가 CI/CD 자동 빌드 · 배포의 출발점이 된다."

- **CI/CD** — 지속적 통합 / 배포 · 빌드 · 테스트 · 배포 자동화
- **GitHub Actions** — 워크플로우(`.yml`) 로 리포지토리 내 자동화
- **OSS 라이선스** — MIT · Apache · GPL 의 차이와 의무사항

W12 에서 만든 Docker 이미지 → W13 에서 GitHub Actions 로 자동 빌드 · 푸시 → 운영 환경 배포의 한 흐름으로 이어진다.

---

## 참고 자료

- [Docker 시작 가이드](https://docs.docker.com/get-started/)
- [Dockerfile 레퍼런스](https://docs.docker.com/reference/dockerfile/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [Docker Hub — Eclipse Temurin](https://hub.docker.com/_/eclipse-temurin)
- 코딩 자율학습 스프링 부트 3 (홍팍 저, 길벗)


