# Week 11 — MVC 실습 #2 · 페이징 처리 + 파일 업로드

> "W10까지 완성된 학생 CRUD에 페이징·검색·정렬·파일 업로드를 더해 실무 수준 완성"
>
> **베이스**: `/Users/m/Documents/IdeaProjects/40.tukorea/swframework` — student 도메인 (10주차까지 완성)

---

## 핵심 결정 — **student 도메인 그대로 확장**

W10까지 완성된 swframework의 student 도메인 위에 페이징·검색·정렬·파일 업로드를 더해 실무 수준으로 끌어올립니다.

| 항목 | swframework 현황 | W11에서 추가 |
|---|---|---|
| student 테이블 | ✓ (DataInitializer 3건) | **30건+ 추가 데이터** (페이징 테스트용) |
| StudentForm | ✓ @Valid 적용 | 변경 없음 |
| StudentController | ✓ CRUD | `?page=2&size=10` 파라미터 |
| StudentMapper | ✓ findAll/findBySearchType | **PageDTO + findAllWithPaging / countAll** |
| 파일 업로드 | 없음 | student 테이블에 `attachment_name`·`saved_name` 컬럼 추가 |
| 회원/로그인 | LoginController + ConcurrentHashMap (W07) | **member 테이블 영구 저장 + W07 `PasswordUtil` 재사용 (선택)** |

---

## 이번 주 학습 목표

| # | 목표 | 관련 Lab |
|---|------|---------|
| 1 | 페이징(Pagination) 필요성 + LIMIT/OFFSET 원리 | Lab 01 |
| 2 | PageDTO 설계 + `<sql>`/`<include>` 검색 조건 재사용 | Lab 01 |
| 3 | 정렬(Sorting) — `<choose>` 화이트리스트(name/student_id/major/created_at) | Lab 02 |
| 4 | 블록 페이징 UI (1-10 / 11-20 ...) | Lab 03 |
| 5 | 파일 업로드/다운로드 (`MultipartFile` + UUID) — 학생 자료 첨부 | Lab 04 |
| 6 | 회원가입 + W07 `PasswordUtil` 재사용 + member 테이블 영구 저장 | Lab 05 (선택) |
| 7 | 팀 프로젝트 WBS 작성 (W11 과제) | Lab 06 + 과제 |

---

## 4~11주차 연결 지도

```
W04~W07 IoC/DI · AOP · View · 세션 보안
                  ↓
W08 분석     → W08_요구사항_정의서.docx
W09 ERD      → W09_ERD_테이블정의서.docx
W10 화면·API → W10_화면설계서_API명세.docx
W11 구현 + WBS  ← 이번 주
   · 학생 페이징·검색·정렬 · 자료 첨부 · W07 PasswordUtil 재사용
   · W11_WBS.xlsx (간트차트)
                  ↓
W12 배포 · W13 품질 · W14·W15 발표
```

---

## 사전 준비 (swframework)

### 1. student 테이블 — 페이징 테스트용 30건+ 추가

```sql
-- swframework/src/main/resources/sql/data-w11.sql (선택적 보강)
INSERT INTO student (name, student_id, email, major) VALUES
('박지성',  '202300004','park@tukorea.ac.kr','IT경영'),
('손흥민',  '202300005','son@tukorea.ac.kr', 'IT경영'),
('김민재',  '202300006','min@tukorea.ac.kr', '컴퓨터공학'),
-- ... 30건+ (페이징 테스트용)
;
```

> 또는 swframework의 `DataInitializer`에 페이징 테스트 데이터 30건 자동 삽입 로직 추가.

### 2. student 테이블에 파일 컬럼 추가 (Lab 04용)

```sql
ALTER TABLE student
  ADD COLUMN attachment_name  VARCHAR(255) NULL,    -- 원본 파일명 (예: '학생증.pdf')
  ADD COLUMN saved_name       VARCHAR(255) NULL;    -- UUID 저장 파일명
```

### 3. member 테이블 (Lab 05, 선택)

```sql
CREATE TABLE member (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    login_id  VARCHAR(50)  NOT NULL UNIQUE,          -- 로그인 키 (W07 UserRepository 호환)
    password  VARCHAR(255) NOT NULL,                 -- BCrypt 해시 (60자+ 여유)
    name      VARCHAR(50)  NOT NULL,
    email     VARCHAR(100) NULL,                     -- 선택 입력
    role      VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

> **Lab 05 핵심**: 새 `BCryptPasswordEncoder` Bean을 만들지 말고, **W07에서 작성한 `kr.ac.tukorea.swframework.util.PasswordUtil`** 의 정적 메서드 `encode()` / `matches()` 를 그대로 재사용합니다.

### 4. application.yaml — 파일 업로드 + 페이징 기본값

```yaml
spring:
  servlet:
    multipart:
      max-file-size:    10MB
      max-request-size: 20MB

file:
  upload-dir: ./uploads          # 운영에서는 절대 경로 또는 S3
```

---

## Lab 흐름

| Lab | 주제 | 핵심 산출물 | 예상 시간 |
|---|---|---|---|
| 01 | 학생 페이징 (PageDTO + LIMIT/OFFSET) | PageDTO + StudentMapper `findAllWithPaging`/`countAll` | 30분 |
| 02 | 검색 + 정렬 통합 | `<sql id="searchCondition">` + `<choose>` ORDER BY | 25분 |
| 03 | 블록 페이징 UI | startPage/endPage/blockSize 4공식 + Thymeleaf | 20분 |
| 04 | 학생 자료 첨부 업로드/다운로드 | MultipartFile + UUID + Content-Disposition | 25분 |
| 05 | member 테이블 + BCrypt 회원 (선택) | MemberDTO + **W07 `PasswordUtil` 재사용** + LoginController 통합 | 20분 |
| 06 | **팀 WBS 작성** (필수 과제) | docs/W11_WBS.md + assignment/W11_WBS_템플릿.xlsx | 50분 |

---

## 핵심 공식

### 페이징 OFFSET
```
offset = (page - 1) * size

page=1, size=10  →  offset=0   (1~10번 학생)
page=2, size=10  →  offset=10  (11~20번 학생)
page=3, size=10  →  offset=20  (21~30번 학생)

totalPages = Math.ceil(totalCount / size)
hasPrev    = page > 1
hasNext    = page < totalPages
```

### 블록 페이징 (10페이지씩 묶음)
```
blockSize     = 10
currentBlock  = (page - 1) / blockSize          // 0-based 블록 인덱스
startPage     = currentBlock * blockSize + 1
endPage       = Math.min(startPage + blockSize - 1, totalPages)

hasPrevBlock  = currentBlock > 0
hasNextBlock  = endPage < totalPages
prevBlockPage = Math.max(startPage - 1, 1)
nextBlockPage = endPage + 1
```

예) totalPages=37, page=23, blockSize=10
  → currentBlock=2, startPage=21, endPage=30, hasPrev=true, hasNext=true

---

## swframework 주요 URL (W11 적용 후)

| Method | URL | 설명 | W11 변화 |
|---|---|---|---|
| GET | `/students` | 학생 목록 | **`?page=2&size=10&searchType=name&keyword=홍&sortBy=name`** |
| GET | `/students/{id}` | 학생 상세 | 첨부파일 다운로드 링크 추가 |
| GET | `/students/new` | 등록 폼 | 파일 업로드 input 추가 |
| POST | `/students` | 등록 (PRG) | `MultipartFile` 처리 추가 |
| GET | `/students/{id}/edit` | 수정 폼 | 동일 |
| POST | `/students/{id}/edit` | 수정 | 동일 |
| GET | `/students/search` | 검색 (Lab 04 W10) | 페이징 통합 |
| GET | `/students/by-ids?ids=1,2,3` | 다건 조회 | 동일 |
| **GET** | **`/students/attachment/{savedName}`** | **첨부파일 다운로드 (신규)** | 신규 |
| POST | `/login` | 로그인 (W07) | **`PasswordUtil.matches()` 검증 (Lab 05 적용 시)** |
| GET/POST | `/members/signup` | 회원가입 (Lab 05) | `PasswordUtil.encode()` + member INSERT |
| GET/POST | `/members/login` | 회원 로그인 (Lab 05) | `findByLoginId()` + `PasswordUtil.matches()` |
| POST | `/members/logout` | 로그아웃 (Lab 05) | 세션 무효화 |

---

## 과제

### 기본 과제 — 학생 페이징·검색·정렬
- [ ] Lab 01~03 완성 → 학생 목록 10건씩 + 검색 + 정렬 동시 동작
- [ ] **검색·정렬 조건이 페이지 이동 시 유지**되어야 함 (querystring 보존)
- [ ] LIMIT/OFFSET이 SQL에 정확히 적용 (콘솔 로그 확인)
- [ ] (Lab 04) 학생 자료 첨부 업로드/다운로드 동작
- [ ] GitHub Push (실행 가능 상태)
- [ ] 실행 화면 캡처 3장 (페이징 + 검색 결과 + 파일 첨부)

### 심화 과제 — 팀 WBS 작성 (필수)
- [ ] 템플릿 활용: `sw-framework-demo/docs/assignment/W11_WBS_템플릿.xlsx`
- [ ] 시트 5개 모두 작성: 일정 개요 · WBS 간트차트 · 팀원별 집계 · 리스크 · 협업 규칙
- [ ] 남은 5주(W11~W15) 작업을 **Phase별로 분해** — 작업당 시작일·종료일·담당·산출물·상태
- [ ] `docs/W11_WBS.md`에도 Markdown 사본 업로드 (GitHub 자동 렌더링)
- [ ] 팀 저장소 Push

### 제출
- **마감**: 다음 주차(W12) 수업 시작 전
- **방법**: e-class + GitHub Push (실행 가능한 상태로 제출)
- **금지**: 타인 코드 복붙 / AI 코드 전체 사용 / 비밀번호 노출

---

## 참고 자료

- [MyBatis Dynamic SQL 한국어](https://mybatis.org/mybatis-3/ko/dynamic-sql.html)
- [Spring Framework — MultipartResolver](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/multipart.html)
- [Spring Security Crypto — BCryptPasswordEncoder](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html) (W07 PasswordUtil 내부 구현)
- **swframework** (student 도메인 · 10주차까지 완성 — `domain/Student`, `mapper/StudentMapper`, `util/PasswordUtil`)
- 전자정부 표준프레임워크 실행환경 교육교재 — 화면처리
- 코딩 자율학습 스프링 부트 (홍팍 저, 길벗)
