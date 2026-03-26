// Week 04 — IoC/DI
// KoreanGreetingService.java — 한국어 인사 서비스 구현체
package kr.ac.tukorea.swframework.service;

import org.springframework.stereotype.Service;

/**
 * 한국어 인사 서비스 구현체
 * - @Service: Spring Bean으로 자동 등록 (컴포넌트 스캔 대상)
 * - GreetingService 인터페이스를 구현한다
 */
@Service
public class KoreanGreetingService implements GreetingService {

    /**
     * 한국어로 인사 메시지를 생성한다.
     *
     * @param name 사용자 이름
     * @return 한국어 인사 메시지
     */
    @Override
    public String greet(String name) {
        return name + "님, 안녕하세요! SW프레임워크에 오신 것을 환영합니다.";
    }
}
