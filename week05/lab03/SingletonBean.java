// src/main/java/kr/ac/tukorea/swframework/component/SingletonBean.java
package kr.ac.tukorea.swframework.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Singleton 스코프 빈 (기본값)
 * - Spring 컨테이너에 단 하나의 인스턴스만 생성됨
 * - 애플리케이션 전체에서 동일한 객체를 공유
 *
 * 확인 방법:
 *   http://localhost:8080/scope-test 접속 후 콘솔 로그 확인
 *   → SingletonBean 생성자는 단 1번만 호출됨
 *   → context.getBean()을 여러 번 호출해도 같은 인스턴스 반환 (s1 == s2: true)
 */
@Slf4j
@Component // 기본 Singleton — 별도 @Scope 설정 없음
public class SingletonBean {

    // 요청 횟수 카운터 (싱글톤이므로 모든 요청이 공유하는 상태)
    private int requestCount = 0;

    public SingletonBean() {
        log.info("[Singleton] 생성자 호출 — 인스턴스: {}", this);
    }

    /**
     * 요청 횟수 증가 및 반환
     * 싱글톤이므로 여러 요청이 동일한 카운터를 공유한다.
     */
    public int incrementAndGet() {
        return ++requestCount;
    }
}
