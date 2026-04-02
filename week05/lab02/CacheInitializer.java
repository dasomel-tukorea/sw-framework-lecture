// src/main/java/kr/ac/tukorea/swframework/component/CacheInitializer.java
package kr.ac.tukorea.swframework.component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 캐시 초기화 컴포넌트 (Bean Lifecycle 시각화 실습)
 *
 * 실무 활용:
 *   서버 시작 시 자주 조회되는 데이터(공통코드, 학과 목록 등)를 미리 캐시에 올려두어
 *   첫 번째 요청부터 빠른 응답을 보장한다.
 *   ("캐시 워밍업(Cache Warm-up)" 패턴)
 */
@Slf4j
@Component
public class CacheInitializer {

    /**
     * @PostConstruct: 서버 시작 시 캐시 데이터 로딩
     */
    @PostConstruct
    public void init() {
        log.info("[2단계] CacheInitializer — 캐시 워밍업 시작");
        log.info("   └ 자주 사용하는 학과 목록 캐싱 완료");
    }

    /**
     * @PreDestroy: 서버 종료 시 캐시 비우기
     */
    @PreDestroy
    public void destroy() {
        log.info("[2단계] CacheInitializer — 캐시 비우기 완료");
    }
}
