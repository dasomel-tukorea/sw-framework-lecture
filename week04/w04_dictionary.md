# 4주차 핵심 용어집

강의 자료의 비유와 설명을 바탕으로 정리한 용어집입니다. 처음 접하는 개념은 비유를 통해 먼저 감을 잡고, 정확한 정의로 마무리하세요.

---

## 1. IoC (Inversion of Control, 제어의 역전)

**IoC**
> "코드에서 new를 지우고 Spring에게 맡겨라"

객체 생성과 생명주기 제어권을 개발자에서 Spring 컨테이너로 이전하는 설계 원칙입니다. 개발자는 비즈니스 로직에만 집중하고, 객체의 생성·초기화·소멸은 프레임워크가 담당합니다.

| 구분 | 기존 (JSP/Servlet) | IoC (Spring) |
|---|---|---|
| 객체 생성 | `new StudentService()` 직접 호출 | 컨테이너가 `@Service` 스캔 후 자동 생성 |
| 객체 관리 | 개발자가 `init()`/`destroy()` 수동 관리 | 컨테이너가 생명주기 자동 관리 |
| 의존 관계 | 코드 안에서 직접 설정 (하드코딩) | 어노테이션/설정으로 선언적 관리 |
| 변경 시 | 코드 수정 → 재컴파일 → 재배포 | 설정만 변경, 코드 수정 없이 교체 |

> 비유: "직접 요리하기" vs "레스토랑에서 주문하기" — 결과는 같지만 내가 할 일이 다르다

---

**Tight Coupling (강한 결합)**
> "결제 모듈 100군데 수정" — 그 공포에서 시작됩니다

`new` 키워드로 객체를 직접 생성하면 '객체 생성 책임'과 '객체 사용 책임'이 하나의 클래스에 묶입니다. 구현체 변경 시 해당 클래스를 사용하는 모든 코드를 찾아 수정해야 합니다.

```java
// Tight Coupling 예시 — 카카오페이 → 네이버페이 교체 시?
private PaymentService pay = new KakaoPay(); // 100개 파일에서 전부 수정!
```

해결책: 객체 생성 책임을 외부(Spring 컨테이너)로 분리 → 이것이 IoC/DI의 핵심!

---

**IoC 컨테이너 (ApplicationContext)**
Spring에서 모든 Bean의 생성·초기화·의존성 주입·소멸을 관리하는 컨테이너입니다. `@SpringBootApplication`이 시작점이며, 컴포넌트 스캔 범위를 자동으로 설정합니다.

IoC 컨테이너의 동작 순서:
1. **컴포넌트 스캔**: `@Component`, `@Service` 등이 붙은 클래스 발견
2. **빈 등록**: 싱글톤 인스턴스 생성 → Singleton Pool에 저장
3. **의존성 주입**: 생성자 주입 (final) 방식으로 자동 연결

---

## 2. Spring Bean

**Spring Bean**
Spring 컨테이너(ApplicationContext)가 관리하는 객체를 '빈(Bean)'이라 합니다. 빈은 기본적으로 싱글톤(Singleton)으로 생성되어, 같은 타입의 빈은 하나의 인스턴스를 공유합니다.

---

**싱글톤 (Singleton)**
> "여러 곳에서 주입받아도 동일 인스턴스"

Spring Bean의 기본 스코프입니다. 애플리케이션 전체에서 한 타입당 인스턴스 1개만 생성하여 공유합니다.
- 메모리 효율성 + 성능 향상
- 상태 없는(Stateless) 설계 권장
- 필요시 Prototype 스코프 설정 가능

---

## 3. DI (Dependency Injection, 의존성 주입)

**DI**
> "Controller는 '무엇을 사용하는지'만 선언하고, '어떤 구현체를 쓸지'는 Spring이 결정"

IoC를 실현하는 구체적인 방법입니다. 객체가 필요로 하는 의존 객체를 외부에서 주입해주어 결합도를 낮추고 유연성을 높입니다.

---

**생성자 주입 (권장)**

```java
@Controller
public class StudentController {
    private final StudentService studentService; // final + 인터페이스 타입

    public StudentController(StudentService studentService) { // 생성자 1개 → @Autowired 생략
        this.studentService = studentService;
    }
}
```

| 특징 | 설명 |
|---|---|
| 불변성 | `final` 키워드 → 생성 후 변경 불가 |
| 컴파일 체크 | 의존성 누락 시 컴파일 에러 |
| 테스트 용이 | 순수 Java로 단위 테스트 가능 |
| @Autowired 생략 | 생성자 1개이면 Spring Boot 3.x에서 자동 적용 |

> Lombok 사용 시: `@RequiredArgsConstructor` 하나면 생성자 전체가 자동 생성됩니다!

---

**Setter 주입 (선택적)**

```java
@Autowired
public void setStudentService(StudentService studentService) {
    this.studentService = studentService;
}
```

- 가변 (런타임에 변경 가능)
- 런타임 `NullPointerException` 위험
- **선택적 의존성에만** 사용 권장, 일반적으로 지양

---

**필드 주입 (지양)**

```java
@Autowired
private StudentService studentService; // final 사용 불가!
```

- 불변성 보장 불가, Spring 컨테이너 없이 테스트 불가
- 의존성이 외부에 드러나지 않음
- **사용하지 마세요** (테스트 어려움)

---

## 4. 빈 등록 방법

**@Component (자동 등록)**
> "내가 직접 만든 클래스를 자동으로 빈 등록"

클래스 레벨에 선언하면 컴포넌트 스캔으로 자동 등록됩니다. `@Service`, `@Repository`, `@Controller`는 `@Component`의 특수화(Stereotype) 어노테이션입니다.

```java
@Service  // @Component의 특수화
public class StudentService {
    // 컴포넌트 스캔 대상 → 자동 등록
}
```

---

**@Bean (수동 등록)**
> "남이 만든 것(외부 라이브러리)을 빈으로 등록"

`@Configuration` 클래스 내 메서드에 선언하여 반환 객체를 수동으로 빈 등록합니다. 외부 라이브러리처럼 직접 어노테이션을 붙일 수 없는 클래스에 사용합니다.

```java
@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper(); // 초기화 로직 제어 가능
    }
}
```

| 구분 | @Component (자동) | @Bean (수동) |
|---|---|---|
| 선언 위치 | 클래스 레벨 | @Configuration 메서드 |
| 대상 | 내가 만든 클래스 | 외부 라이브러리 객체 |
| 등록 방식 | 컴포넌트 스캔 자동 | 메서드 반환값 수동 등록 |
| 초기화 제어 | 제한적 | 세밀한 제어 가능 |

---

## 5. 다중 구현체 처리

**@Primary**
> "기본값 지정 — 구현체가 여러 개일 때 이 빈을 우선 사용"

동일 인터페이스의 구현체가 2개 이상일 때 기본으로 주입될 빈을 지정합니다. Controller 코드를 변경하지 않고 `@Primary` 어노테이션만 옮기면 구현체가 교체됩니다.

```java
@Service
@Primary  // ← 이 어노테이션 하나로 기본 주입 대상이 변경된다
public class EnglishGreetingService implements GreetingService { ... }
```

---

**@Qualifier**
> "이름표 지정 — @Primary보다 우선하여 특정 구현체를 선택"

빈 이름을 직접 지정하여 특정 구현체를 주입받습니다. `@Primary`보다 우선순위가 높습니다.

```java
public GreetingController(@Qualifier("koreanGreetingService") GreetingService svc) {
    this.greetingService = svc;
}
```

빈 이름 규칙: 클래스명의 첫 글자를 소문자로 변환
- `KoreanGreetingService` → `"koreanGreetingService"`
- `EnglishGreetingService` → `"englishGreetingService"`

| 우선순위 | 어노테이션 | 설명 |
|---|---|---|
| 1순위 | `@Qualifier` | 이름으로 정확히 지정 |
| 2순위 | `@Primary` | 기본 빈으로 지정 |
| 3순위 | 타입 매칭 | 구현체가 1개면 자동 |

---

## 6. @Profile

**@Profile**
> "환경(dev/prod)에 따라 다른 구현체를 자동으로 주입"

활성화된 프로파일에 따라 특정 Bean만 등록되도록 제어하는 어노테이션입니다. Controller 코드 변경 없이 프로파일 전환만으로 동작이 변경됩니다.

```java
@Service
@Profile("dev")   // "dev" 프로파일일 때만 빈 등록
public class DevNotificationService implements NotificationService { ... }

@Service
@Profile("prod")  // "prod" 프로파일일 때만 빈 등록
public class ProdNotificationService implements NotificationService { ... }
```

실행 방법:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'   # 개발 환경
./gradlew bootRun --args='--spring.profiles.active=prod'  # 운영 환경
```

---

**Profile별 설정 파일**

| 파일 | 용도 |
|---|---|
| `application.yml` | 공통 설정 (모든 프로파일에 적용) |
| `application-h2.yml` | H2 인메모리 DB (개발/테스트) |
| `application-mysql.yml` | MySQL DB (운영/통합 테스트) |

`spring.profiles.active` 값에 따라 해당 설정 파일이 자동으로 로드됩니다.

---

## 7. Lombok

**Lombok**
Java의 반복적인 boilerplate 코드(Getter, Setter, 생성자, toString 등)를 어노테이션 하나로 자동 생성해주는 라이브러리입니다.

| 어노테이션 | 생성되는 코드 |
|---|---|
| `@Getter` | 모든 필드의 Getter 메서드 |
| `@Setter` | 모든 필드의 Setter 메서드 |
| `@RequiredArgsConstructor` | `final` 필드만 포함하는 생성자 |
| `@AllArgsConstructor` | 모든 필드를 포함하는 생성자 |
| `@NoArgsConstructor` | 기본 생성자 (파라미터 없음) |
| `@Slf4j` | `log` 변수 자동 생성 (로깅용) |

> `@RequiredArgsConstructor` + `final` 필드 = 생성자 주입 자동 완성!

---

## 8. Spring Annotation 종합 정리

| 어노테이션 | 설명 | 역할 |
|---|---|---|
| `@SpringBootApplication` | 앱 시작점, 컴포넌트 스캔 + 자동 설정 | 진입점 |
| `@Component` | 범용 빈 자동 등록 (`@Service` 등의 상위) | 공통 |
| `@Service` | 비즈니스 로직 빈 등록 | 서비스 |
| `@Repository` | 데이터 접근 빈 + 예외 변환 | 리포지토리 |
| `@Controller` | MVC 컨트롤러, 뷰 이름 반환 (HTML) | 컨트롤러 |
| `@RestController` | `@Controller` + `@ResponseBody` (JSON 반환) | REST API |
| `@Configuration` | Spring 설정 클래스 선언 | 설정 |
| `@Bean` | `@Configuration` 내 메서드 → 수동 빈 등록 | 설정 |
| `@Autowired` | 빈 자동 주입 (생성자 1개면 생략 가능) | 주입 |
| `@Primary` | 동일 타입 빈 중 기본 주입 대상 지정 | 주입 |
| `@Qualifier` | 빈 이름으로 특정 구현체 지정 (`@Primary`보다 우선) | 주입 |
| `@Profile` | 특정 프로파일에서만 빈 등록 | 환경 |

---

## 9. 비교 정리

| 구분 | JSP/Servlet (과거) | Spring IoC (현재) |
|---|---|---|
| 객체 생성 | `new StudentService()` 직접 | 컨테이너가 `@Service` 스캔 후 자동 |
| 객체 관리 | `init()`/`destroy()` 수동 | 컨테이너가 생명주기 자동 관리 |
| 의존 관계 | 코드에 하드코딩 | 어노테이션/설정으로 선언적 관리 |
| 설정 방식 | `web.xml` + 자바 코드 | `@Configuration` + `@Bean` 또는 자동 스캔 |
| 테스트 | 실제 서버 기동 필수, Mock 어려움 | Mock 주입으로 독립 단위 테스트 가능 |

| 구분 | @Component (자동) | @Bean (수동) |
|---|---|---|
| 대상 | 내가 만든 클래스 | 외부 라이브러리 |
| 선언 | 클래스 레벨 | 메서드 레벨 |
| 등록 | 컴포넌트 스캔 | @Configuration 내 메서드 |

| 구분 | @Primary | @Qualifier |
|---|---|---|
| 역할 | 기본 빈 지정 | 특정 빈 이름 지정 |
| 우선순위 | 2순위 | 1순위 (더 높음) |
| 사용 위치 | 구현체 클래스 | 주입받는 쪽 (생성자 파라미터) |

| DI 방식 | 권장 여부 | 핵심 특징 |
|---|---|---|
| 생성자 주입 | **권장** | `final` + 불변성 + 컴파일 체크 |
| Setter 주입 | 선택적 | 가변, 선택적 의존성에만 |
| 필드 주입 | **지양** | 테스트 어려움, 불변성 보장 불가 |
