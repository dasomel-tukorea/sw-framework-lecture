// src/main/java/kr/ac/tukorea/swframework/annotation/LogExecutionTime.java
package kr.ac.tukorea.swframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 커스텀 어노테이션: 메서드 실행 시간 측정
 *
 * 이 어노테이션이 붙은 메서드는 ExecutionTimeAspect에 의해
 * 실행 시간이 자동으로 측정되어 콘솔에 출력된다.
 *
 * 사용 예시:
 *   @LogExecutionTime
 *   public String greet(String name) { ... }
 *
 * 메타 어노테이션 설명:
 *   @Target(METHOD): 메서드에만 이 어노테이션을 붙일 수 있음
 *   @Retention(RUNTIME): 런타임에도 어노테이션 정보가 유지됨
 *                        → Spring AOP가 @annotation Pointcut으로 읽을 수 있음
 *                        → RUNTIME이 아니면 AOP가 동작하지 않음!
 */
@Target(ElementType.METHOD)           // 이 어노테이션을 어디에 붙일 수 있는가? → 메서드에만
@Retention(RetentionPolicy.RUNTIME)   // 이 어노테이션 정보를 언제까지 유지하는가? → 런타임까지
public @interface LogExecutionTime {
}
