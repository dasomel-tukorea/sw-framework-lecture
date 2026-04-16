// Week 07 — Lab 04: BCrypt 비밀번호 암호화 (심화, 20분)
// LoginController.java — UserRepository + BCrypt 인증 버전
package kr.ac.tukorea.swframework.controller;

import jakarta.servlet.http.HttpSession;
import kr.ac.tukorea.swframework.dto.LoginForm;
import kr.ac.tukorea.swframework.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 로그인/로그아웃 Controller — Lab 04 업그레이드
 *
 * Lab 01/03과의 차이점:
 * - 하드코딩 인증 → UserRepository.authenticate() 사용
 * - BCrypt matches()로 비밀번호 검증 (평문 비교 제거)
 * - 생성자 주입으로 UserRepository 의존성 관리
 *
 * @author 학생 이름
 * @since 2026-04-24 (7주차)
 */
@Slf4j
@Controller
public class LoginController {

    // 생성자 주입 (Week 04 DI 복습)
    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    /**
     * 로그인 처리 — BCrypt 기반 인증
     *
     * Lab 01: "admin".equals(loginId) && "1234".equals(password)  ← 하드코딩 비교
     * Lab 04: userRepository.authenticate(loginId, password)       ← BCrypt 비교
     *
     * 확인 포인트:
     * - 같은 '1234' 입력 → 매번 다른 해시값 생성 (Salt)
     * - matches('1234', 해시값) → true
     * - matches('5678', 해시값) → false
     * - encode() 결과끼리 비교 → 항상 false! (반드시 matches() 사용)
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        // === Lab 04 핵심: BCrypt 기반 인증 ===
        if (userRepository.authenticate(loginId, password)) {

            // LoginForm DTO로 세션 저장 (Lab 03 연장)
            String role = "admin".equals(loginId) ? "ADMIN" : "USER";
            String name = "admin".equals(loginId) ? "관리자" : "게스트";

            LoginForm loginUser = new LoginForm(loginId, name, role);
            session.setAttribute("loginUser", loginUser);
            log.info("로그인 성공 (BCrypt 인증): {} ({})", loginId, role);

            return "redirect:/students";
        }

        log.warn("로그인 실패 시도 (BCrypt 인증): {}", loginId);
        model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        return "login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        LoginForm loginUser = (LoginForm) session.getAttribute("loginUser");
        if (loginUser != null) {
            log.info("로그아웃: {} ({})", loginUser.getLoginId(), loginUser.getRole());
        }
        session.invalidate();
        return "redirect:/login";
    }
}
