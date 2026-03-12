# 2주차 핵심 용어집

강의 자료의 비유와 설명을 바탕으로 정리한 용어집입니다. 처음 접하는 개념은 비유를 통해 먼저 감을 잡고, 정확한 정의로 마무리하세요.

---

## 1. 개발환경

**개발환경 (Development Environment)**
> "요리 전에 칼, 도마, 불을 준비하는 것과 같은 원리"

Spring Boot 프로젝트를 개발하고 실행하는 데 필요한 도구들의 집합입니다. JDK, IDE, 빌드 도구, 데이터베이스가 모두 설치되고 올바르게 연결된 상태를 말합니다.
- 실무에서는 신입 개발자가 환경 설정에 하루~이틀이 걸리는 경우가 흔합니다.

---

**JDK (Java Development Kit)**
자바 프로그램을 **컴파일하고 실행**하는 데 필요한 도구 전체 묶음입니다. 컴파일러(javac), JVM, 표준 라이브러리가 포함됩니다.
- **JRE**: 실행만 가능 (컴파일 불가)
- **JVM**: 바이트코드를 OS에 맞게 실행하는 가상 머신
- 이 과목 기준: **JDK 21 (LTS)** 필수

---

**LTS (Long Term Support)**
> "안정적인 집처럼, 수년간 보안 패치와 업데이트가 보장된 버전"

일반 버전은 6개월 단기 지원으로 끝나지만, LTS 버전은 **수년간 안정적인 지원**을 받을 수 있습니다. 실무와 학습 모두 LTS 버전을 선택하는 것이 원칙입니다.

| 버전 | 출시 | LTS 지원 종료 | 비고 |
|---|---|---|---|
| Java 8 | 2014 | 2030년 12월 | 레거시 프로젝트 다수 |
| Java 17 | 2021 | 2029년 9월 | Spring Boot 3.0 최소 요건 |
| **Java 21** | **2023** | **2031년 9월** | **이 과목 기준** |

---

**IDE (통합 개발 환경, Integrated Development Environment)**
코드 작성, 컴파일, 디버깅, 버전 관리까지 **개발에 필요한 모든 기능을 하나로 통합**한 도구입니다. 이 과목 표준: **IntelliJ IDEA**.
- **지능형 자동완성**: 컨텍스트 기반 변수·메서드·클래스 추천 (`^Space` on macOS)
- **디버깅**: 중단점(Breakpoint) 설정 후 변수값과 호출 스택 확인 (F8/F7)
- **리팩토링**: 이름 일괄 변경(`Shift+F6`), 메서드 추출(`Ctrl+Alt+M`) 등 안전한 코드 구조 변경
- **VCS 통합**: Git 커밋·브랜치·diff·병합을 GUI로 처리 (`⌘9` on macOS)

---

**Gradle**
> "Maven보다 빠른 현대적 빌드 도구"

Groovy/Kotlin DSL 기반으로 **의존성 관리와 빌드 자동화**를 담당합니다. 이 과목의 표준 빌드 도구입니다.

```
빌드 생명주기:
① 초기화 → ② 설정(build.gradle 해석) → ③ 실행(컴파일·테스트) → ④ 출력(JAR 생성)
```

- **Gradle Wrapper** (`gradlew`): 팀 전원이 동일한 Gradle 버전으로 빌드하도록 보장하는 스크립트. `./gradlew --version`으로 확인

> **Gradle vs Maven 핵심 차이:** Gradle은 Incremental Build & Cache로 빠르고, Maven은 XML 기반 pom.xml로 정형화됨. 신규 프로젝트는 Gradle.

---

## 2. 데이터베이스

**H2 데이터베이스**
> "설치 없이 쓰는 초경량 연습용 DB"

Java로 작성된 **내장형(Embedded) RDBMS**로, Spring Boot 의존성 하나만 추가하면 자동으로 사용할 수 있습니다.
- **인메모리 모드**: `jdbc:h2:mem:testdb` — 앱 종료 시 데이터 삭제
- **H2 Console**: 브라우저에서 `localhost:8080/h2-console`로 SQL 직접 실행
- 이 과목: 1~8주차 실습에 H2 사용 → 팀 프로젝트 배포 단계에서 MySQL로 전환

---

**MySQL**
가장 널리 쓰이는 **독립 실행형(Standalone) 오픈소스 RDBMS**입니다. 별도 서버 프로세스가 필요하며, 데이터는 디스크에 영구 저장됩니다.
- 포트: 기본 **3306**
- 접속 확인: `mysql -u root -p` → `SELECT VERSION();`
- 이 과목: 최종 팀 프로젝트 배포 환경에서 사용

> **MySQL vs H2 핵심 차이:** MySQL은 운영 환경용 영구 저장, H2는 개발·테스트 환경용 인메모리. 개발 중에는 H2로 빠르게, 배포 시 MySQL로 전환.

---

**DBeaver**
MySQL, H2 등 다양한 DB에 연결할 수 있는 **범용 DB 클라이언트 GUI 도구**입니다. SQL 실행, 테이블 조회, ERD 확인 등을 시각적으로 수행합니다.

---

## 3. Git & GitHub

**Git**
> "수정 → 선택(add) → 기록(commit)의 3단계로 버전을 관리하는 로컬 도구"

**분산 버전 관리 시스템(DVCS)**으로, 인터넷 없이도 로컬에서 버전 관리가 가능합니다. GitHub 사용의 전제 조건입니다.

---

**GitHub**
Git 저장소를 **클라우드에 호스팅**하는 플랫폼입니다. Pull Request, Issue, Actions 등 협업 기능을 제공합니다.

> **Git vs GitHub 핵심 차이:** Git은 로컬 도구, GitHub은 클라우드 서비스. Git 없이 GitHub을 쓸 수 없음.

---

**Git 작업 흐름 (4단계)**

```
Working Directory  →(git add)→  Staging Area  →(git commit)→  Local Repository  →(git push)→  Remote Repository
     파일 수정              커밋 준비                버전 기록                             GitHub 공유

                    ←──────────────────────────(git pull)──────────────────────────────────
                    ←──────────────────────────(git clone)─────────────────────────────────
```

| 영역 | 설명 |
|---|---|
| **Working Directory** | 실제 파일을 편집하는 작업 공간 |
| **Staging Area** | `git add`로 커밋할 파일을 선택·준비하는 대기 장소 |
| **Local Repository** | `git commit`으로 변경사항이 버전으로 영구 기록된 로컬 저장소 |
| **Remote Repository** | `git push`로 업로드한 원격 저장소 (GitHub 등) |

---

**주요 Git 명령어**

| 명령어 | 설명 |
|---|---|
| `git init` | 현재 디렉토리를 Git 저장소로 초기화 |
| `git status` | 변경된 파일 목록 확인 (습관적으로 실행) |
| `git add <파일>` | 파일을 Staging Area에 올리기 |
| `git commit -m "메시지"` | Staging Area의 변경사항을 Local Repository에 기록 |
| `git push` | Local Repository의 커밋을 Remote Repository로 업로드 |
| `git pull` | Remote Repository의 최신 변경사항을 로컬로 가져오기 |
| `git clone <URL>` | 원격 저장소를 로컬에 복제 |
| `git log --oneline --graph` | 커밋 이력을 한 줄 그래프로 확인 |

---

**브랜치 (Branch)**
> "메인 줄기에서 뻗어 나온 독립된 작업 공간"

기존 코드에 영향을 주지 않고 **새로운 기능이나 수정 작업을 독립적으로 진행**할 수 있는 작업 흐름입니다.

```bash
git branch feature/login    # 브랜치 생성
git switch feature/login    # 브랜치 이동
git switch main             # main으로 복귀
git merge feature/login     # feature 브랜치를 main에 병합
git branch -d feature/login # 병합 완료 후 브랜치 삭제
```

---

**Pull Request (PR)**
GitHub에서 **브랜치의 변경사항을 main에 병합하기 전에 팀원에게 코드 리뷰를 요청**하는 협업 메커니즘입니다. 팀 프로젝트에서 코드 품질 관리의 핵심 도구입니다.
- IntelliJ 내에서 PR 열람·댓글·승인이 가능 (별도 브라우저 불필요)

---

**Collaborator (협업자)**
GitHub 저장소에 **push 권한을 부여받은 팀원**입니다. 팀 저장소 생성 후 Settings → Collaborators에서 추가합니다.

---

**Personal Access Token (PAT)**
GitHub 계정 비밀번호 대신 사용하는 **API 인증 토큰**입니다. HTTPS 방식으로 push/pull 시 비밀번호 자리에 입력합니다.
- GitHub → Settings → Developer Settings → Personal access tokens에서 생성

---

## 4. 비교 정리

| 구분 | Git | GitHub |
|---|---|---|
| 종류 | 로컬 버전 관리 도구 | 클라우드 저장소 플랫폼 |
| 인터넷 | 불필요 | 필수 |
| 주요 기능 | init, add, commit, branch, merge | PR, Issue, Actions |
| 작업 흐름 | 수정 → 선택 → 기록 | 공유 → 협업(PR) → 백업 |

| 구분 | Gradle | Maven |
|---|---|---|
| 설정 파일 | `build.gradle` (Groovy/Kotlin DSL) | `pom.xml` (XML) |
| 빌드 속도 | 빠름 (Incremental Build & Cache) | 느림 (순차 실행) |
| 유연성 | 높음 | 정형화 |
| 이 과목 | **표준** | 참고만 |

| 구분 | H2 | MySQL |
|---|---|---|
| 종류 | 내장형 (Embedded) | 독립 실행형 (Standalone) |
| 데이터 보존 | 앱 종료 시 삭제 (인메모리 모드) | 디스크 영구 저장 |
| 설치 | Spring Boot 의존성만 추가 | 별도 설치 + 포트(3306) 설정 |
| 용도 | 개발·테스트 환경 | 운영(Production) 환경 |
