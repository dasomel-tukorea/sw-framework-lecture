# Week 05 — AOP & Bean 실습 코드

## 실습 구성

| 실습 | 주제 | 핵심 개념 | 시간 |
|---|---|---|---|
| lab01 | @Aspect 실행 시간 측정 | @Around, ProceedingJoinPoint, execution Pointcut | 30분 |
| lab02 | Bean 생명주기 콜백 | @PostConstruct, @PreDestroy, 3개 빈 초기화 순서 | 15분 |
| lab03 | Singleton vs Prototype | @Scope, ApplicationContext, ScopeTestController | 15분 |
| lab04 | 커스텀 @LogExecutionTime AOP | @Target, @Retention, @annotation Pointcut | 15분 |
| lab05 | 보안 감사 AOP (AuditAspect) | 감사 로깅, 파라미터/결과 추적, 횡단 관심사 분리 | 30분 |
| lab06 | Pointcut 표현식 실험실 | execution vs within vs @annotation 비교 | 15분 |

> 🆕 = 신규, ⚡ = week04 확장

## 파일 복사 경로 (swframework 프로젝트 기준)

각 lab 파일을 `src/main/java/kr/ac/tukorea/swframework/` 아래 해당 패키지에 복사합니다.

| lab 파일 | 복사 대상 패키지 |
|---|---|
| `lab01/ExecutionTimeAspect.java` | `aspect/` |
| `lab02/DatabaseInitializer.java` | `component/` |
| `lab02/CacheInitializer.java` | `component/` |
| `lab02/HealthChecker.java` | `component/` |
| `lab03/SingletonBean.java` | `component/` |
| `lab03/PrototypeBean.java` | `component/` |
| `lab03/ScopeTestController.java` | `controller/` |
| `lab04/LogExecutionTime.java` | `annotation/` |
| `lab04/ExecutionTimeAspectV2.java` | `aspect/` |
| `lab05/AuditAspect.java` | `aspect/` |
| `lab05/StudentInfoService.java` | `service/` |
| `lab05/AuditTestController.java` | `controller/` |
| `lab06/PointcutLabAspect.java` | `aspect/` |

> `aspect/`, `component/`, `annotation/` 패키지가 없으면 직접 생성합니다.

## 사전 준비: build.gradle 의존성 추가

```groovy
dependencies {
    // AOP 의존성 추가
    implementation 'org.springframework.boot:spring-boot-starter-aop'

    // Lombok 의존성 추가 (@Slf4j 등)
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}
```

> **추가 후 반드시 Gradle 리프레시!** (IntelliJ: 🐘 코끼리 아이콘 클릭 또는 `Ctrl+Shift+O`)
>
> `configurations` 블록이 없다면 `dependencies` 위에 추가:
> ```groovy
> configurations {
>     compileOnly {
>         extendsFrom annotationProcessor
>     }
> }
> ```

## lab01 — @Aspect 실행 시간 측정 (기본, 30분)

> week04의 `GreetingService`를 그대로 활용하여 AOP로 실행 시간을 측정하는 실습입니다.
> Service 코드를 한 줄도 수정하지 않고 로깅이 추가되는 것을 체험합니다.

```
lab01/
└── ExecutionTimeAspect.java   ← @Aspect + @Around 실행 시간 측정 (aspect/)   🆕 신규
```

**테스트**: `http://localhost:8080/greeting?name=홍길동` → 콘솔에서 실행 시간 로그 확인

### 핵심 학습 포인트
1. `@Aspect` + `@Component` 둘 다 필요 (빈 등록 + AOP 선언)
2. `@Around`의 `proceed()` 호출 & `return` 필수
3. Pointcut 표현식: `execution(* kr.ac.tukorea.swframework.service..*.*(..))`
4. `ProceedingJoinPoint`에서 메서드 시그니처 추출

## lab02 — Bean 생명주기 콜백 (기본, 15분)

> 3개의 빈을 만들어 @PostConstruct / @PreDestroy 호출 순서를 관찰하는 실습입니다.

```
lab02/
├── DatabaseInitializer.java   ← @PostConstruct: DB 연결 초기화 (lifecycle/)   🆕 신규
├── CacheInitializer.java      ← @PostConstruct: 캐시 워밍업 (lifecycle/)      🆕 신규
└── HealthChecker.java         ← @PostConstruct: 서버 준비 확인 (lifecycle/)    🆕 신규
```

**테스트**: 서버 시작 → 콘솔에서 3개 빈 초기화 순서 관찰 / Ctrl+C → @PreDestroy 역순 호출 확인

### 핵심 학습 포인트
1. 생명주기: 생성 → DI → `@PostConstruct` → 비즈니스 수행 → `@PreDestroy` → 소멸
2. `@PostConstruct` 호출 순서는 Spring이 결정 (실행마다 다를 수 있음)
3. `kill -9` 강제 종료 시 `@PreDestroy` 미호출!
4. `@DependsOn`으로 초기화 순서 제어 가능

## lab03 — Singleton vs Prototype 스코프 비교 (심화, 15분)

> 브라우저에서 직접 Singleton과 Prototype의 인스턴스 동일성을 확인하는 실습입니다.

```
lab03/
├── SingletonBean.java         ← @Component 기본 Singleton (scope/)           🆕 신규
├── PrototypeBean.java         ← @Scope(SCOPE_PROTOTYPE) (scope/)            🆕 신규
└── ScopeTestController.java   ← getBean() 비교 컨트롤러 (controller/)         🆕 신규
```

**테스트**: `http://localhost:8080/scope-test`
**예상 결과**: `Singleton 동일: true | Prototype 동일: false`

### 핵심 학습 포인트
1. Singleton: 컨테이너에 단 1개, 모든 요청에서 동일 인스턴스 공유
2. Prototype: 요청마다 새 인스턴스 생성, `@PreDestroy` 미호출
3. `ApplicationContext.getBean()`으로 빈을 직접 꺼내어 `==` 비교

## lab04 — 커스텀 @LogExecutionTime AOP (심화, 15분)

> 커스텀 어노테이션을 정의하고, `@annotation` Pointcut으로 정밀하게 AOP를 적용하는 실습입니다.

```
lab04/
├── LogExecutionTime.java       ← 커스텀 어노테이션 정의 (annotation/)          🆕 신규
└── ExecutionTimeAspectV2.java  ← @annotation 기반 Aspect (aspect/)           🆕 신규
```

**사용법**: week04의 `KoreanGreetingService.greet()` 메서드에 `@LogExecutionTime` 추가

```java
import kr.ac.tukorea.swframework.annotation.LogExecutionTime; // ← import 추가

@Service
public class KoreanGreetingService implements GreetingService {
    @LogExecutionTime  // ← 이 메서드만 실행 시간 측정!
    @Override
    public String greet(String name) { return name + "님, 안녕!"; }
}
```

> ⚠️ **주의**: lab01의 `ExecutionTimeAspect`와 동시 사용 시 service 메서드마다 AOP가 2번 실행됩니다.
> lab04 실습 시 lab01의 `ExecutionTimeAspect.java`에서 `@Component`를 주석 처리하세요.

### 핵심 학습 포인트
1. `@Target(METHOD)` + `@Retention(RUNTIME)` — AOP 커스텀 어노테이션의 필수 조합
2. `execution`(패키지 전체) → `@annotation`(특정 메서드만) 전환으로 정밀 제어
3. Spring 내장 `@Transactional`, `@Cacheable`, `@Async`도 동일 원리

## lab05 — 보안 감사 AOP (심화, 30분)

> Service 코드에 감사 로그 없이 AOP만으로 '누가, 언제, 무엇을' 자동 기록하는 실습입니다.

```
lab05/
├── AuditAspect.java            ← 보안 감사 Aspect (aspect/)                   🆕 신규
├── StudentInfoService.java     ← 학생 정보 서비스 — 감사 코드 없음! (service/)   🆕 신규
└── AuditTestController.java    ← 감사 테스트 컨트롤러 (controller/)             🆕 신규
```

**테스트**:
- `http://localhost:8080/audit/student?id=42`
- `http://localhost:8080/audit/grade?id=42&subject=SW프레임워크&grade=95`

### 핵심 학습 포인트
1. Service에 감사 로그 코드가 전혀 없는데 자동으로 기록됨 — 횡단 관심사 분리 체험
2. `jp.getTarget().getClass().getSimpleName()` — 대상 클래스명 추출
3. `jp.getArgs()` — 파라미터 배열 추출
4. `LocalDateTime.now()` — 감사 시간 기록

## lab06 — Pointcut 표현식 실험실 (심화, 15분)

> 3가지 Pointcut을 직접 바꿔가며 어떤 메서드에 AOP가 적용되는지 관찰하는 실습입니다.

```
lab06/
└── PointcutLabAspect.java     ← 3가지 Pointcut 비교 (aspect/)               🆕 신규
```

**사용법**: 한 번에 하나만 주석 해제 → `/greeting`과 `/audit/student` 접속 → 로그 차이 관찰

| Pointcut | 적용 범위 | 적합한 상황 |
|---|---|---|
| `execution(* ..service..*.*(..))` | 패키지 하위 전체 | 초기 개발, 전체 Service 로깅 |
| `within(..service.StudentInfoService)` | 특정 클래스만 | 민감한 비즈니스 모니터링 |
| `@annotation(..annotation.LogExecutionTime)` | 어노테이션 붙은 메서드만 | 운영, 정밀 제어 (실무 가장 많이 사용) |

## 기술 스택

- Java 21
- Spring Boot 3.5.x
- Spring AOP (`spring-boot-starter-aop`)
- Lombok (`@Slf4j`)
- Thymeleaf (week04 greeting.html 재사용)
- Gradle
