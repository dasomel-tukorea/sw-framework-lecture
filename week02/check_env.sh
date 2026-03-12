#!/bin/bash
# =============================================================
# Week 02 — 개발환경 확인 스크립트 (macOS / Linux)
# 패키지명: kr.ac.tukorea.swframework
# Spring Boot 3.x + Java 21
# =============================================================

echo "========================================="
echo " SW프레임워크 개발환경 확인 스크립트"
echo "========================================="
echo ""

# 1. Java 버전 확인
echo "[1/4] Java 확인..."
if command -v java &> /dev/null; then
    JAVA_VER=$(java -version 2>&1 | head -1)
    echo "  ✅ Java 설치됨: $JAVA_VER"
else
    echo "  ❌ Java가 설치되지 않았습니다. JDK 21을 설치하세요."
fi
echo ""

# 2. JAVA_HOME 확인
echo "[2/4] JAVA_HOME 확인..."
if [ -n "$JAVA_HOME" ]; then
    echo "  ✅ JAVA_HOME=$JAVA_HOME"
else
    echo "  ⚠️  JAVA_HOME이 설정되지 않았습니다."
    echo "     ~/.zshrc 또는 ~/.bashrc에 다음을 추가하세요:"
    echo '     export JAVA_HOME="$(brew --prefix openjdk@21)"'
fi
echo ""

# 3. Git 버전 확인
echo "[3/4] Git 확인..."
if command -v git &> /dev/null; then
    GIT_VER=$(git --version)
    echo "  ✅ Git 설치됨: $GIT_VER"

    # Git 사용자 정보 확인
    GIT_USER=$(git config --global user.name 2>/dev/null)
    GIT_EMAIL=$(git config --global user.email 2>/dev/null)
    if [ -n "$GIT_USER" ] && [ -n "$GIT_EMAIL" ]; then
        echo "  ✅ Git 사용자: $GIT_USER <$GIT_EMAIL>"
    else
        echo "  ⚠️  Git 사용자 정보가 설정되지 않았습니다."
        echo '     git config --global user.name "홍길동"'
        echo '     git config --global user.email "id@tukorea.ac.kr"'
    fi
else
    echo "  ❌ Git이 설치되지 않았습니다."
fi
echo ""

# 4. MySQL 확인
echo "[4/4] MySQL 확인..."
if command -v mysql &> /dev/null; then
    MYSQL_VER=$(mysql --version 2>&1)
    echo "  ✅ MySQL 설치됨: $MYSQL_VER"
else
    echo "  ❌ MySQL이 설치되지 않았습니다."
    echo "     brew install mysql"
fi
echo ""

echo "========================================="
echo " 확인 완료! 모든 항목이 ✅인지 확인하세요."
echo "========================================="
