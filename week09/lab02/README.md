# Lab 02 — Spring Data JDBC → MyBatis 마이그레이션 (실전)

> "W06~W07까지 만든 `ListCrudRepository` 코드를 W09 MyBatis 구조로 옮긴다 — 실제 swframework 프로젝트에서 진행하는 작업"

---

## 왜 이 lab이 필요한가

| Week | 학생이 만든 것 | 데이터 접근 |
|---|---|---|
| W03 | Map 기반 임시 저장소 | `Map<Long, Student>` |
| W06 | Spring Data JDBC + H2 | `ListCrudRepository<Student, Long>` |
| **W09** | **MyBatis + MySQL** | **`@Mapper interface StudentMapper`** ← **오늘** |

> Lab 01은 "처음부터 짤 때"의 흐름. **Lab 02는 "기존 코드를 옮길 때"의 흐름**.
> 실제로 학생들이 매주 부딪히는 작업은 **마이그레이션**이다.

---

## Spring Data JDBC vs MyBatis — 개념 차이

> "둘 다 JDBC 추상화"이지만 **SQL을 누가 쓰는가**가 정반대.

### 사고방식

```
Spring Data JDBC                            MyBatis
────────────────                            ───────
"객체가 곧 테이블"                          "이 SQL을 Java에서 호출"
도메인 우선 → SQL은 부산물                  SQL 우선 → 객체는 결과 매핑
```

### 비교표

| 관점 | Spring Data JDBC | MyBatis |
|---|---|---|
| SQL 작성 | 프레임워크 자동 생성 | XML에 직접 작성 |
| 매핑 | `@Table` / `@Id` / `@Column` 어노테이션 | XML namespace + 메서드 id |
| `save(s)` 동작 | id 유무로 INSERT/UPDATE 자동 분기 | `<insert>` / `<update>` 따로 호출 |
| 동적 SQL | 한계 — 복잡한 건 `@Query` / Spec / native | 강점 — `<if>` `<choose>` `<foreach>` 표준 |
| 운영 SQL 튜닝 | Java 재컴파일·재배포 필요 | XML만 수정 → 핫픽스 가능 |
| 학습 곡선 | 낮음 (인터페이스만) | 중간 (XML + 동적 SQL 5종) |
| 국내 실무 비중 | 신규 / 스타트업 / DDD 프로젝트 | 대형 SI / 공공 / 금융 / 전자정부 |

### 코드로 보는 차이 (같은 `findAll`)

```java
// Spring Data JDBC — SQL이 보이지 않는다
public interface StudentRepository extends ListCrudRepository<Student, Long> {}
// 사용: studentRepository.findAll();
```

```java
// MyBatis — Java는 시그니처만, SQL은 XML로 분리
@Mapper
public interface StudentMapper {
    List<Student> findAll();
}
```
```xml
<!-- resources/mapper/StudentMapper.xml -->
<select id="findAll" resultType="Student">
    SELECT id, name, email, major, created_at
    FROM student
    ORDER BY id DESC
</select>
```

### 무엇이 안 변하는가 — DI의 보너스

```java
// Service / Controller 코드는 거의 그대로
@Service
@Transactional
public class StudentService {
    private final StudentMapper mapper;     // ← 필드 타입만 교체
    // findAll(), save(), delete() 시그니처 동일 → Controller 영향 없음
}
```

> W04 IoC/DI + W05 AOP 덕분에 **상위 계층(Controller)은 ORM 교체에 영향받지 않는다**.

### 왜 W09에서 굳이 바꾸는가 — 3가지 실무 이유

| # | 이유 | 실무 시나리오 |
|---|---|---|
| 1 | **국내 SI 표준** | 공공기관·금융·전자정부 표준프레임워크 모두 MyBatis 채택 → 취업 후 첫 날 코드 |
| 2 | **동적 SQL 자유도** | 검색 화면 5조건 분기, 통계 리포트는 Spring Data JDBC에선 결국 `@Query` 우회 |
| 3 | **운영 핫픽스** | DBA가 보내준 튜닝 SQL → MyBatis는 XML 한 줄 교체 / Data JDBC는 재배포 |

> **결론**: 둘 중 우월한 게 있는 게 아니라 **도메인 중심 vs SQL 중심**의 선택.
> 본 강의는 둘 다 거치며 트레이드오프를 체감하는 것이 목표.

---

---

## 마이그레이션 매핑 표 (1:1)

| 항목 | Before (W06 / Spring Data JDBC) | After (W09 / MyBatis) |
|---|---|---|
| Build | `spring-boot-starter-data-jdbc` | `mybatis-spring-boot-starter:3.0.3` |
| Domain 어노테이션 | `@Table("student")` `@Id` | (없음) — 순수 POJO |
| 데이터 접근 | `interface StudentRepository extends ListCrudRepository<Student, Long>` | `@Mapper interface StudentMapper` |
| 조회 메서드 | `findAll()` (자동 제공) | `List<Student> findAll();` (직접 정의) |
| SQL 위치 | (자동 생성) | `resources/mapper/StudentMapper.xml` |
| 컬럼 매핑 | `studentId` ↔ `student_id` (자동) | `application.yml`의 `map-underscore-to-camel-case: true` |
| 트랜잭션 | `@Transactional` (Spring 공통) | `@Transactional` (그대로) |
| 의존성 주입 | `private final StudentRepository repo;` | `private final StudentMapper mapper;` |

> **트랜잭션 어노테이션은 그대로** — W05 AOP 프록시는 ORM과 무관.

---

## 단계별 마이그레이션 (40분)

### 1단계 — build.gradle 의존성 교체 (3분)

```diff
 dependencies {
     implementation 'org.springframework.boot:spring-boot-starter-web'
     implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
-    implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
+    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3'
     runtimeOnly    'com.h2database:h2'
     runtimeOnly    'com.mysql:mysql-connector-j'
 }
```

> **주의**: `spring-data-jdbc`를 제거하면 `@Table`/`@Id` import가 깨진다 — 다음 단계에서 정리.

### 2단계 — application.yml 설정 추가 (2분)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/swframework?serverTimezone=Asia/Seoul
    username: root
    password: ${DB_PASSWORD:1234}      # 환경변수 우선, 없으면 1234
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: kr.ac.tukorea.swframework.domain
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 3단계 — Student.java 정리 (3분)

```diff
 package kr.ac.tukorea.swframework.domain;

-import org.springframework.data.annotation.Id;
-import org.springframework.data.relational.core.mapping.Table;
-
-@Table("student")
 public class Student {

-    @Id
     private Long id;
     private String name;
     private String studentId;        // → DB student_id (camelCase 옵션)
     private String email;
+    private String major;            // 새 컬럼 (W09 학생 테이블)
+    private LocalDateTime createdAt; // 새 컬럼

     public Student() {}              // ← MyBatis 리플렉션용 (필수)
     // setter도 모두 public 유지
 }
```

> **체크**: `@Table`, `@Id` import 제거 후 컴파일 통과되는지 확인.

### 4단계 — Repository → Mapper 교체 (5분)

#### Before
```java
// repository/StudentRepository.java  ← 삭제
public interface StudentRepository extends ListCrudRepository<Student, Long> {}
```

#### After
```java
// mapper/StudentMapper.java  ← 새로 생성
@Mapper
public interface StudentMapper {
    List<Student> findAll();
    Student      findById(Long id);
    void         insert(Student student);
    void         update(Student student);
    void         delete(Long id);
}
```

> Lab 01의 `StudentMapper.java`를 그대로 복사 → 패키지 경로만 맞추면 끝.

### 5단계 — Mapper XML 생성 (5분)

`src/main/resources/mapper/StudentMapper.xml` — Lab 01의 XML을 그대로 사용.

```
src/main/resources/mapper/
└── StudentMapper.xml    ← Lab 01에서 복사
```

### 6단계 — Service 계층 수정 (5분)

```diff
 @Service
 @Transactional
 public class StudentService {

-    private final StudentRepository repo;
+    private final StudentMapper mapper;

-    public StudentService(StudentRepository repo) { this.repo = repo; }
+    public StudentService(StudentMapper mapper) { this.mapper = mapper; }

     @Transactional(readOnly = true)
     public List<Student> findAll() {
-        return repo.findAll();
+        return mapper.findAll();
     }

     public void save(Student s) {
-        repo.save(s);
+        if (s.getId() == null) mapper.insert(s);
+        else                   mapper.update(s);
     }

     public void delete(Long id) {
-        repo.deleteById(id);
+        mapper.delete(id);
     }
 }
```

> **차이의 본질**: `repo.save(s)`는 **id 유무로 INSERT/UPDATE를 자동 분기**하지만, MyBatis는 **호출자가 명시**해야 한다. 이걸 Service에 책임으로 두는 것이 일반적.

### 7단계 — Controller 검증 (3분)

`StudentController`는 **변경 없음** — Service 인터페이스만 같으면 그대로 동작한다.

> 이게 W04 DI + W05 AOP의 보너스: **상위 계층은 ORM 교체에 영향받지 않는다**.

### 8단계 — Spring Data JDBC 잔재 제거 (2분)

검색해서 정리할 import / 어노테이션:

```bash
# 프로젝트 루트에서 검색 — 잔재가 있으면 표시됨
grep -rn "ListCrudRepository\|@Table\|@Id" src/main/java
grep -rn "spring-data-jdbc" build.gradle
```

남은 `@Table`/`@Id`/`ListCrudRepository`는 모두 제거.

### 9단계 — 빌드 + 실행 (2분)

```bash
./gradlew clean build
./gradlew bootRun
```

`http://localhost:8080/students` — 목록 정상 동작 확인.

### 10단계 — H2 프로필도 동작 확인 (5분)

```bash
./gradlew bootRun --args='--spring.profiles.active=h2'
open http://localhost:8080/h2-console
```

> Lab 04 적용 완료 상태이면 양쪽 환경 모두 같은 코드로 동작.

---

## 주차 연결 라벨

```
W04 IoC/DI                ← Service-Mapper 생성자 주입은 그대로 유지
W05 @Transactional AOP    ← 어노테이션 그대로, 내부 구현만 교체
W06 Spring Data JDBC      ← Before
W07 SQL Injection 이해     ← #{} = PreparedStatement (자동)
W09 MyBatis  ← 오늘
```

---

## 체크리스트

### 기능 동작
- [ ] `/students` 목록 — W06과 동일하게 표시됨
- [ ] `/students/new` 등록 — PRG 패턴 유지
- [ ] `/students/{id}/edit` 수정 — 기존 값 채워진 폼
- [ ] `/students/{id}/delete` 삭제 — 목록에서 제거
- [ ] 콘솔에 SQL 로그 출력 (`StdOutImpl`)

### 코드 정리
- [ ] `StudentRepository.java` 삭제됨
- [ ] `Student.java`에서 `@Table`/`@Id` 제거됨
- [ ] `build.gradle`에서 `spring-data-jdbc` 제거됨
- [ ] 어디에도 `ListCrudRepository` 참조 없음

### 안전
- [ ] `application.yml`에 비밀번호 평문 노출 없음 (환경변수 또는 `application-local.yml`)
- [ ] `.gitignore`에 `application-local.yml` 추가됨

---

## 흔한 실수

| 증상 | 원인 | 해결 |
|---|---|---|
| `Invalid bound statement` | XML namespace ≠ 인터페이스 FQCN | `kr.ac.tukorea.swframework.mapper.StudentMapper` 정확히 |
| `studentId` 필드만 null | `map-underscore-to-camel-case` 누락 | application.yml 설정 추가 |
| `ListCrudRepository cannot be resolved` | data-jdbc 의존성 제거 후 import 잔재 | `import` 줄 삭제 |
| `Could not autowire StudentRepository` | Service에서 옛 의존성 그대로 사용 | StudentMapper로 교체 |
| 등록 후 폼이 빈 상태로 redirect | `mapper.insert(s)` 실수로 `update` 호출 | id null 분기 확인 |

---

## 마이그레이션 후 다음 단계

1. **Lab 03 (Dynamic SQL)** 적용 — 검색 기능 추가
2. **Lab 04 (H2 프로필)** 적용 — 환경 분리
3. **Lab 06 (팀 ERD)** — 본인 팀 프로젝트의 1번 엔티티에 동일 절차 적용
4. **다음 주 W10**: 전역 예외 처리 (`@ControllerAdvice`) + `@Transactional` 속성 심화
