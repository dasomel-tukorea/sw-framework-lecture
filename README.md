# SW 프레임워크 강의 자료

한국공학대학교 SW 프레임워크 수업 실습 자료 저장소입니다.

## 주차별 자료

| 주차 | 주제 | 자료 |
|---|---|---|
| Week 01 | 프레임워크 개념 + Spring 핵심 (IoC/DI/AOP/PSA) | [week01/](week01/) |
| Week 02 | 개발환경 설정 + Git 기초 + 팀배정 | [week02/](week02/) |
| Week 03 | SW 프레임워크 이론 (백엔드/프론트엔드) + Spring MVC 실습 | [week03/](week03/) |
| Week 04 | IoC/DI 실습 (인터페이스 DI, @Primary, Profile) | [week04/](week04/) |
| Week 05 | AOP & Bean 실습 (Aspect, 생명주기, Scope, Pointcut) | [week05/](week05/) |
| Week 06 | Spring MVC + Thymeleaf (SSR, Form 바인딩, PRG, Bean Validation, XSS 방어, Fragment) | [week06/](week06/) |
| Week 07 | 세션 처리 & 웹 보안 기초 (HttpSession, Interceptor, BCrypt, JWT 개념, 4대 보안 위협) | [week07/](week07/) |
| Week 09 | Java DB 프로그래밍 & MyBatis (JDBC 통증 → SQL Mapper, HikariCP, `#{}/${}`, Spring Profile, 팀 ERD 1차) | [week09/](week09/) |
| Week 10 | Spring MVC 패턴 (DispatcherServlet, 3계층/DTO, PRG, `@ControllerAdvice`, `@Valid`, 팀 화면설계서/API 명세서) | [week10/](week10/) |
| Week 11 | MVC 실습 #2 — 페이징·검색·정렬 + 파일 업로드 + member 영구 저장 (PageDTO, LIMIT/OFFSET, `<sql>/<choose>`, MultipartFile/UUID, W07 `PasswordUtil` 재사용, 팀 WBS) | [week11/](week11/) |

### Week 09 상세 — Lab 구성

| Lab | 주제 | 산출물 | 시간 |
|---|---|---|---|
| 01 | 학생 CRUD 5계층 (그린필드) | Controller/Service/Mapper(I/F+XML)/Templates | 30분 |
| 02 | Spring Data JDBC → MyBatis 마이그레이션 | 본인 swframework(W06)에 적용 | 20분 |
| 03 | Dynamic SQL 5종 | `<if>` `<choose>` `<set>` `<foreach>` `<trim>` | 25분 |
| 04 | H2 프로필 전환 | application-h2.yml + Console | 20분 |
| 05 | 게시판 DTO 패턴 (선택) | BoardDTO + 페이징 | 25분 |
| 06 | 팀 ERD 작성 (과제) | docs/W09_ERD.md (Mermaid + DDL) | 20분 |

> Week 08은 팀 프로젝트 분석 단계 (FR/NFR · MoSCoW · 요구사항 정의서). Week 09는 그 분석 결과를 **테이블 설계·구현으로 옮기는 첫 주**.

### Week 10 상세 — Lab 구성

| Lab | 주제 | 산출물 | 시간 |
|---|---|---|---|
| 01 | 학생 CRUD 5계층 (그린필드 학습) | swframework 9주차 완성 코드 — 읽고 이해 | 50분 |
| 02 | 전역 예외 처리 (`@ControllerAdvice`) | `exception/` + `templates/error/` (404·500) 신규 추가 | 25분 |
| 03 | Bean Validation (`@Valid` + `BindingResult`) | `StudentForm` 검증 패턴 분석 | 25분 |
| 04 | Dynamic SQL 검색 응용 | `SearchController` + `StudentMapper.xml` (W09 lab03 확장) | 15분 |
| 05 | HttpSession + LoginInterceptor (선택) | W07 세션·인터셉터 통합 — Lab 02·03과 결합 | 15분 |
| 06 | 팀 화면설계서 + API 명세서 (과제) | docs/W10_화면설계서.md + docs/W10_API_명세서.md | 50분 |

> Week 10은 W09 MyBatis 위에 Spring MVC 패턴(3계층·DTO·PRG·`@ControllerAdvice`·`@Valid`)을 입히는 주. **DB 스키마 변화 없이** W09의 `student` 테이블을 그대로 재사용하며, 신규 코드는 Lab 02(`exception/` + `templates/error/`)뿐.

### Week 11 상세 — Lab 구성

| Lab | 주제 | 산출물 | 시간 |
|---|---|---|---|
| 01 | 학생 페이징 (PageDTO + LIMIT/OFFSET) | `PageDTO` + `StudentMapper.findAllWithPaging`/`countAll` | 30분 |
| 02 | 검색 + 정렬 통합 | `<sql id="searchCondition">` + `<choose>` ORDER BY 화이트리스트 | 25분 |
| 03 | 블록 페이징 UI | startPage/endPage/blockSize 4공식 + Thymeleaf 네비게이션 | 20분 |
| 04 | 학생 자료 첨부 업로드/다운로드 | `MultipartFile` + UUID 저장명 + `Content-Disposition` | 25분 |
| 05 | member 테이블 + BCrypt 회원 (선택) | `MemberDTO` + **W07 `PasswordUtil` 재사용** + LoginController 통합 | 20분 |
| 06 | 팀 WBS 작성 (과제) | docs/W11_WBS.md + assignment/W11_WBS_템플릿.xlsx | 50분 |

> Week 11은 W10까지 완성된 student CRUD 위에 **페이징·검색·정렬·파일 업로드**를 얹어 실무 수준으로 끌어올리는 주. `member` 테이블을 신설해 W07 메모리 회원을 영구화하되 **새 Bean을 만들지 않고 W07 `PasswordUtil`의 정적 메서드를 그대로 재사용**한다. 팀 프로젝트는 남은 W11~W15 일정을 WBS로 분해.

## 기술 스택

- **언어**: Java 21 (LTS)
- **프레임워크**: Spring Boot 3.x
- **빌드 도구**: Gradle 8.x
- **데이터베이스**: H2 (실습) / MySQL 8.x (배포)
- **IDE**: IntelliJ IDEA
- **버전 관리**: Git / GitHub
