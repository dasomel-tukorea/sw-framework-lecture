// src/main/java/kr/ac/tukorea/swframework/component/DatabaseInitializer.java
package kr.ac.tukorea.swframework.component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * DB 연결 초기화 컴포넌트 (Bean Lifecycle 시각화 실습)
 *
 * InitializingBean 인터페이스 구현으로 afterPropertiesSet() 활용 가능
 * (단, @PostConstruct 방식이 더 현대적이고 권장됨 — Spring 비의존적)
 *
 * 빈 생명주기:
 *   1. 생성자 호출
 *   2. 의존관계 주입(DI) 완료
 *   3. @PostConstruct (init) 호출 ← 이 시점에 초기화 작업 수행
 *   4. 사용
 *   5. @PreDestroy (destroy) 호출 ← 이 시점에 정리 작업 수행
 *   6. 소멸
 */
@Slf4j
@Component
public class DatabaseInitializer implements InitializingBean {

    /**
     * @PostConstruct: 의존관계 주입 완료 직후 자동 호출
     * - DB 연결 풀 초기화, 연결 상태 확인 등에 활용
     * - jakarta.annotation.PostConstruct (Spring Boot 3.x — javax가 아닌 jakarta 패키지)
     */
    @PostConstruct
    public void init() {
        log.info("[1단계] DatabaseInitializer — DB 연결 초기화 완료");
        log.info("   └ 연결 대상: jdbc:h2:mem:testdb");
    }

    /**
     * InitializingBean.afterPropertiesSet(): @PostConstruct와 유사하지만 Spring 의존적
     * - @PostConstruct 이후에 호출됨 (두 방법을 동시에 쓰면 @PostConstruct 먼저 실행)
     * - 실무에서는 @PostConstruct 하나만 사용하는 것이 일반적
     */
    @Override
    public void afterPropertiesSet() {
        log.info("   └ [InitializingBean] afterPropertiesSet() 호출 — DB 설정 검증 완료");
    }

    /**
     * @PreDestroy: 컨테이너 종료 직전 자동 호출
     * - DB 연결 해제, 트랜잭션 롤백 등에 활용
     * - Ctrl+C 정상 종료 시에만 호출됨 (강제 종료 시 미호출)
     */
    @PreDestroy
    public void destroy() {
        log.info("[3단계] DatabaseInitializer — DB 연결 해제 완료");
    }
}
