# [실습 가이드] SW 프레임워크 2주차: 개발환경 설정 및 Git 기초

---

## 목차

1. [JDK 21 설치 및 환경변수 설정](#1-jdk-21-설치-및-환경변수-설정)
2. [IntelliJ IDEA 설치 및 프로젝트 생성](#2-intellij-idea-설치-및-프로젝트-생성)
3. [Gradle 빌드 도구](#3-gradle-빌드-도구)
4. [MySQL 8 설치 및 DB 계정 설정](#4-mysql-8-설치-및-db-계정-설정)
5. [Git 기초 실습](#5-git-기초-실습)
6. [실습 5: 브랜치 생성 및 병합](#실습-5-브랜치-생성-및-병합)
7. [실습 6: 브랜치 전략 및 PR 실습](#실습-6-브랜치-전략-및-pr-실습)
8. [실습 7: H2 Console 및 DevTools 설정](#실습-7-h2-console-및-devtools-설정)

---

## 1. JDK 21 설치 및 환경변수 설정

> **다운로드**: https://adoptium.net/temurin/releases/?version=21
> 운영체제에 맞는 **JDK 21 (LTS)** 선택
>
> **Eclipse Temurin이란?**
> Oracle JDK와 동일한 OpenJDK 소스 기반의 무료 배포판입니다.
> Oracle JDK는 상업적 이용 시 라이선스 비용이 발생하지만, Temurin은 완전 무료입니다.
> Eclipse Foundation(Adoptium)이 관리하며 실무에서 가장 널리 쓰이는 JDK 배포판 중 하나입니다.

### Java 버전 선택 기준

| 버전 | 출시 | 지원 종료 | 비고 |
|------|------|-----------|------|
| Java 8 | 2014 | 2030년 12월 | 레거시 프로젝트에 사용, 신규 금지 |
| Java 17 | 2021 | 2029년 9월 | Spring Boot 3.x 최소 요구 버전 |
| **Java 21** | **2023** | **2031년 9월** | **이 과목 기준 버전 (LTS)** |
| Java 22 | 2024 | 6개월 단기 | 실무·학습 비권장 |

> Spring Boot 3.x는 Java 17 이상이 필수입니다. Java 8·11로는 실습 불가.

### Windows

1. **JDK 다운로드**: x64 Installer 다운로드 후 설치
   - 기본 설치 경로: `C:\Program Files\Java\jdk-21`

2. **환경변수 등록** (시스템 속성 > 환경변수 > 시스템 변수 > 새로 만들기):

   | 변수 | 값 |
   |------|-----|
   | `JAVA_HOME` | `C:\Program Files\Java\jdk-21` |
   | `Path` | `%JAVA_HOME%\bin` 추가 |

3. **설치 확인**: **새** PowerShell 창에서 실행 (기존 창은 환경변수 미반영)

```powershell
java -version    # openjdk version "21.x.x" 출력 확인
javac -version   # javac 21.x.x 출력 확인
echo $env:JAVA_HOME
```

### macOS

1. **Homebrew 설치** (미설치 시): https://brew.sh

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

2. **JDK 설치**:

```bash
brew install openjdk@21
```

3. **쉘 설정**: `~/.zshrc`에 추가 후 `source ~/.zshrc` 실행

```bash
export JAVA_HOME="$(brew --prefix openjdk@21)"
export PATH="$JAVA_HOME/bin:$PATH"
```

4. **설치 확인**:

```bash
java -version    # openjdk version "21.x.x" 출력 확인
```

### 트러블슈팅

| 문제 | 원인 | 해결 |
|------|------|------|
| `'java'은(는) 내부 또는 외부 명령이 아닙니다` | JAVA_HOME 또는 Path 설정 오류 | 환경변수 확인 후 **터미널 재시작** |
| macOS: `brew` 명령어 없음 | Homebrew 미설치 | brew.sh에서 먼저 설치 후 터미널 재시작, `brew --version`으로 확인 |

> **확인 포인트**: `java -version`이 21.x.x인가? 환경변수 설정 후 반드시 **새 터미널**에서 확인.

---

## 2. IntelliJ IDEA 설치 및 프로젝트 생성

> **다운로드**: https://www.jetbrains.com/idea/download/
> 스크롤 내려서 **IntelliJ IDEA Community Edition** (무료) 선택
> 권장 버전: **2025.x 이상**

### 설치 및 초기 설정

1. **IDE 설치**: 다운로드한 설치 파일 실행 후 기본 옵션으로 설치

2. **초기 설정 — 테마 및 폰트**:

   | 항목 | 권장 설정 |
   |------|-----------|
   | 테마 | **Darcula** (어두운 배경 — 장시간 코딩 시 눈 피로 감소) |
   | 폰트 크기 | **14~16pt** (Settings > Editor > Font) |
   | 한글 폰트 | **D2Coding** 또는 **JetBrains Mono** |

3. **Ultimate 버전 학생 인증** (선택):
   - JetBrains 웹사이트에서 학생 인증 페이지 접속
   - 학교 이메일(`@tukorea.ac.kr`)로 라이선스 신청 → 1년 단위 무료
   - Ultimate는 Spring, Database 도구 등 추가 기능 포함
   - Community로도 모든 실습 가능

### 첫 프로젝트 생성

1. **New Project** 선택

   | 항목 | 설정값 |
   |------|--------|
   | Build system | **Gradle** |
   | Language | Java |
   | JDK | **21** |

2. **Gradle Indexing 완료 대기** (프로젝트 구조 로딩 시간 필요)

3. **동작 확인**: `Main.java` 파일 우클릭 > Run 선택 > 콘솔에 `Hello World` 출력 확인

### 추천 플러그인 (Settings > Plugins)

| 플러그인 | 중요도 | 설명 |
|----------|--------|------|
| Lombok | **필수** | `@Data`, `@Builder` 등 보일러플레이트 코드 자동 생성 |
| Key Promoter X | 권장 | 마우스 클릭 시 해당 단축키 안내 |

### IntelliJ 주요 단축키

| 기능 | Windows | macOS | 설명 |
|------|---------|-------|------|
| 자동완성 | `Ctrl+Space` | `^Space` | 변수·메서드·클래스 추천 |
| 디버그 한 줄 실행 | `F8` | `F8` | Step Over |
| 디버그 내부 진입 | `F7` | `F7` | Step Into |
| 이름 변경 | `Shift+F6` | `Shift+F6` | 변수·메서드명 일괄 변경 |
| 메서드 추출 | `Ctrl+Alt+M` | `⌘+Alt+M` | 코드 블록 → 메서드 추출 |
| Git 패널 | `Alt+9` | `⌘+9` | 커밋·브랜치·diff 시각적 관리 |

### 트러블슈팅

| 문제 | 해결 방법 |
|------|-----------|
| `No SDK` 또는 `SDK not found` | File > Project Structure > Project 탭 > SDK를 21로 선택 > Apply |
| Gradle 동기화 실패 | Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM을 21로 변경 > Gradle Sync 재시도 |
| IDE 동작 불안정·인식 오류 (캐시 문제) | File > Invalidate Caches… > **Invalidate and Restart** 클릭 > 재인덱싱 완료 확인 |

---

## 3. Gradle 빌드 도구

> Gradle은 별도 설치 불필요 — IntelliJ 프로젝트 생성 시 **Gradle Wrapper**가 자동 포함됩니다.

### Gradle vs Maven

| | Gradle | Maven |
|-|--------|-------|
| 설정 방식 | Groovy/Kotlin DSL (간결) | XML pom.xml (장황) |
| 빌드 속도 | 빠름 (Incremental Build & Cache) | 상대적으로 느림 (순차 실행) |
| 유연성 | 높음 | 정형화된 생명주기로 제한적 |
| 이 과목 | **표준 빌드 도구** | 레거시 프로젝트에서 많이 사용 |

### Gradle 빌드 생명주기

```
① 초기화 → ② 설정 → ③ 실행 → ④ 출력
(프로젝트 파악)  (build.gradle 해석)  (컴파일·테스트·패키징)  (JAR 생성)
```

### 자주 쓰는 Gradle 명령어

> Windows에서는 `./gradlew` 대신 `gradlew` 또는 `gradlew.bat` 사용

| 명령어 | 설명 | 사용 시점 |
|--------|------|-----------|
| `./gradlew --version` | Gradle 버전 확인 | 설치 확인 |
| `./gradlew clean` | `build/` 디렉토리 삭제, 이전 빌드 결과 제거 | 빌드 문제 발생 시 **첫 번째** 시도 |
| `./gradlew build` | 컴파일 + 테스트 + JAR 생성 (전체 빌드) | 배포 전 최종 확인 |
| `./gradlew bootRun` | Spring Boot 애플리케이션 실행 (개발 서버 시작) | 개발 중 실행 테스트 |
| `./gradlew test` | 테스트 코드만 실행 | CI/CD 파이프라인, 테스트 확인 |
| `./gradlew dependencies` | 의존성 트리 출력, 버전 충돌 확인 | 라이브러리 충돌 디버깅 |

---

## 4. MySQL 8 설치 및 DB 계정 설정

> **다운로드**: https://dev.mysql.com/downloads/mysql/
> 운영체제 선택 후 **MySQL Community Server 8.x** 다운로드
> (로그인 없이 설치하려면 "No thanks, just start my download" 클릭)

### Windows 설치

- MySQL Installer (MSI) 다운로드 후 실행
- **Server Only** 옵션 선택하여 설치
- 설치 과정에서 Root 비밀번호 설정 — **반드시 기억할 수 있도록 메모**
- 기본 포트: **3306**

### macOS 설치

```bash
brew install mysql
brew services start mysql      # MySQL 서비스 자동 시작 설정
mysql_secure_installation      # Root 비밀번호 설정 및 보안 설정
```

### 접속 및 버전 확인

```bash
mysql -u root -p               # Root로 접속
```

```sql
SELECT VERSION();              -- 8.x.x 출력 확인
```

### 실습 3: 실습용 DB 및 계정 생성

```sql
-- 데이터베이스 생성 (9주차 게시판 프로젝트까지 계속 사용)
CREATE DATABASE spring_board
  CHARACTER SET utf8mb4          -- 한글·이모지 저장 가능
  COLLATE utf8mb4_unicode_ci;

-- 실습용 계정 생성
CREATE USER 'student'@'%' IDENTIFIED BY 'pass!123';

-- 권한 부여
GRANT ALL PRIVILEGES ON spring_board.* TO 'student'@'%';
FLUSH PRIVILEGES;

-- 생성 확인
SHOW DATABASES;                                -- spring_board 목록 확인
SELECT user, host FROM mysql.user;             -- student 계정 생성 확인
```

### 실습 3: 기본 CRUD 실습

```sql
-- 1. 데이터베이스 선택
USE spring_board;

-- 2. 테이블 생성
CREATE TABLE students (
  id   INT         PRIMARY KEY,
  name VARCHAR(20)
);

-- 3. 데이터 삽입 (Create)
INSERT INTO students (id, name) VALUES (101, '홍길동');

-- 4. 데이터 조회 (Read)        ← 확인 포인트: 결과가 정상 출력되는가?
SELECT * FROM students;

-- 5. 데이터 수정 (Update)
UPDATE students SET name = '김철수' WHERE id = 101;

-- 6. 데이터 삭제 (Delete)
DELETE FROM students WHERE id = 101;
```

### MySQL vs H2 비교

| | MySQL | H2 |
|-|-------|----|
| 종류 | 독립 실행형 RDBMS | 내장형(Embedded) RDBMS |
| 데이터 보존 | 디스크에 영구 저장 | 앱 종료 시 삭제 (인메모리) |
| 용도 | 운영 환경 실제 서비스 | 개발·테스트 빠른 반복 |
| 설치 | 별도 설치 + 포트(3306) 설정 | Spring Boot 의존성만 추가 |
| 이 과목 | 팀 프로젝트 최종 배포 단계 | 1~8주차 실습 |

### DBeaver (선택 — GUI DB 클라이언트)

> **다운로드**: https://dbeaver.io/download/
> MySQL에 그래픽 인터페이스로 접속하고 싶을 때 사용

### 트러블슈팅

| 문제 | 해결 |
|------|------|
| 포트 충돌 (3306 이미 사용 중) | 작업 관리자에서 충돌 프로세스 종료, 또는 `my.ini`에서 포트를 3307로 변경 후 서비스 재시작 |
| Root 비밀번호 분실 | 로컬 실습 환경에서는 **재설치가 가장 빠름** |
| `Access denied for user 'root'` | 비밀번호 오타 확인, 대소문자 주의 |

---

## 5. Git 기초 실습

> **Git 다운로드**: https://git-scm.com/downloads (최신 버전, 2.x 이상 권장)
> **GitHub 가입**: https://github.com
>
> Windows에서는 Git Bash 또는 PowerShell 사용, macOS/Linux는 기본 Terminal 사용.

### Git vs GitHub

| | Git | GitHub |
|-|-----|--------|
| 정의 | 분산 버전 관리 시스템 (DVCS) | 원격 저장소 호스팅 서비스 |
| 운영 환경 | 로컬 PC (오프라인 가능) | 클라우드 (인터넷 필수) |
| 주요 기능 | 이력 관리, 브랜치, 병합 | Pull Request, Issue, Actions |
| 사용 형태 | CLI 명령어 기반 | 웹 인터페이스 + CLI |
| 작업 흐름 | 수정 → 선택(add) → 기록(commit) | 공유(push) → 협업(PR) → 백업 |

> Git은 도구이며, GitHub 사용의 전제 조건입니다. Git 없이 GitHub을 쓸 수 없습니다.

### Git 작업 흐름 (4단계)

```
Working Directory  →(git add)→  Staging Area  →(git commit)→  Local Repository  →(git push)→  Remote Repository
  (파일 수정)                   (커밋 준비)                    (버전 기록)                      (GitHub 공유)
                  ←──────────────────────────────────────────────────────(git pull)─────────────────────────
                  ←─────────────────────────────────────────────────────────────────────(git clone)─────────
```

| 영역 | 설명 |
|------|------|
| Working Directory | 실제 파일을 편집하는 작업 공간 |
| Staging Area | `git add`로 커밋할 파일을 선택·준비하는 대기 장소 (선택적으로 추가 가능) |
| Local Repository | `git commit`으로 버전을 영구 기록하는 로컬 저장소 (각 커밋은 고유 해시값 보유) |
| Remote Repository | `git push`로 업로드하는 GitHub 등 원격 저장소 |

### Git 설치 확인

```bash
git --version    # Git 버전 정보 출력 확인
```

### 실습 4: 초기 설정

```bash
git config --global user.name "홍길동"
git config --global user.email "id@tukorea.ac.kr"

# 설정 확인
git config --global --list    # 설정된 모든 전역 옵션 표시
```

> 사용자 정보는 모든 커밋 기록에 남으며, GitHub·GitLab에서 기여자 식별에 사용됩니다.

### 실습 4: 로컬 저장소 생성 및 첫 커밋

```bash
# 프로젝트 폴더 생성 및 Git 초기화
mkdir hello-git && cd hello-git
git init

# 파일 생성 및 커밋
echo "# Hello Git" > README.md
git add README.md
git commit -m "docs: add README"

# 커밋 이력 확인
git log --oneline
```

### 커밋 메시지 컨벤션

> 한 커밋에는 한 가지 변경만 담기. 제목은 한 줄, 50자 이내, 마침표 없이.

| 패턴 | 의미 | 예시 |
|------|------|------|
| `feat:` | 새 기능 추가 | `feat: 게시글 작성 기능 추가` |
| `fix:` | 버그 수정 | `fix: 댓글 저장 오류 수정` |
| `docs:` | 문서 수정 | `docs: README에 실행 방법 추가` |
| `style:` | 코드 포맷 통일 | `style: 코드 포맷 통일` |
| `refactor:` | 리팩토링 | `refactor: User 서비스 구조 리팩토링` |

### 실습 4: GitHub 연결 및 Push

GitHub에서 `hello-git` 저장소(Private 권장, README 초기화 체크 **해제**)를 생성한 후:

```bash
git remote add origin <저장소 URL>
git remote -v                          # 연결 상태 확인
git push -u origin main

# 이력 확인
git log --oneline --graph --decorate   # 커밋 이력 그래프로 시각화
git status                             # 작업 트리 상태 확인
```

### GitHub Personal Access Token (PAT)

GitHub는 비밀번호 대신 PAT를 사용합니다.

1. GitHub > Settings > Developer settings > Personal access tokens > **Generate new token (classic)**
2. `repo` 권한 체크 후 생성
3. `git push` 시 비밀번호 자리에 토큰 입력
4. 토큰은 안전하게 보관 및 만료 설정 가능

### 트러블슈팅

| 문제 | 원인 | 해결 |
|------|------|------|
| `remote: Permission denied` | PAT 미설정 또는 권한 부족 | Settings > Developer settings > Tokens에서 PAT 생성 (`repo` 권한 체크) |
| `fatal: remote origin already exists` | 이미 원격 저장소 등록됨 | `git remote set-url origin <새 URL>` 또는 `git remote remove origin` 후 재등록 |

---

## 실습 5: 브랜치 생성 및 병합

### 브랜치 생성 및 이동

```bash
git branch feature/login      # 새 기능 브랜치 생성
git switch feature/login       # 브랜치 이동
# 또는 한 번에: git switch -c feature/login
```

> 브랜치는 독립적인 작업 공간 — `feature/login`에서 작업해도 `main`에는 영향 없음

### 파일 수정 및 커밋

```bash
echo "로그인 기능 추가" >> README.md
git add README.md
git commit -m "feat: add login description"
```

### main으로 병합

```bash
git switch main                        # 메인 브랜치로 복귀
git merge feature/login                # 변경사항 병합
git log --oneline --graph --decorate   # 병합 이력 확인
```

---

## 실습 6: 브랜치 전략 및 PR 실습

### 기능 브랜치 작업

```bash
git switch -c feature/about
echo "실습용 정보" >> ABOUT.md
git add .
git commit -m "feat: add ABOUT page"
git push -u origin feature/about
```

### 실습 6: Pull Request 생성 및 병합

GitHub 웹에서 **Compare & pull request** 클릭 → PR 작성 → `main` 브랜치로 Merge

### 로컬 동기화

```bash
git switch main
git pull origin main
```

---

## 실습 7: H2 Console 및 DevTools 설정

### H2 데이터베이스란?

- 설치 없이 사용하는 **경량 내장형 DB** — Spring Boot 의존성만 추가하면 자동 설정
- 애플리케이션 시작 시 DB 자동 생성, **종료 시 데이터 삭제** (인메모리 모드)
- `localhost:8080/h2-console`에서 웹 브라우저로 SQL 실행 가능
- 이 과목 **1~8주차 실습**에 사용, 팀 프로젝트 배포 시 MySQL로 전환

### application.yml 설정

`src/main/resources/application.yml`에 추가:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb         # 인메모리 모드
    driver-class-name: org.h2.Driver
    username: sa
    password:                       # 빈 값 (기본값)
  h2:
    console:
      enabled: true
      path: /h2-console             # 접속: localhost:8080/h2-console
  sql:
    init:
      mode: always
      schema-locations: classpath:sql/schema-h2.sql   # 테이블 생성 스크립트
      data-locations: classpath:sql/data.sql           # 초기 데이터 스크립트
```

> **접속 방법**: 앱 실행 후 브라우저에서 `http://localhost:8080/h2-console` 열기
> - JDBC URL: `jdbc:h2:mem:testdb`
> - User Name: `sa`
> - Password: (빈 값)

### 자동 빌드 활성화 (DevTools)

1. `build.gradle` 의존성 추가:

```groovy
developmentOnly 'org.springframework.boot:spring-boot-devtools'
```

2. IntelliJ 설정: **Settings > Build, Execution, Deployment > Compiler > Build project automatically** 체크
