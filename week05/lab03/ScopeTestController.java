// Week 05 — AOP & Bean 생명주기
// ScopeTestController.java — Singleton vs Prototype 스코프 차이 확인
package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.component.PrototypeBean;
import kr.ac.tukorea.swframework.component.SingletonBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bean Scope 차이를 확인하는 테스트용 컨트롤러
 *
 * Singleton vs Prototype 비교:
 *   - Singleton(기본값): 컨테이너에 단 하나의 인스턴스만 생성, 모든 요청에서 공유
 *   - Prototype: 요청할 때마다 새로운 인스턴스를 생성
 *
 * 테스트 방법:
 *   1. 서버 실행 후 브라우저에서 http://localhost:8080/scope-test 접속
 *   2. 결과 확인: "Singleton 동일? true | Prototype 동일? false"
 *   3. 새로고침을 해도 Singleton은 항상 같은 인스턴스 (해시값 동일)
 */
@RestController
public class ScopeTestController {

    // ApplicationContext를 주입받아 수동으로 빈을 조회
    // (일반적으로는 생성자 주입을 사용하지만, Scope 테스트를 위해 의도적으로 사용)
    private final ApplicationContext context;

    public ScopeTestController(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Singleton과 Prototype 빈의 인스턴스 동일성을 비교하는 API
     *
     * @return 비교 결과 문자열
     *   - Singleton: getBean()을 여러 번 호출해도 항상 같은 인스턴스 (== true)
     *   - Prototype: getBean()을 호출할 때마다 새 인스턴스 생성 (== false)
     */
    @GetMapping("/scope-test")
    public String scopeTest() {
        // Singleton 빈: 두 번 조회해도 동일한 인스턴스
        SingletonBean s1 = context.getBean(SingletonBean.class);
        SingletonBean s2 = context.getBean(SingletonBean.class);

        // Prototype 빈: 조회할 때마다 새로운 인스턴스 생성
        PrototypeBean p1 = context.getBean(PrototypeBean.class);
        PrototypeBean p2 = context.getBean(PrototypeBean.class);

        return String.format(
            "Singleton 동일? %s (s1=%s, s2=%s) | Prototype 동일? %s (p1=%s, p2=%s)",
            s1 == s2, s1.hashCode(), s2.hashCode(),  // true  (같은 인스턴스)
            p1 == p2, p1.hashCode(), p2.hashCode()    // false (다른 인스턴스)
        );
    }
}
