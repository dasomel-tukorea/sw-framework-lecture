// Week 03 — 기본 Spring MVC
// HelloController.java — 기본 Hello 컨트롤러
package kr.ac.tukorea.swframework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 기본 Hello 컨트롤러
 *
 * - Presentation 계층: 사용자의 HTTP 요청을 받아 적절한 View를 반환한다
 * - @Controller 어노테이션으로 Spring Bean에 자동 등록된다
 */
@Controller
public class HelloController {

    /**
     * GET /hello 요청을 처리하는 핸들러 메서드
     *
     * @param model View에 전달할 데이터를 담는 객체
     * @return "hello" → templates/hello.html을 렌더링
     */
    @GetMapping("/hello")
    public String hello(Model model) {
        // Model에 데이터를 추가하면 Thymeleaf 템플릿에서 사용할 수 있다
        model.addAttribute("name", "SW프레임워크");
        return "hello"; // templates/hello.html과 매핑
    }
}
