// Week 07 — Lab 03: 세션에 객체 저장 (심화, 20분)
// LoginController.java — LoginForm DTO를 세션에 저장하는 버전
package kr.ac.tukorea.swframework.controller;

import jakarta.servlet.http.HttpSession;
import kr.ac.tukorea.swframework.dto.LoginForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 로그인/로그아웃 Controller — Lab 03 업그레이드
 *
 * Lab 01과의 차이점:
 * - 세션에 String(loginId)이 아닌 LoginForm DTO 객체를 저장
 * - LoginForm에 아이디, 이름, 권한 정보를 포함하여 화면에서 활용 가능
 * - Thymeleaf에서 ${loginUser.loginId}, ${loginUser.name}, ${loginUser.admin} 사용
 *
 * @author 학생 이름
 * @since 2026-04-24 (7주차)
 */
@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    /**
     * 로그인 처리 — LoginForm DTO를 세션에 저장
     *
     * Lab 01: session.setAttribute("loginUser", loginId)        ← String 저장
     * Lab 03: session.setAttribute("loginUser", new LoginForm(...)) ← 객체 저장
     *
     * 이점: 화면에서 사용자 이름, 권한 등 다양한 정보 활용 가능
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (("admin".equals(loginId) || "guest".equals(loginId)) && "1234".equals(password)) {

            // === Lab 03 핵심: 객체를 세션에 저장 ===
            // 권한 결정: admin → "ADMIN", 나머지 → "USER"
            String role = "admin".equals(loginId) ? "ADMIN" : "USER";
            String name = "admin".equals(loginId) ? "관리자" : "게스트";

            // LoginForm DTO 생성 → 세션에 저장
            LoginForm loginUser = new LoginForm(loginId, name, role);
            session.setAttribute("loginUser", loginUser);
            log.info("로그인 성공: {} ({})", loginId, role);

            return "redirect:/students";
        }

        log.warn("로그인 실패 시도: {}", loginId);
        model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        return "login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // 세션에 LoginForm 객체가 저장되어 있으므로 캐스팅하여 정보 추출
        LoginForm loginUser = (LoginForm) session.getAttribute("loginUser");
        if (loginUser != null) {
            log.info("로그아웃: {} ({})", loginUser.getLoginId(), loginUser.getRole());
        }
        session.invalidate();
        return "redirect:/login";
    }
}
