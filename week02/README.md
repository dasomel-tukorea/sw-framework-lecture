# Week 02 — 개발환경 설정 + Git 기초

## 이번 주 목표

- JDK 21, IntelliJ IDEA, Gradle, MySQL 8.x 설치 및 동작 확인
- Git 기본 명령어 학습 (init, add, commit, push, pull)
- 팀 배정 완료

## 체크리스트

- [ ] JDK 21 설치 확인: `java -version`
- [ ] IntelliJ IDEA Ultimate 학생 라이선스 활성화
- [ ] MySQL 8.x 접속 확인
- [ ] Git 설치 확인: `git --version`
- [ ] GitHub 계정 생성 및 팀 Repository 생성
- [ ] `.gitignore` 파일 커밋

## 파일 목록

| 파일명 | 설명 |
|---|---|
| `check_env.sh` | macOS/Linux 환경 확인 스크립트 |
| `check_env.ps1` | Windows PowerShell 환경 확인 스크립트 |
| `git_setup.sh` | Git 초기 설정 및 기본 명령어 모음 |
| `TeamAssigner.java` | 시드 기반 랜덤 팀 배정 프로그램 |
| `students.txt` | 팀 배정 대상 학생 명단 |
| `docs/install.md` | 개발환경 상세 설치 가이드 |

## 실행 방법

### 환경 확인 스크립트

**macOS / Linux**
```bash
chmod +x check_env.sh
./check_env.sh
```

**Windows (PowerShell)**
```powershell
.\check_env.ps1
```

### Git 초기 설정

```bash
# git_setup.sh 내용을 참고하여 순서대로 실행
# 스크립트를 직접 실행하지 말고, 명령어를 하나씩 복사하여 수행할 것
```

## 참고

- 팀 배정 후 GitHub Organization/Repository 생성
- `.gitignore` 설정 (IntelliJ, Gradle, OS 파일 제외)
- 저장소 초기 구조: `README.md`, `.gitignore`, `docs/` 디렉토리 생성 권장
- 상세 설치 가이드: [`docs/install.md`](docs/install.md)
