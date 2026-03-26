// Week 04 — IoC/DI
// GreetingController.java — 인터페이스 기반 DI 컨트롤러
package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.service.GreetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 인터페이스 기반 DI 컨트롤러
 * - GreetingService 인터페이스 타입으로 주입받는다 (구현체 타입 아님!)
 * - 생성자 주입 + final 필드로 불변성을 보장한다
 * - 구현체가 바뀌어도 이 코드는 한 줄도 수정할 필요 없다
 */
@Controller
public class GreetingController {

    private final GreetingService greetingService; // 인터페이스 타입으로 선언

    /**
     * 생성자 주입: Spring이 GreetingService 구현체를 자동으로 주입한다.
     * - @Primary가 붙은 구현체가 우선 주입됨
     * - Spring Boot 3.x에서 생성자가 1개이면 @Autowired 생략 가능
     *
     * @param greetingService Spring이 주입하는 GreetingService 구현체
     */
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * GET /greeting 요청 처리
     *
     * @param name URL 파라미터 (기본값: "학생")
     * @param model View에 전달할 데이터
     * @return "greeting" → templates/greeting.html 렌더링
     */
    @GetMapping("/greeting")
    public String greeting(
            @RequestParam(defaultValue = "학생") String name,
            Model model) {
        String message = greetingService.greet(name); // 인터페이스 메서드 호출
        model.addAttribute("message", message);
        return "greeting";
    }
}
