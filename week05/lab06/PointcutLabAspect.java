// src/main/java/kr/ac/tukorea/swframework/aspect/PointcutLabAspect.java
package kr.ac.tukorea.swframework.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Pointcut 표현식 실험실
 *
 * 아래 3가지 Pointcut을 하나씩 주석 해제하며 적용 범위를 직접 관찰한다.
 * ⚠️ 한 번에 하나만 활성화할 것 — 동시에 여러 개 활성화하면 로그가 중복 출력됨
 *
 * 실험 절차:
 *   1. 실험 1 활성화 → 서버 재시작 → URL 호출 → 로그 확인
 *   2. 실험 1 주석 처리, 실험 2 주석 해제 → 반복
 *   3. 실험 2 주석 처리, 실험 3 주석 해제 → 반복
 *
 * 테스트 URL:
 *   http://localhost:8080/greeting?name=홍길동
 *   http://localhost:8080/audit/student?id=1
 */
@Aspect
@Component
@Slf4j
public class PointcutLabAspect {

    // ── 실험 1: execution — 패키지 + 메서드 시그니처 기반 ──
    // service 패키지 하위의 모든 클래스의 모든 메서드에 적용
    // (반환타입 패키지..클래스.메서드(파라미터) 형식 — 맨 앞 * 반드시 필요)
    @Before("execution(* kr.ac.tukorea.swframework.service..*.*(..))")
    public void experimentExecution(JoinPoint joinPoint) {
        log.info("[실험1-execution] 적용됨 → {}", joinPoint.getSignature().toShortString());
    }

    // ── 실험 2: within — 클래스/패키지 기반 (주석 해제하여 실험) ──
    // StudentInfoService 클래스의 모든 메서드에만 적용
    // (GreetingService 호출 시에는 로그가 출력되지 않음)
    // @Before("within(kr.ac.tukorea.swframework.service.StudentInfoService)")
    // public void experimentWithin(JoinPoint joinPoint) {
    //     log.info("[실험2-within] 적용됨 → {}", joinPoint.getSignature().toShortString());
    // }

    // ── 실험 3: @annotation — 특정 어노테이션이 붙은 메서드만 (주석 해제하여 실험) ──
    // @LogExecutionTime 어노테이션이 붙은 메서드에만 적용
    // (메서드 단위 정밀 제어 — @Transactional, @Cacheable과 동일한 원리)
    // @Before("@annotation(kr.ac.tukorea.swframework.annotation.LogExecutionTime)")
    // public void experimentAnnotation(JoinPoint joinPoint) {
    //     log.info("[실험3-@annotation] 적용됨 → {}", joinPoint.getSignature().toShortString());
    // }
}
