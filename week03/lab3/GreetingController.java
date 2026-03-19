// Week 03 — 기본 Spring MVC
// GreetingController.java — 계층 분리 컨트롤러 (심화 실습)
package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.service.GreetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 계층 분리 컨트롤러 (심화 실습)
 *
 * - Controller는 요청 접수와 View 반환만 담당한다
 * - 비즈니스 로직은 Service 계층에 위임한다
 * - 생성자 주입(DI)으로 GreetingService를 주입받는다
 */
@Controller
public class GreetingController {

    private final GreetingService greetingService; // 생성자 주입 (DI)

    /**
     * 생성자 주입: Spring이 GreetingService 빈을 자동으로 주입한다.
     * Spring Boot 3.x에서 생성자가 1개이면 @Autowired 생략 가능
     */
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * GET /greeting 요청 처리
     * - @RequestParam으로 URL 파라미터를 받는다.
     * - 예: /greeting?name=홍길동
     *
     * @param name URL 파라미터 (기본값: "학생")
     * @param model View에 전달할 데이터
     * @return "greeting" → templates/greeting.html 렌더링
     */
    @GetMapping("/greeting")
    public String greeting(
            @RequestParam(defaultValue = "학생") String name,
            Model model) {
        String message = greetingService.getGreeting(name); // Service에 위임
        model.addAttribute("message", message);
        return "greeting";
    }
}
