# =============================================================
# Week 02 — 개발환경 확인 스크립트 (Windows PowerShell)
# 패키지명: kr.ac.tukorea.swframework
# Spring Boot 3.x + Java 21
# =============================================================

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host " SW프레임워크 개발환경 확인 스크립트" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Java 버전 확인
Write-Host "[1/4] Java 확인..." -ForegroundColor Yellow
try {
    $javaVer = java -version 2>&1 | Select-Object -First 1
    Write-Host "  ✅ Java 설치됨: $javaVer" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Java가 설치되지 않았습니다. JDK 21을 설치하세요." -ForegroundColor Red
}
Write-Host ""

# 2. JAVA_HOME 확인
Write-Host "[2/4] JAVA_HOME 확인..." -ForegroundColor Yellow
if ($env:JAVA_HOME) {
    Write-Host "  ✅ JAVA_HOME=$env:JAVA_HOME" -ForegroundColor Green
} else {
    Write-Host "  ⚠️  JAVA_HOME이 설정되지 않았습니다." -ForegroundColor DarkYellow
    Write-Host "     시스템 속성 > 환경 변수 > 시스템 변수 > 새로 만들기"
    Write-Host '     변수 이름: JAVA_HOME'
    Write-Host '     변수 값: C:\Program Files\Java\jdk-21'
}
Write-Host ""

# 3. Git 버전 확인
Write-Host "[3/4] Git 확인..." -ForegroundColor Yellow
try {
    $gitVer = git --version 2>&1
    Write-Host "  ✅ Git 설치됨: $gitVer" -ForegroundColor Green

    # Git 사용자 정보 확인
    $gitUser = git config --global user.name 2>$null
    $gitEmail = git config --global user.email 2>$null
    if ($gitUser -and $gitEmail) {
        Write-Host "  ✅ Git 사용자: $gitUser <$gitEmail>" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️  Git 사용자 정보가 설정되지 않았습니다." -ForegroundColor DarkYellow
        Write-Host '     git config --global user.name "홍길동"'
        Write-Host '     git config --global user.email "id@tukorea.ac.kr"'
    }
} catch {
    Write-Host "  ❌ Git이 설치되지 않았습니다." -ForegroundColor Red
}
Write-Host ""

# 4. MySQL 확인
Write-Host "[4/4] MySQL 확인..." -ForegroundColor Yellow
try {
    $mysqlVer = mysql --version 2>&1
    Write-Host "  ✅ MySQL 설치됨: $mysqlVer" -ForegroundColor Green
} catch {
    Write-Host "  ❌ MySQL이 설치되지 않았습니다." -ForegroundColor Red
    Write-Host "     https://dev.mysql.com/downloads/installer/ 에서 다운로드"
}
Write-Host ""

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host " 확인 완료! 모든 항목이 ✅인지 확인하세요." -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
