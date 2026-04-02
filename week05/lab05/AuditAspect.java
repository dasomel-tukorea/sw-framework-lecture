// src/main/java/kr/ac/tukorea/swframework/aspect/AuditAspect.java
package kr.ac.tukorea.swframework.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * 보안 감사 로깅 Aspect
 * - Service 계층의 모든 메서드 호출을 감사 로그로 기록
 * - 누가(클래스), 언제(시간), 무엇을(메서드+파라미터), 어떤 결과(결과값)를 자동 기록
 *
 * 핵심 포인트:
 *   StudentInfoService 코드를 한 줄도 수정하지 않았는데
 *   이 Aspect 하나로 모든 Service 메서드의 감사 로그가 자동 기록된다.
 *
 * 실무 활용:
 *   정부 행정 시스템, 금융 시스템 등에서 법적으로 접근 기록(Audit Trail)을 남겨야 할 때 사용
 *
 * @AfterReturning 대신 @Around를 사용하는 이유:
 *   실행 전 정보(시간 측정 시작)와 실행 후 결과(반환값 로깅)를 모두 다루기 위함
 */
@Aspect
@Component
@Slf4j
public class AuditAspect {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Service 패키지 하위 모든 메서드에 감사 로깅 적용
     * @AfterReturning 방식이 아닌 @Around를 사용하여 호출 전후 모두 처리
     */
    @Around("execution(* kr.ac.tukorea.swframework.service..*.*(..))")
    public Object auditLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 감사 정보 수집
        String methodName = joinPoint.getSignature().toShortString();
        String parameters = Arrays.toString(joinPoint.getArgs());
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // 2. 호출 전 감사 로그 기록
        log.info("┌─── 감사 로그 (AUDIT) ──────────────────");
        log.info("│ 시간     : {}", timestamp);
        log.info("│ 클래스   : {}", className);
        log.info("│ 메서드   : {}", methodName);
        log.info("│ 파라미터 : {}", parameters);

        // 3. 핵심 메서드 실행 (proceed()를 빠뜨리면 핵심 로직이 실행되지 않음!)
        Object result = joinPoint.proceed();

        // 4. 실행 결과 기록 (결과값이 너무 길면 100자로 잘라서 출력)
        log.info("│ 결과     : {}", result != null
                ? result.toString().substring(0, Math.min(result.toString().length(), 100))
                : "null");
        log.info("└──────────────────────────────────────");

        return result;
    }
}
