# W12 핵심 용어 사전 — Docker 컨테이너화 배포

> 학부생/주니어 기준. "무엇 → 왜 → 실무 한 줄" 순서로 정리.
> 본 주차는 **시연(Demonstration)** — 강사 시연을 따라가며 용어가 어디서 등장했는지 매핑하면 효과적이다.

## 1. 핵심 3요소

| 용어 | 무엇 | 왜/실무 |
|---|---|---|
| **Dockerfile** | 이미지를 만드는 명령어 텍스트 파일 (레시피) | `docker build` 가 읽어 이미지를 생성. Git 으로 버전 관리. |
| **Image (이미지)** | 앱 + 실행환경이 묶인 **읽기 전용** 템플릿 (붕어빵 틀) | 한 번 만들면 어디서나 동일 실행. 변경 불가(Immutable) → 변경 시 새 레이어. |
| **Container (컨테이너)** | 이미지를 실행한 **인스턴스** (구워진 붕어빵) | `docker run` 으로 생성. 하나의 이미지로 여러 개 실행 가능. |

> 한 줄: **Dockerfile(레시피) → build → Image(틀) → run → Container(제품).**

## 2. Docker 아키텍처 — Client / Daemon / Registry

| 구성 | 무엇 | 역할 |
|---|---|---|
| **Docker Client** | CLI (`docker build`, `docker run` …) | 사용자 명령의 시작점. REST API 로 Daemon 에 전달 |
| **Docker Daemon** (`dockerd`) | 백그라운드 서비스 | 이미지 빌드 · 컨테이너 생명주기 관리 (실제 작업 수행) |
| **Docker Registry** | 이미지 저장소 (Docker Hub · 사내 레지스트리) | `pull` (받기) / `push` (올리기) 로 이미지 공유 |

> 사용자는 **Client(CLI)** 로 명령만 내리고, 실제 작업은 **Daemon** 이 수행한다. 이미지는 **Registry** 에서 받거나 올린다.

## 3. VM vs Container

| | VM (가상머신) | Container |
|---|---|---|
| 비유 | 독립 주택 (집마다 OS) | 호텔 객실 (건물 OS 커널 공유) |
| 격리 | Guest OS 전체 | 호스트 OS 커널 공유 |
| 크기 | 수 GB | 수십~수백 MB |
| 부팅 | 수 분 | ms 단위 |
| 오버헤드 | 높음(5~10%) | 매우 낮음(1% 미만) |
| 가상화 수준 | 하드웨어 수준 | OS 수준 |
| 이식성 | 상대적으로 낮음 | 매우 높음 |

> 한 줄: **VM 은 독립 주택, Docker 는 효율적인 호텔 방.**

## 4. Dockerfile 명령어

| 명령 | 시점 | 역할 |
|---|---|---|
| `FROM` | 빌드 | 베이스 이미지(출발점). 예: `eclipse-temurin:21-jre` |
| `WORKDIR` | 빌드 | 작업 디렉토리 설정. 예: `/app` |
| `COPY` | 빌드 | 호스트 파일 → 컨테이너. 예: `COPY build/libs/*.jar app.jar` |
| `RUN` | **빌드** | 빌드 중 명령 실행 (패키지 설치 등). 각 RUN 이 레이어 생성 |
| `ENV` | 빌드/실행 | 환경변수 기본값. 예: `ENV PROFILE=docker` |
| `EXPOSE` | 문서 | 사용할 포트 명시 (문서 목적). 실제 연결은 `-p` |
| `ENTRYPOINT` | **실행** | 컨테이너 시작 시 실행할 메인 명령. 예: `["java","-jar","app.jar"]` |
| `CMD` | **실행** | ENTRYPOINT 의 기본 인자 (단독 사용 시 메인 명령) |

> `RUN`(빌드 시점) vs `ENTRYPOINT/CMD`(실행 시점) 구분이 핵심.

## 5. 레이어 / 캐시 최적화

- **Layer(레이어)**: Dockerfile 명령 하나 = 레이어 하나. 변경분만 저장(증분).
- **캐시 최적화 원칙**:
  - **위쪽** = 잘 안 바뀌는 것 (베이스 이미지 · 의존성)
  - **아래쪽** = 자주 바뀌는 것 (소스 · JAR)
  - → 위쪽 레이어 캐시가 유지되어 빌드 시간 대폭 단축
- **변경 레이어 "이후"는 모두 재빌드** → 자주 바뀌는 명령을 아래로 미루는 것이 핵심

```dockerfile
# 좋은 예 — 의존성 먼저, 소스 나중
COPY build.gradle settings.gradle .
RUN ./gradlew dependencies --no-daemon   # 의존성 캐시 유지
COPY src src                              # 소스 변경에만 영향
RUN ./gradlew bootJar --no-daemon
```

## 6. 컨테이너 라이프사이클

```
Created → Running → Paused → Stopped → Removed
docker     docker     docker    docker    docker
create     run/start  pause     stop      rm
```

| 상태 | 명령 | 의미 |
|---|---|---|
| **Created** | `docker create` | 생성만 됨, 미실행 |
| **Running** | `docker run` / `start` | 실행 중 |
| **Paused** | `docker pause` | 일시중단 (메모리 유지). `docker unpause` 로 복귀 |
| **Stopped** | `docker stop` | 종료 (파일은 보존, 재시작 가능) |
| **Removed** | `docker rm` | 완전 삭제 (내부 데이터 소멸) |

> `stop` = 잠시 멈춤 (재시작 가능) / `rm` = 완전 삭제. 상태를 알면 디버깅이 쉬워진다.

## 7. STATUS 읽는 법 — `docker ps` 의 핵심

| STATUS 표시 | 의미 | 대응 |
|---|---|---|
| `Up 5 minutes` | 정상 실행 중 (5분째) | OK |
| `Exited (0) ...` | 정상 종료 | OK |
| `Exited (1) ...` / `Exited (137)` | 비정상 종료 (오류 / SIGKILL) | `docker logs` 로 원인 확인 |
| `Restarting (N)` | 기동 실패 반복 (N회째 재시도) | `docker logs` 필수 — 무한 재시작 중 |
| `PORTS: 0.0.0.0:8080->8080/tcp` | 호스트 8080 ↔ 컨테이너 8080 매핑 | 정상 매핑 |

> **트러블슈팅은 항상 `docker ps` 와 `docker logs` 에서 시작한다.**

## 8. 네트워크 / 스토리지

| 용어 | 무엇 | 주의 |
|---|---|---|
| **포트 매핑** | `-p 8080:8080` = 호스트:컨테이너 | 호스트 포트만 바꿔도 코드 수정 불필요 (`-p 8081:8080`) |
| **컨테이너 내부 localhost** | 컨테이너 **자기 자신** | 호스트 DB 는 `host.docker.internal`, compose 는 서비스명(`db`) |
| **Volume(볼륨)** | 컨테이너 밖 데이터 저장소 | 컨테이너 삭제돼도 데이터 보존 → **DB 컨테이너 필수** |
| **bind mount** | 호스트 디렉토리 직접 마운트 | 개발 중 소스 hot-reload 등에 활용 |

## 9. 이미지 최적화

| 용어 | 무엇 | 효과 |
|---|---|---|
| **JDK vs JRE** | JDK=개발(컴파일)용, JRE=실행 전용 | 최종 이미지엔 JRE만 → 경량 |
| **Multi-stage Build** | Stage1(JDK 빌드) → Stage2(JRE+JAR만) | `COPY --from=builder` 로 산출물만 복사. ~290MB → ~250MB |
| **Jib** | Google Gradle/Maven 플러그인 | Dockerfile · Docker 데몬 없이 이미지 빌드. CI/CD 친화 |
| **.dockerignore** | 빌드 컨텍스트 제외 목록 | `src/`, `.git` 등 제외 → 빌드↑, 크기↓ |

## 10. Docker Compose

| 키 | 의미 |
|---|---|
| `services` | 실행할 컨테이너 목록 (app, db) |
| `image` / `build` | 기존 이미지 vs Dockerfile 빌드 |
| `ports` | 호스트:컨테이너 포트 |
| `environment` | 환경변수 (예: `DB_HOST: db`) |
| `depends_on` | 시작 **순서** (준비 완료 보장은 아님) |
| `volumes` | 데이터 영속화 |
| `healthcheck` | 컨테이너 '준비 완료' 판정 (`SELECT 1` 등) — `depends_on: condition` 의 핵심 |

> 명령: `docker compose up -d --build` / `ps` / `logs -f` / `down` / `down -v`

### depends_on 의 한계 + healthcheck

- `depends_on` 은 **시작 순서**만 보장한다 (db 가 app 보다 먼저 시작).
- DB 가 **접속 가능**한지(`SELECT 1` 통과)는 보장하지 않는다.
- 해결: `db` 에 `healthcheck` 를 두고, `app` 은 `depends_on: { db: { condition: service_healthy } }`.

## 11. 배포 / 운영

| 용어 | 무엇 |
|---|---|
| **Docker Daemon** | 이미지 빌드 · 컨테이너 실행을 담당하는 백그라운드 서비스(dockerd) |
| **Registry** | 이미지 저장소 (Docker Hub, 사내 레지스트리) — `pull`/`push` |
| **프로파일** | `-Dspring.profiles.active=docker` → `application-docker.yml` 활성화 |
| **restart 정책** | `no` / `always` / `on-failure` / `unless-stopped` — 컨테이너 자동 재시작 |
| **healthcheck** | 컨테이너 '준비 완료' 판정. `depends_on` 의 한계를 보완 |
| **CI/CD** | (다음 주 W13) 빌드·테스트·배포 자동화. GitHub Actions 등 |

### restart 정책 4가지

| 정책 | 동작 | 사용 예 |
|---|---|---|
| `no` | 재시작 안 함 (기본값) | 일회성 작업·테스트 |
| `always` | 항상 재시작 (Docker 데몬 재시작 시에도) | 중요 웹/DB 서비스 |
| `on-failure` | 오류 종료(exit≠0)일 때만 (횟수 제한 가능) | 오류 복구가 필요한 앱 |
| `unless-stopped` | 수동 `stop` 전까지 항상 재시작 | 프로덕션 안정 운영 |

## 12. swframework 컨텍스트 (도메인 일치)

- 컨테이너화 대상 = **student 관리 앱** (게시판 board 아님). 접속 URL `http://localhost:8080/students`.
- DB: `student`, `member` 테이블 (W09~W11). 회원 비밀번호는 BCrypt 해시(W07 `PasswordUtil`).
- JAR: `build/libs/swframework-0.0.1-SNAPSHOT.jar`, 이미지: `swframework:1.0`.
- 기본 프로파일은 `h2` (개발용) → 컨테이너에서는 `docker` 프로파일로 MySQL 접속.
