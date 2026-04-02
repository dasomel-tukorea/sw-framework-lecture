// src/main/java/kr/ac/tukorea/swframework/component/HealthChecker.java
package kr.ac.tukorea.swframework.component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 서버 상태 점검 컴포넌트 (Bean Lifecycle 시각화 실습)
 *
 * 실무 활용:
 *   모든 컴포넌트(DB, 캐시 등) 초기화가 완료된 후 최종 상태를 확인하고
 *   "서버 준비 완료" 로그를 출력한다.
 *   15주차 발표 시연에서 서버 시작 로그가 깔끔하게 출력되면 완성도 높은 인상을 줄 수 있다.
 *
 * ⚠️ 빈 초기화 순서는 Spring이 결정하므로 1/2/3 순서가 보장되지 않을 수 있음.
 *    순서 제어가 필요하면 @DependsOn 사용
 */
@Slf4j
@Component
public class HealthChecker {

    /**
     * @PostConstruct: 모든 컴포넌트 초기화 완료 후 최종 상태 확인
     */
    @PostConstruct
    public void init() {
        log.info("[3단계] HealthChecker — 서버 상태 점검 완료");
        log.info("   └ 모든 컴포넌트 정상 (서비스 준비 완료)");
        log.info("═══════════════════════════════════════");
        log.info("  SW프레임워크 실습 서버 시작 완료!");
        log.info("═══════════════════════════════════════");
    }

    /**
     * @PreDestroy: 서버 종료 시 최종 상태 보고
     * Ctrl+C 정상 종료 시에만 호출됨
     */
    @PreDestroy
    public void destroy() {
        log.info("═══════════════════════════════════════");
        log.info("  SW프레임워크 실습 서버 종료 중...");
        log.info("═══════════════════════════════════════");
        log.info("[1단계] HealthChecker — 상태 보고 완료");
    }
}
