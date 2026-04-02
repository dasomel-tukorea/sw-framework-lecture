# 5주차 핵심 용어집

강의 자료의 비유와 설명을 바탕으로 정리한 용어집입니다. 처음 접하는 개념은 비유를 통해 먼저 감을 잡고, 정확한 정의로 마무리하세요.

---

## 1. AOP (Aspect-Oriented Programming, 관점 지향 프로그래밍)

**AOP**
> "비즈니스 로직은 순수하게, 공통 기능은 AOP로"

로깅·트랜잭션·보안 등 여러 모듈에 걸쳐 반복되는 **횡단 관심사(Cross-Cutting Concerns)**를 비즈니스 로직에서 완전히 분리하는 프로그래밍 패러다임입니다.

| 구분 | AOP 없이 (기존) | AOP 적용 후 |
|---|---|---|
| 로깅 코드 | 100개 메서드에 각각 작성 | @Aspect 1개로 전체 적용 |
| Service 코드 | 비즈니스 + 로깅 혼재 | 순수 비즈니스 로직만 |
| 로깅 변경 시 | 100곳 전부 수정 | Aspect 한 곳만 수정 |
| 새 Service 추가 시 | 로깅 코드 직접 추가 필요 | 자동 적용 |

> 비유: "수십 명 SI 프로젝트 — 공통 기능은 AOP 전담 팀, 각 개발자는 비즈니스 로직에만 집중"

---

## 2. AOP 핵심 용어

**Aspect (관점)**
횡단 관심사를 모듈화한 클래스입니다. Advice(기능) + Pointcut(대상)을 결합합니다.

```java
@Aspect       // AOP 선언
@Component    // Spring Bean 등록 (둘 다 필요!)
public class ExecutionTimeAspect { ... }
```

> 비유: Aspect = '출석체크', Advice = '이름부르기', Pointcut = '1교시 전공만'

---

**Advice (어드바이스)**
실제 수행할 기능 + 실행 시점을 정의합니다.

| 어노테이션 | 실행 시점 | 사용 예시 |
|---|---|---|
| `@Before` | 메서드 실행 전 | 사전 검증, 로깅 |
| `@AfterReturning` | 정상 종료 후 | 반환값 후처리 |
| `@AfterThrowing` | 예외 발생 시 | 예외 로깅, 알림 |
| `@After` | 항상 동작 (정상/예외 무관) | 리소스 정리 |
| **`@Around`** ★ | **전후 모두 감싸는 가장 강력한 방식** | **실행 시간 측정, 트랜잭션 관리** |

---

**Pointcut (포인트컷)**
Advice 적용 대상을 선별하는 조건입니다. '어떤 메서드에 적용할 것인가'를 정의합니다.

| 표현식 | 대상 | 적합한 상황 |
|---|---|---|
| `execution(* ..service..*.*(..))` | 패키지 하위 전체 | 초기 개발, 전체 Service 로깅 |
| `within(..StudentInfoService)` | 특정 클래스만 | 민감한 비즈니스 모니터링 |
| `@annotation(..LogExecutionTime)` | 어노테이션 붙은 메서드만 | 운영, 정밀 제어 (실무 가장 많이 사용) |

> 선택 전략: 초기 → execution / 운영 → @annotation / 보안감사 → within

---

**JoinPoint**
Advice가 적용될 수 있는 지점 (메서드 실행 시점)입니다. Spring AOP에서는 메서드 실행만 지원합니다.

```java
// JoinPoint에서 추출 가능한 정보
jp.getSignature().toShortString()  // 메서드 시그니처
jp.getTarget().getClass()          // 대상 클래스
jp.getArgs()                       // 파라미터 배열
```

---

**ProceedingJoinPoint**
`@Around`에서만 사용하는 특수한 JoinPoint입니다. `proceed()`로 핵심 로직 실행을 제어합니다.

```java
@Around("execution(* ..service..*.*(..))")
public Object measure(ProceedingJoinPoint jp) throws Throwable {
    // 전처리 (시작 시간 기록)
    Object result = jp.proceed();  // ← 핵심 로직 실행!
    // 후처리 (종료 시간 계산)
    return result;  // ← 반환 필수!
}
```

> 주의: `proceed()` 누락 → 핵심 로직 미실행 / `return` 누락 → null 반환

---

**Weaving (위빙)**
Aspect를 대상 코드에 적용하는 과정입니다. Spring AOP는 런타임에 프록시를 생성하여 위빙합니다.

---

## 3. Proxy 패턴

**Proxy (프록시)**
Spring AOP의 핵심 동작 방식입니다. 클라이언트 → Proxy → Target 순서로 호출됩니다.

| 방식 | 조건 | 설명 |
|---|---|---|
| JDK Dynamic Proxy | 인터페이스 존재 시 | 인터페이스 기반 프록시 |
| CGLIB Proxy | 클래스만 존재 시 | 클래스 상속 기반 프록시 |

> 비유: CEO(핵심 로직)에게 직접 가지 않고 비서(Proxy)를 거침
> - 비서가 하는 일: 방문자 기록(로깅), 면담 시간 측정(성능), 보안 확인(인증/인가)
> - CEO는 순수하게 업무만 수행!

개발자가 직접 Proxy를 작성할 필요 없음 — Spring이 자동 생성

---

## 4. 커스텀 어노테이션

**@Target**
어노테이션을 적용할 수 있는 위치를 제한합니다.

```java
@Target(ElementType.METHOD)    // 메서드에만 적용 가능
@Target(ElementType.TYPE)      // 클래스/인터페이스에만 적용 가능
```

---

**@Retention**
어노테이션 정보가 유지되는 범위를 지정합니다.

```java
@Retention(RetentionPolicy.RUNTIME)   // 런타임까지 유지 (AOP 필수!)
@Retention(RetentionPolicy.SOURCE)    // 컴파일 시 제거 (Lombok 등)
@Retention(RetentionPolicy.CLASS)     // .class 파일까지만 유지
```

> AOP에서 사용하려면 반드시 `RUNTIME`이어야 함!

---

**커스텀 어노테이션 정의 예시**

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
}
```

Spring 내장도 동일 원리:
- `@Transactional`: `@Target(TYPE, METHOD)` + `@Retention(RUNTIME)` → AOP 프록시 트랜잭션
- `@Cacheable`: 메서드 결과 캐시 저장
- `@Async`: 메서드 비동기 실행

---

## 5. Bean 스코프

**Singleton (기본값)**
> "컨테이너에 단 1개, 모든 요청이 공유"

Spring Bean의 기본 스코프입니다. 별도 설정 없이 사용하면 자동 적용됩니다.

| 특성 | 설명 |
|---|---|
| 인스턴스 수 | 컨테이너에 단 1개 |
| 생명주기 | 컨테이너가 전체 관리 |
| `@PreDestroy` | 자동 호출됨 |
| 공유 | 모든 요청에서 동일 인스턴스 |
| 적합 | Stateless: Service, Repository |
| 주의 | 멀티스레드 동시성 |

---

**Prototype**
> "요청마다 새로 생성, 소멸은 미관리"

| 특성 | 설명 |
|---|---|
| 인스턴스 수 | 요청마다 새로 생성 |
| 생명주기 | 생성·주입까지만 관리 |
| `@PreDestroy` | **호출 안 됨!** |
| 공유 | 각 요청 독립적 |
| 적합 | Stateful: 주문서, 장바구니 |
| 설정 | `@Scope("prototype")` 명시 |
| 주의 | 메모리 사용량 증가 |

```java
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrototypeBean { }
```

---

## 6. Bean 생명주기 (Lifecycle)

**생명주기 흐름**

```
① 빈 생성 → ② DI 주입 → ③ @PostConstruct → ④ 비즈니스 수행 → ⑤ @PreDestroy → ⑥ 소멸
   (생성자 호출)  (의존관계 설정)  (초기화 콜백)     (요청 처리)      (소멸 전 정리)
```

---

**@PostConstruct**
DI 완료 후 자동 실행되는 초기화 콜백입니다.

사용 예시:
- DB 연결 확인
- 캐시 워밍업
- 초기 데이터 로딩

```java
@PostConstruct
public void init() {
    log.info("DB 연결 초기화 완료");
}
```

---

**@PreDestroy**
컨테이너 종료 직전 자동 실행되는 소멸 콜백입니다.

사용 예시:
- 연결 해제
- 파일 정리
- 캐시 저장

```java
@PreDestroy
public void destroy() {
    log.info("DB 연결 해제 완료");
}
```

> 주의: Prototype 스코프에서는 `@PreDestroy` 호출 안 됨!
> 주의: `kill -9` 강제 종료 시에도 미호출 — 정상 종료(Ctrl+C) 필요

---

**순환 참조 (Circular Dependency)**
두 빈이 서로 의존하면 생성 순서를 결정할 수 없어 에러가 발생합니다. 생성자 주입이 가장 안전하게 감지합니다.

| 해결 방법 | 설명 | 권장 |
|---|---|---|
| 책임 분리 | 중간 계층(EventPublisher) 추가, 의존 관계 단방향화 | **권장** |
| 인터페이스 분리 | 공통 인터페이스 추출, 한쪽만 의존하도록 재설계 | 권장 |
| `@Lazy` | 지연 주입으로 임시 우회 | **비권장** (근본 해결 아님) |

---

## 7. 비교 정리

| 구분 | @Before | @Around |
|---|---|---|
| 반환 제어 | 불가 | 가능 (`return result`) |
| 예외 제어 | 불가 | 가능 (`try-catch`) |
| 실행 제어 | 불가 | 가능 (`proceed()`) |
| 사용 복잡도 | 낮음 | 높음 |
| 대표 용도 | 사전 로깅, 검증 | 실행 시간 측정, 트랜잭션 |

| Pointcut 종류 | 대상 | 적합 상황 |
|---|---|---|
| `execution` | 패키지 전체 | 초기 개발 |
| `within` | 특정 클래스 | 보안 감사 |
| `@annotation` | 어노테이션 붙은 메서드 | 운영 (실무 최다 사용) |

| 스코프 | 인스턴스 | @PreDestroy | 적합 대상 |
|---|---|---|---|
| Singleton | 1개 (공유) | 호출됨 | Service, Repository |
| Prototype | 매번 생성 | **미호출** | 주문서, 장바구니 |

---

## 8. 자주 발생하는 문제 & 해결

| 문제 | 원인 & 해결 |
|---|---|
| AOP 로그 미출력 | `spring-boot-starter-aop` 의존성 확인 → Gradle 리프레시 |
| @Aspect 동작 안 함 | `@Aspect` + `@Component` 둘 다 필요 |
| 빈 화면/null 반환 | `@Around`에서 `proceed()` 누락 → `return` 필수 |
| Pointcut 매칭 안 됨 | `execution(* ...)` 반환 타입 `*` 누락, 패키지 경로 확인 |
| @PreDestroy 미호출 | Ctrl+C 정상 종료 / Prototype은 소멸 콜백 불가 |

---

## 9. Spring Annotation 종합 정리 (5주차 추가)

| 어노테이션 | 설명 | 역할 |
|---|---|---|
| `@Aspect` | AOP 관점 클래스 선언 | AOP |
| `@Around` | 메서드 전후 감싸는 Advice (가장 강력) | AOP |
| `@Before` | 메서드 실행 전 Advice | AOP |
| `@AfterReturning` | 정상 종료 후 Advice | AOP |
| `@AfterThrowing` | 예외 발생 시 Advice | AOP |
| `@After` | 항상 실행되는 Advice | AOP |
| `@PostConstruct` | DI 완료 후 초기화 콜백 | 생명주기 |
| `@PreDestroy` | 소멸 전 정리 콜백 | 생명주기 |
| `@Scope` | 빈 스코프 지정 (singleton/prototype) | 스코프 |
| `@DependsOn` | 빈 초기화 순서 제어 | 스코프 |
| `@Target` | 어노테이션 적용 위치 제한 | 메타 |
| `@Retention` | 어노테이션 유지 범위 지정 | 메타 |
