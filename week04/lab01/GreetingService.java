// Week 04 — IoC/DI
// GreetingService.java — 인사 서비스 인터페이스
package kr.ac.tukorea.swframework.service;

/**
 * 인사 서비스 인터페이스
 * - 구현체를 교체해도 Controller 코드를 수정할 필요 없음
 * - 인터페이스 타입으로 DI 받으면 결합도가 낮아진다
 */
public interface GreetingService {

    /**
     * 사용자 이름을 받아 인사 메시지를 반환한다.
     *
     * @param name 사용자 이름
     * @return 인사 메시지 문자열
     */
    String greet(String name);
}
