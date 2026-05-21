// [복사 위치] src/main/java/kr/ac/tukorea/swframework/controller/MemberController.java
// [작업] Week 11 lab05 — 회원가입/로그인/로그아웃 컨트롤러
//        - URL 은 REST 스타일 /members (복수형) 유지
//        - Lombok 미사용 — 생성자 주입과 세션 처리 흐름을 수강생이 직접 확인
//        - 로그인 성공 시 W07 LoginForm 패턴으로 세션 저장 (LoginInterceptor 무수정 호환)
package kr.ac.tukorea.swframework.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.ac.tukorea.swframework.dto.LoginForm;
import kr.ac.tukorea.swframework.dto.MemberDTO;
import kr.ac.tukorea.swframework.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 회원가입/로그인 컨트롤러 — Week 11 lab05
 *
 * URL 매핑 (REST 스타일):
 *   GET  /members/signup  — 회원가입 폼
 *   POST /members/signup  — 회원가입 처리 (PRG)
 *   GET  /members/login   — 로그인 폼
 *   POST /members/login   — 로그인 처리
 *   POST /members/logout  — 로그아웃 처리
 *
 * 로그인 성공 시:
 *   session.setAttribute("loginUser", new LoginForm(loginId, name, role))
 *   → W07 LoginInterceptor 가 "loginUser" 키로 인증 여부 확인 (무수정 호환)
 */
@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // ── 회원가입 ───────────────────────────────────────

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "member/signup";
    }

    /**
     * 회원가입 처리.
     *   @Valid       — MemberDTO 의 Bean Validation 어노테이션 실행
     *   BindingResult — 검증 결과 (에러 정보 포함), @Valid 바로 뒤에 선언 필수
     */
    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("memberDTO") MemberDTO memberDTO,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "member/signup";
        }
        memberService.signup(memberDTO);
        return "redirect:/members/login";  // PRG 패턴
    }

    // ── 로그인 ────────────────────────────────────────

    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    /**
     * 로그인 처리 — loginId + password (W07 LoginController 와 동일 키).
     *
     * 인증은 MemberService.login() 이 W07 PasswordUtil.matches() 로 처리.
     * 계정 잠금(5회 → 5분) 로직은 W07 LoginController 에 그대로 유지 가능 — 본 lab 범위 외.
     */
    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        MemberDTO member = memberService.login(loginId, password);

        if (member == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "member/login";
        }

        // 인증 성공 — W07 LoginForm 패턴으로 세션 저장 (LoginInterceptor 호환)
        LoginForm loginUser = new LoginForm(member.getLoginId(), member.getName(), member.getRole());
        session.setAttribute("loginUser", loginUser);
        session.setMaxInactiveInterval(1800); // 30분

        return "redirect:/students";
    }

    // ── 로그아웃 ───────────────────────────────────────

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/members/login";
    }
}
