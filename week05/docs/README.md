# Week 05 — 수업 내용

이번 주차에서는 AOP(관점 지향 프로그래밍)와 Bean Scope/생명주기를 학습합니다. 서비스 계층 설계의 핵심 개념입니다.

## 이번 주 목표

- 관점 지향 프로그래밍(AOP, Aspect-Oriented Programming) 개념 이해
- `@Aspect`, `@Around`, `@Before`, `@After` 어노테이션 실습
- Bean Scope (Singleton, Prototype) 차이 이해
- Bean 생명주기 (`@PostConstruct`, `@PreDestroy`) 활용

## 산출물 안내

이번 주차는 서비스 계층의 횡단 관심사(Cross-Cutting Concern) 분리를 학습합니다.
팀 프로젝트에서 로깅, 실행 시간 측정, 트랜잭션 관리 등에 직접 활용됩니다.

### 서비스 계층 설계 포인트

| 개념 | 프로젝트 적용 |
|---|---|
| AOP — 실행 시간 측정 | 모든 Service 메서드의 성능 모니터링 |
| AOP — 로깅 | Controller 진입/반환 로그 자동화 |
| Singleton Scope | Service, Mapper Bean의 기본 스코프 |
| Bean 생명주기 | DB 커넥션 풀 초기화/해제 패턴 |

### 참고 — 완성 프로젝트에서 AOP 적용 예시

```
code/complete/
└── aspect/
    └── ExecutionTimeAspect.java  ← @Around로 Service 메서드 실행 시간 측정
```

## 다음 주차와의 연결

- 4~5주차의 IoC/DI/AOP 기반 위에 6주차에서 Thymeleaf를 활용한 화면(View) 연동을 시작합니다.
- Controller → Service → View 전체 흐름을 처음으로 완성하게 됩니다.
