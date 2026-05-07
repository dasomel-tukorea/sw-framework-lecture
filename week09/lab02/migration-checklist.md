# Lab 02 — 마이그레이션 체크리스트 (인쇄용)

> A4 1장 분량. 작업 전 출력해 옆에 두고 한 줄씩 체크.

## Phase 1 — 의존성 (5분)

- [ ] `build.gradle`: `spring-boot-starter-data-jdbc` **제거**
- [ ] `build.gradle`: `mybatis-spring-boot-starter:3.0.3` **추가**
- [ ] Gradle Sync 성공
- [ ] `./gradlew build` 컴파일 에러 확인 (이 단계에서는 에러 정상)

## Phase 2 — 도메인 (5분)

- [ ] `Student.java`에서 `@Table`/`@Id` import 제거
- [ ] `Student.java`에서 어노테이션 자체 제거
- [ ] 기본 생성자 `public Student() {}` 존재 확인 (MyBatis 필수)
- [ ] 모든 필드 setter 존재 확인

## Phase 3 — 데이터 접근 (10분)

- [ ] `repository/StudentRepository.java` **삭제**
- [ ] `mapper/` 디렉터리 생성
- [ ] `mapper/StudentMapper.java` 생성 (`@Mapper` 어노테이션 포함)
- [ ] `resources/mapper/` 디렉터리 생성
- [ ] `resources/mapper/StudentMapper.xml` 작성
- [ ] XML namespace = `kr.ac.tukorea.swframework.mapper.StudentMapper` (FQCN 일치)
- [ ] 모든 SQL이 `#{}` 사용 (`${}` 아님)

## Phase 4 — 설정 (5분)

- [ ] `application.yml`에 `mybatis:` 블록 추가
- [ ] `mapper-locations: classpath:mapper/*.xml`
- [ ] `type-aliases-package: kr.ac.tukorea.swframework.domain`
- [ ] `map-underscore-to-camel-case: true`
- [ ] `log-impl: org.apache.ibatis.logging.stdout.StdOutImpl`
- [ ] DB 비밀번호: 환경변수 또는 `application-local.yml` 분리

## Phase 5 — 비즈니스 계층 (5분)

- [ ] `StudentService` 필드: `StudentRepository` → `StudentMapper`
- [ ] `findAll/findById`에 `@Transactional(readOnly = true)`
- [ ] `save()` 메서드에서 `id == null ? insert : update` 분기 처리
- [ ] `delete()`는 `mapper.delete(id)` 호출
- [ ] `StudentController`는 **수정 없음** (Service 인터페이스 호환)

## Phase 6 — 정리 (3분)

- [ ] `grep -rn "ListCrudRepository" src/main/java` → 0건
- [ ] `grep -rn "@Table" src/main/java` → 0건 (다른 모듈 영향 없는지 확인)
- [ ] `grep -rn "@Id" src/main/java` → 0건 (Spring Data JDBC의 @Id 한정)
- [ ] 사용하지 않는 import 제거 (IntelliJ: ⌥⌘O)

## Phase 7 — 검증 (5분)

- [ ] `./gradlew clean build` 성공
- [ ] `./gradlew bootRun` 부팅 로그에 `Mapped statement [...]` 표시
- [ ] `/students` 목록 페이지 200 OK
- [ ] 등록 → 목록에 새 학생 표시 (PRG)
- [ ] 수정 → 변경 내용 반영
- [ ] 삭제 → 목록에서 제거
- [ ] 콘솔에 `==>  Preparing: SELECT ...` SQL 로그 출력
- [ ] `--args='--spring.profiles.active=h2'`로도 동일하게 동작

## 보너스 (Lab 03 연계)

- [ ] `findByName` / `findBySearchType` / `updateSelective` / `findByIds` 추가
- [ ] `/students/search?type=name&keyword=홍` 동작
- [ ] SQL 로그에서 동적 조립된 `WHERE` / `SET` 절 확인

## 보안 게이트 (Push 전 필수)

- [ ] `git diff`에 비밀번호 평문 없음
- [ ] `git status`에 `application-local.yml` 없음 (gitignore 처리)
- [ ] commit message에 환경변수 키 노출 없음
- [ ] PR description에 DB URL / 비밀번호 노출 없음

> **한 줄이라도 빠지면 다음 주 W10에서 더 비싸진다 — 1:10:100.**
