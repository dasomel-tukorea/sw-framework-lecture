#!/bin/bash
# =============================================================
# Week 02 — Git 초기 설정 및 기본 명령어 모음
# 패키지명: kr.ac.tukorea.swframework
# 주의: 이 파일을 직접 실행하지 마세요!
#       각 명령어를 하나씩 복사하여 터미널에서 실행하세요.
# =============================================================

# ─────────────────────────────────────
# 1단계. Git 설치 확인
# ─────────────────────────────────────
git --version
# 출력 예시: git version 2.43.0

# ─────────────────────────────────────
# 2단계. 사용자 정보 등록 (커밋 기록에 남음)
# ─────────────────────────────────────
git config --global user.name "홍길동"           # 본인 이름으로 변경
git config --global user.email "id@tukorea.ac.kr" # 본인 학교 이메일로 변경

# 설정 확인
git config --global --list

# ─────────────────────────────────────
# 3단계. 로컬 저장소 생성 → 첫 커밋
# ─────────────────────────────────────
mkdir hello-git && cd hello-git
git init

# 파일 생성 및 스테이징
echo "# Hello Git" > README.md
git status            # 변경 파일 확인 (습관적으로 add 전에 실행)
git add README.md
git commit -m "docs: add README"

# ─────────────────────────────────────
# 4단계. GitHub 원격 저장소 연결 → Push
# ─────────────────────────────────────
# GitHub에서 'hello-git' 저장소를 먼저 생성한 후 실행
git remote add origin https://github.com/<your-id>/hello-git.git
git push -u origin main
# -u 옵션: 이후 git push만 입력해도 origin/main으로 자동 연결

# ─────────────────────────────────────
# 5단계. 이력 확인
# ─────────────────────────────────────
git log --oneline --graph --decorate
git status

# ─────────────────────────────────────
# 6단계. 브랜치 생성 · 병합
# ─────────────────────────────────────
# 기능 브랜치 생성 및 이동
git branch feature/login
git switch feature/login

# 파일 수정 후 커밋
echo "로그인 기능 추가" >> README.md
git add README.md
git commit -m "feat: add login description"

# main으로 돌아와서 병합
git switch main
git merge feature/login

# 병합 완료 후 브랜치 삭제
git branch -d feature/login

# 병합 상태 확인
git log --oneline --graph
