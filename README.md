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

## 기술 스택

- **언어**: Java 21 (LTS)
- **프레임워크**: Spring Boot 3.x
- **빌드 도구**: Gradle 8.x
- **데이터베이스**: H2 (실습) / MySQL 8.x (배포)
- **IDE**: IntelliJ IDEA
- **버전 관리**: Git / GitHub
