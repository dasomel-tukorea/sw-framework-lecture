// Week 07 — Lab 05: 로그인 실패 처리 & 타임아웃 (도전, 25분)
// LoginController.java — 실패 횟수 카운팅 + 계정 잠금 + 세션 타임아웃
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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 로그인/로그아웃 Controller — Lab 05 최종 버전
 *
 * Lab 04에서 추가된 기능:
 * 1. 로그인 실패 횟수 카운팅 — 5회 실패 시 계정 잠금 (5분)
 * 2. 세션 타임아웃 설정 — application.yml에서 설정
 * 3. 계정 잠금 해제 자동 처리
 *
 * 실무 참고:
 * - 실패 횟수는 DB에 저장 (서버 재시작 시에도 유지)
 * - IP 기반 차단도 병행 (Brute Force 방어)
 * - Spring Security에서는 AuthenticationFailureHandler로 처리
 *
 * @author 학생 이름
 * @since 2026-04-24 (7주차)
 */
@Slf4j
@Controller
public class LoginController {

    private final UserRepository userRepository;

    // 로그인 실패 횟수 추적 (메모리 기반 — 실무에서는 DB 사용)
    private final Map<String, Integer> failCountMap = new ConcurrentHashMap<>();

    // 계정 잠금 시간 추적
    private final Map<String, LocalDateTime> lockTimeMap = new ConcurrentHashMap<>();

    // 최대 실패 허용 횟수
    private static final int MAX_FAIL_COUNT = 5;

    // 계정 잠금 시간 (분)
    private static final int LOCK_MINUTES = 5;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    /**
     * 로그인 처리 — 실패 횟수 카운팅 + 계정 잠금
     *
     * 흐름:
     * 1. 계정 잠금 상태 확인 → 잠금 중이면 에러 메시지 반환
     * 2. 잠금 시간 경과 시 자동 해제
     * 3. 인증 성공 → 실패 횟수 초기화 + 세션 저장
     * 4. 인증 실패 → 실패 횟수 증가 → 5회 도달 시 잠금
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        // === 1단계: 계정 잠금 확인 ===
        if (isAccountLocked(loginId)) {
            log.warn("잠금된 계정 로그인 시도: {}", loginId);
            model.addAttribute("error",
                    "로그인 " + MAX_FAIL_COUNT + "회 실패로 계정이 잠겼습니다. "
                            + LOCK_MINUTES + "분 후 다시 시도해주세요.");
            return "login";
        }

        // === 2단계: 인증 처리 ===
        if (userRepository.authenticate(loginId, password)) {
            // 성공 → 실패 횟수 초기화
            failCountMap.remove(loginId);
            lockTimeMap.remove(loginId);

            String role = "admin".equals(loginId) ? "ADMIN" : "USER";
            String name = "admin".equals(loginId) ? "관리자" : "게스트";

            LoginForm loginUser = new LoginForm(loginId, name, role);
            session.setAttribute("loginUser", loginUser);
            log.info("로그인 성공: {} (실패 횟수 초기화)", loginId);

            return "redirect:/students";
        }

        // === 3단계: 실패 처리 ===
        int failCount = failCountMap.merge(loginId, 1, Integer::sum);
        int remaining = MAX_FAIL_COUNT - failCount;
        log.warn("로그인 실패: {} ({}회 / {}회)", loginId, failCount, MAX_FAIL_COUNT);

        if (failCount >= MAX_FAIL_COUNT) {
            // 최대 실패 → 계정 잠금
            lockTimeMap.put(loginId, LocalDateTime.now());
            log.warn("계정 잠금: {} ({}분간)", loginId, LOCK_MINUTES);
            model.addAttribute("error",
                    "로그인 " + MAX_FAIL_COUNT + "회 실패로 계정이 잠겼습니다. "
                            + LOCK_MINUTES + "분 후 다시 시도해주세요.");
        } else {
            model.addAttribute("error",
                    "아이디 또는 비밀번호가 올바르지 않습니다. (남은 시도: " + remaining + "회)");
        }

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

    /**
     * 계정 잠금 여부 확인
     * - 잠금 시간이 LOCK_MINUTES 이상 경과하면 자동 해제
     */
    private boolean isAccountLocked(String loginId) {
        LocalDateTime lockTime = lockTimeMap.get(loginId);
        if (lockTime == null) {
            return false;
        }
        // 잠금 시간 경과 → 자동 해제
        if (lockTime.plusMinutes(LOCK_MINUTES).isBefore(LocalDateTime.now())) {
            failCountMap.remove(loginId);
            lockTimeMap.remove(loginId);
            log.info("계정 잠금 해제: {} ({}분 경과)", loginId, LOCK_MINUTES);
            return false;
        }
        return true;
    }
}
