// Week 04 — IoC/DI
// EnglishGreetingService.java — 영어 인사 서비스 구현체
package kr.ac.tukorea.swframework.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * 영어 인사 서비스 구현체
 * - @Primary: 동일 인터페이스의 구현체가 2개일 때 이 빈을 우선 사용
 * - Controller 코드 변경 없이 주입되는 구현체가 바뀌는 것을 체험하는 실습용
 *
 * [도전] @Qualifier로 특정 구현체 지정하기:
 *   @Primary보다 @Qualifier가 우선한다!
 *   Controller 생성자에서 아래와 같이 사용하면 @Primary를 무시하고 Korean이 주입됨:
 *
 *   public GreetingController(@Qualifier("koreanGreetingService") GreetingService svc)
 *
 *   빈 이름 규칙: 클래스명의 첫 글자를 소문자로 변환
 *   - KoreanGreetingService → "koreanGreetingService"
 *   - EnglishGreetingService → "englishGreetingService"
 */
@Service
@Primary // ← 이 어노테이션 하나로 기본 주입 대상이 변경된다
public class EnglishGreetingService implements GreetingService {

    /**
     * 영어로 인사 메시지를 생성한다.
     *
     * @param name 사용자 이름
     * @return 영어 인사 메시지
     */
    @Override
    public String greet(String name) {
        return "Hello, " + name + "! Welcome to SW Framework course.";
    }
}
