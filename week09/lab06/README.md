# Lab 06 — 팀 프로젝트 ERD 작성 (필수 과제)

> W08 요구사항 정의서 → **W09 ERD** → W10 Mapper XML로 이어지는 1차 산출물

## 파일

| 파일 | 설명 |
|---|---|
| `W09_ERD_가이드.md` | 5단계 작성 흐름 + Mermaid 예시 + DDL 규칙 + 체크리스트 |

## 과제 템플릿 위치

```
sw-framework-demo/docs/
├── template/
│   └── W09_ERD_템플릿.md                       ← Markdown 템플릿
└── assignment/
    └── W09_ERD_테이블정의서_템플릿.docx          ← 과제 제출용 .docx
```

> md / docx 둘 다 가능 — 산출물 파일명: `ERD_테이블정의서_팀명.docx` 또는 `docs/W09_ERD.md`

## 작성 순서 (요약)

1. **템플릿 복사** — 팀 저장소의 `docs/W09_ERD.md` (또는 .docx)
2. **명사 추출** — W08 요구사항 정의서에서 엔티티 후보 선정
3. **엔티티 검증** — 독립성·속성·관계·최소 2개 이상
4. **속성 정의** — 컬럼명·타입·NULL·KEY (PK/FK/UK)
5. **관계 설정** — `||--o{` (1:N) / `}o--o{` (M:N) / `||--||` (1:1)

> 자세한 흐름은 `W09_ERD_가이드.md` 참고.

## 산출물 체크리스트

### `docs/W09_ERD.md` 또는 `.docx`
- [ ] 프로젝트명·팀명·DB 버전·테이블 수 명시
- [ ] Mermaid `erDiagram` 코드 + GitHub 렌더링 확인
- [ ] 관계 설명 표 (1:1 / 1:N / N:M 구분)
- [ ] 테이블별 컬럼 정의 표 (NULL / KEY / 설명)
- [ ] DDL 스크립트 (MySQL 8.0+)

### `sql/schema.sql`
- [ ] DDL 실행 테스트 완료 (MySQL에서 오류 없이 모든 테이블 생성)
- [ ] FK 관계 정상 동작
- [ ] `utf8mb4` + `InnoDB` 적용

### Git
- [ ] `docs/W09_ERD.md` Push
- [ ] `sql/schema.sql` Push
- [ ] **DB 비밀번호 노출 없음** 확인 (`git diff`)

## 다음 주(W10) 활용

- `docs/W09_ERD.md`의 **테이블 → MyBatis Mapper XML** 변환
- 1개 핵심 엔티티에 대해 CRUD + 검색 구현
- `@ControllerAdvice`로 전역 예외 처리 추가

> 1:10:100 법칙 — 오늘 ERD 한 줄 잘못 그으면 W14 발표에서 100배 비싸진다.
