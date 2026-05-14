# Lab 06 — W10 과제: 화면설계서 + API 명세서 작성 가이드

> W08 요구사항 정의서 → W09 ERD → **W10 화면설계서 + API 명세서** → W11 WBS

## 시작 전 체크

- [ ] W08 요구사항 정의서(`docs/W08_요구사항_정의서.md`) 확인
- [ ] W09 ERD(`docs/W09_ERD.md`) 작성 완료
- [ ] 팀 저장소의 `docs/` 폴더 존재

## 과제 템플릿 위치

```
sw-framework-demo/docs/
├── template/
│   ├── W10_화면설계서_템플릿.md         ← Markdown 템플릿 (Part 1)
│   └── W10_API_명세서_템플릿.md         ← Markdown 템플릿 (Part 2)
├── assignment/
│   └── W10_화면설계서_API명세_템플릿.docx   ← 통합 docx (제출용)
├── W10_화면설계서.md                    ← 팀별 작성 결과 (Part 1)
└── W10_API_명세서.md                    ← 팀별 작성 결과 (Part 2)
```

> md / docx 둘 다 가능 — 산출물 파일명: `화면설계서_API명세_팀명.docx`

---

## Part 1 — 화면설계서 (Screen Design / Wireframe)

### 작성 순서 (예상 25분)

1. **(3분)** 템플릿 복사 → `docs/W10_화면설계서.md`
2. **(5분)** 화면 목록 작성 — W08 FR 기능 ID와 매핑하여 5~8개
3. **(7분)** 화면 흐름도 — Mermaid `flowchart LR` 또는 Figma 이미지
4. **(7분)** 주요 화면 2~3개 상세 명세 (UI 요소 + 와이어프레임)
5. **(3분)** 공통 레이아웃(네비게이션 바·푸터) 정의

### 화면 흐름도 예시

```mermaid
flowchart LR
    S001[로그인 /login] -->|성공| S003[목록 /board/list]
    S001 -->|회원가입| S002[회원가입 /signup]
    S003 -->|글 클릭| S004[상세 /board/detail/{id}]
    S003 -->|작성 버튼| S005[작성 /board/create]
    S004 -->|수정| S006[수정 /board/edit/{id}]
    S005 -->|저장| S003
    S006 -->|저장| S004
```

### 화면 상세 명세 (5개 필수 항목)

| 항목 | 내용 |
|---|---|
| 화면명 | S-003 게시판 목록 |
| URL | `GET /board/list?page=1&searchType=title&keyword=` |
| 접근 권한 | 로그인 사용자 (LoginInterceptor) |
| UI 요소 | 검색 폼·테이블·페이징·작성 버튼 |
| 데이터 | `Model: List<BoardDTO> boardList · SearchDTO search · PageDTO page` |

### 와이어프레임 (ASCII)

```
┌──────────────────────────────────────────────────────────────────┐
│  [Logo]  게시판        [홍길동님]  [로그아웃]                       │
├──────────────────────────────────────────────────────────────────┤
│  ┌────────┐ ┌─────────────────────┐ ┌──────┐  ┌─── 새 글 작성 ──┐ │
│  │ 제목 ▼ │ │ 검색어                │ │ 검색 │  │                 │ │
│  └────────┘ └─────────────────────┘ └──────┘  └─────────────────┘ │
│                                                                    │
│  ┌──────┬──────────────────┬──────────┬──────────────┬─────────┐ │
│  │  ID  │ 제목              │ 작성자    │ 작성일        │ 조회수   │ │
│  ├──────┼──────────────────┼──────────┼──────────────┼─────────┤ │
│  │  10  │ Spring Boot 자동..│ admin    │ 2026-05-08   │   42    │ │
│  └──────┴──────────────────┴──────────┴──────────────┴─────────┘ │
│                                                                    │
│              ◀ 이전   1  2  3  ...  10  다음 ▶                     │
└──────────────────────────────────────────────────────────────────┘
```

---

## Part 2 — API 명세서 (API Specification)

### 작성 순서 (예상 25분)

1. **(3분)** 템플릿 복사 → `docs/W10_API_명세서.md`
2. **(7분)** API 엔드포인트 목록 — 10개 이상 (Method + URL + Controller 메서드)
3. **(10분)** 주요 POST API 3~5개 상세 명세 (요청 + 응답 + 에러 케이스)
4. **(5분)** W08 FR ID와 매핑하여 추적성 확보

### API 엔드포인트 표 예시

| Method | URL | Controller 메서드 | 설명 | W08 FR ID |
|---|---|---|---|---|
| GET | `/login` | `loginForm()` | 로그인 폼 | FR-002 |
| POST | `/login` | `login()` | 로그인 처리 (PRG) | FR-002 |
| POST | `/logout` | `logout()` | 로그아웃 | FR-003 |
| GET | `/board/list` | `list()` | 목록 (페이징·검색) | FR-007, FR-011 |
| GET | `/board/detail/{id}` | `detail()` | 상세 | FR-008 |
| GET | `/board/create` | `createForm()` | 작성 폼 | FR-006 |
| POST | `/board/create` | `create()` | 작성 처리 (PRG) | FR-006 |
| GET | `/board/edit/{id}` | `editForm()` | 수정 폼 (작성자만) | FR-009 |
| POST | `/board/edit/{id}` | `edit()` | 수정 처리 (PRG) | FR-009 |
| POST | `/board/delete/{id}` | `delete()` | 삭제 (작성자/관리자) | FR-010 |

### 상세 명세 형식

#### POST /board/create

| 항목 | 내용 |
|---|---|
| Method | POST |
| URL | `/board/create` |
| Content-Type | `application/x-www-form-urlencoded` |
| 인증 | 로그인 필수 (LoginInterceptor) |

**요청 파라미터:**

| 이름 | 타입 | 필수 | 검증 | 설명 |
|---|---|---|---|---|
| title | string | ✅ | `@NotBlank @Size(2~200)` | 제목 |
| content | string | ✅ | `@NotBlank @Size(min=5)` | 내용 |
| author | (생략) | — | 세션에서 자동 주입 | 작성자 (위조 방어) |

**응답 (성공):**

```
HTTP/1.1 302 Found
Location: /board/list
```

**응답 (검증 실패):**

```
HTTP/1.1 200 OK
Content-Type: text/html
→ board/form.html 재렌더링 + 에러 메시지 + 입력값 보존
```

**응답 (서버 오류):**

```
HTTP/1.1 200 OK  (View 렌더링)
→ error/500.html  (GlobalExceptionHandler가 처리)
```

---

## 산출물 체크리스트

### `docs/W10_화면설계서.md` (Part 1)

- [ ] 화면 목록 5~8개 (ID · URL · 접근 권한 · FR ID)
- [ ] 화면 흐름도 (Mermaid 또는 이미지)
- [ ] 주요 화면 2~3개 상세 명세
- [ ] 공통 레이아웃 (네비게이션 바·푸터)

### `docs/W10_API_명세서.md` (Part 2)

- [ ] API 엔드포인트 10개 이상
- [ ] 주요 POST API 3~5개 상세 명세 (요청·응답·에러)
- [ ] W08 FR ID 매핑 (추적성)
- [ ] PRG 패턴 명시 (POST 후 302 Redirect)

### Git

- [ ] `docs/W10_화면설계서.md` Push
- [ ] `docs/W10_API_명세서.md` Push
- [ ] 또는 통합 `.docx` 제출

---

## 다음 주(W11) 활용

- 본 API 명세서가 **W11 페이징 구현**의 청사진
- 화면설계서의 페이징 UI가 **W11 PageDTO + LIMIT/OFFSET**로 구현됨
- W11 산출물은 **WBS** (`W11_WBS_템플릿.xlsx`)

## 작성 팁

- **과도하게 자세히 X** — 화면 5~8개 / API 10개면 충분
- **추적성 우선** — W08 FR ID와 모든 화면·API가 매핑되어야
- **Mermaid 권장** — GitHub에서 자동 렌더링 + 코드처럼 diff 관리 가능
- **ASCII 와이어프레임** — Figma 없어도 충분, 손글씨처럼 빠르게
- **API 명세는 Postman/HTTP Client 시나리오와 일치**시켜 W11 TDD 준비
