# Lab 06 — 팀 프로젝트 WBS 작성 (필수 과제)

> W08 요구사항 → W09 ERD → W10 화면·API → **W11 WBS (간트차트)** → W12~W15 구현·발표

## 파일

| 파일 | 설명 |
|---|---|
| `W11_WBS_가이드.md` | 작성 가이드 (Phase별 분해 + 간트차트) |

## 과제 템플릿 위치

```
sw-framework-demo/docs/
├── template/
│   └── W11_WBS_템플릿.md                    ← Markdown 템플릿
├── assignment/
│   └── W11_WBS_템플릿.xlsx                  ← 과제 제출용 .xlsx (5개 시트)
└── W11_WBS.md                                ← 팀별 작성 결과 (선택 사본)
```

> md / xlsx 둘 다 가능 — 산출물: `W11_WBS_팀명.xlsx` (필수) + `docs/W11_WBS.md` (GitHub 렌더링용 사본)

## xlsx 5개 시트 구성

| # | 시트명 | 내용 |
|---|---|---|
| 1 | 일정 개요 | 프로젝트명·팀명·기술 스택·기간·주차별 마일스톤 |
| 2 | **WBS 간트차트** | 작업 ID · 작업명 · 담당 · 시작·종료 · 산출물 · 상태 |
| 3 | 팀원별 작업량 집계 | 인원별 작업 수·분야 (백엔드/프론트/DB/배포) |
| 4 | 리스크 관리 | 예상 리스크 · 영향도 · 대응책 |
| 5 | 회의·협업 규칙 | 정기 회의·코드 리뷰·커밋 컨벤션 |

## 작성 순서 (예상 50분)

| 단계 | 시간 | 작업 |
|---|---|---|
| 1 | 5분 | 템플릿 다운로드 → `W11_WBS_팀명.xlsx`로 저장 + Sheet 1 채우기 |
| 2 | 15분 | Sheet 2 (간트차트) — Phase 5~6개로 작업 분해 |
| 3 | 10분 | 각 작업의 시작·종료 주차·담당자 배정 |
| 4 | 5분 | Sheet 3 (집계) — 팀원별 작업 수 균형 확인 |
| 5 | 5분 | Sheet 4 (리스크) — 3~5개 작성 |
| 6 | 5분 | Sheet 5 (회의·규칙) — 협업 규칙 정리 |
| 7 | 5분 | Markdown 사본 → `docs/W11_WBS.md` |

## Phase 분해 예시 (게시판 프로젝트)

```
Phase 1 (W08)         프로젝트 셋업          GitHub Repo · build.gradle · ERD
Phase 2 (W09)         DB·도메인              schema.sql · Mapper · DTO
Phase 3 (W10)         CRUD + 검증·예외       Controller · Service · @Valid · @ControllerAdvice
Phase 4 (W11) ← 이번  페이징·검색·파일       PageDTO · MultipartFile · BCrypt 회원
Phase 5 (W12)         MVP 통합              Must 기능 전체 + 화면 통합
Phase 6 (W13)         Should 추가           Could 기능 / Docker (선택)
Phase 7 (W14~15)      발표 준비             시연 시나리오 + README + 발표자료
```

## 간트차트 행 형식

```
| ID  | 작업명              | 담당   | 시작     | 종료     | 산출물          | 상태 |
|-----|---------------------|--------|----------|----------|-----------------|------|
| 1   | 프로젝트 초기 셋업  | 전원   | W08 (3일)| W08 (5일)| GitHub Repo     | 완료 |
| 1.1 | GitHub Repo 생성    | 홍길동 | W08      | W08      | Repo + Invite   | 완료 |
| 1.2 | 의존성 설정         | 홍길동 | W08      | W08      | build.gradle    | 완료 |
| 4   | 페이징·파일·BCrypt  | 김영희 | W11      | W11      | student CRUD 확장 | 진행 |
| 4.1 | PageDTO 작성        | 김영희 | W11 (1일)| W11 (2일)| PageDTO.java    | 진행 |
| ... | ...                 | ...    | ...      | ...      | ...             | ... |
```

## 리스크 관리 예시 (Sheet 4)

| 리스크 | 영향도 | 가능성 | 대응책 |
|---|---|---|---|
| MySQL 환경 차이로 팀원 PC 빌드 실패 | 높음 | 중간 | H2 프로필 기본 + Docker 옵션 검토 |
| 발표 직전 git 충돌 | 중간 | 높음 | feature 브랜치 + 매주 PR 머지 |
| 파일 업로드 보안 사고 | 높음 | 낮음 | UUID 파일명 + Path Traversal 방어 + 확장자 검증 |
| 팀원 1명 결석 시 작업 지연 | 중간 | 중간 | 짝 작업(pair) · Must 기능 우선 |

## 회의·협업 규칙 예시 (Sheet 5)

- **주간 회의**: 매주 금요일 강의 후 30분 — 진척 공유 + 다음 주 작업 분배
- **코드 리뷰**: PR 1개 = 최소 1명 승인 후 머지
- **커밋 컨벤션**:
  - `feat:` 새 기능
  - `fix:` 버그 수정
  - `docs:` 문서
  - `refactor:` 리팩토링
- **브랜치 전략**: `main` (안정) ← `feature/이름-기능` (작업) ← PR
- **민감 정보**: `application-local.yml`은 `.gitignore` + Secret으로 주입

## 산출물 체크리스트

### `W11_WBS_팀명.xlsx`
- [ ] Sheet 1 일정 개요 — 프로젝트명·팀원·기간 명시
- [ ] Sheet 2 간트차트 — Phase 5~6개, 작업 15개+
- [ ] Sheet 3 집계 — 팀원별 작업 수 균형 (특정 인원에 몰림 X)
- [ ] Sheet 4 리스크 — 3~5개 + 대응책
- [ ] Sheet 5 규칙 — 회의·커밋·브랜치 명시

### Git
- [ ] `docs/assignment/W11_WBS_팀명.xlsx` Push
- [ ] `docs/W11_WBS.md` Markdown 사본 Push
- [ ] 비밀번호·토큰 노출 없음

## 1:10:100 법칙 — WBS 한 줄이 W15에 100배로

> 오늘 WBS에 빠뜨린 작업 1개 = W14 발표 직전 발견 = 100배 비용

## 주차 연결

- W08 분석 → W09 설계 → W10 화면·API → **W11 WBS (오늘)** → W12 MVP → W13 추가 → W14·15 발표
- WBS에서 정의한 작업이 GitHub Issues / 칸반 보드로 전환 가능 (선택)
