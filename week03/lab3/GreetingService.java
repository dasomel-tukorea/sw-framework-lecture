// Week 03 — 기본 Spring MVC
// GreetingService.java — 인사 서비스 (Business Logic 계층)
package kr.ac.tukorea.swframework.service;

import org.springframework.stereotype.Service;

/**
 * 인사 서비스 — Business Logic 계층
 *
 * - Controller에서 직접 로직을 처리하지 않고, Service에 위임한다
 * - @Service는 @Component의 특수화 어노테이션이다
 */
@Service
public class GreetingService {

    /**
     * 사용자 이름을 받아 인사 메시지를 생성한다.
     *
     * @param name 사용자 이름
     * @return 인사 메시지 문자열
     */
    public String getGreeting(String name) {
        return name + "님, SW프레임워크에 오신 것을 환영합니다!";
    }
}
