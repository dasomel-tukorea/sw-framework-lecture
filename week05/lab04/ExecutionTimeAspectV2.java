// Week 05 — AOP & Bean
// ExecutionTimeAspectV2.java — @annotation 기반 Pointcut (커스텀 어노테이션용)
package kr.ac.tukorea.swframework.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 커스텀 @LogExecutionTime 어노테이션이 붙은 메서드만 실행 시간 측정
 *
 * lab01의 ExecutionTimeAspect와 비교:
 * - lab01: execution(* ..service..*.*(..)) → 패키지 전체 대상
 * - lab04: @annotation(..LogExecutionTime) → 특정 메서드만 선택적 적용
 *
 * 장점:
 * - 필요한 메서드만 선택적 AOP → 로그 오버헤드 최소화
 * - @LogExecutionTime 없는 메서드는 AOP 적용 안 됨 → 정밀 제어
 * - 실무에서 가장 많이 사용하는 패턴
 *
 * 주의: lab01의 ExecutionTimeAspect와 동시 사용 시 로그가 중복됨
 *       → 실습 시 lab01 Aspect를 주석 처리하거나 @Component를 제거
 */
@Aspect
@Component
@Slf4j
public class ExecutionTimeAspectV2 {

    @Around("@annotation(kr.ac.tukorea.swframework.annotation.LogExecutionTime)")
    public Object measure(ProceedingJoinPoint jp) throws Throwable {
        String m = jp.getSignature().toShortString();
        log.info(">>> [시작] {}", m);

        long start = System.currentTimeMillis();
        Object result = jp.proceed();
        long t = System.currentTimeMillis() - start;

        log.info("<<< [종료] {} | {}ms", m, t);
        return result;
    }
}
