// Week 07 — Lab 04: BCrypt 비밀번호 암호화 (심화, 20분)
// UserRepository.java — 메모리 기반 사용자 저장소 (BCrypt 적용)
// 경로: src/main/java/kr/ac/tukorea/swframework/repository/UserRepository.java
package kr.ac.tukorea.swframework.repository;

import kr.ac.tukorea.swframework.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 메모리 기반 사용자 저장소 — 9주차(DB 연동) 전까지 임시 사용
 *
 * 학습 포인트:
 * 1. @Repository: Spring 데이터 접근 계층 Bean 등록
 * 2. 생성자에서 테스트 계정을 BCrypt 해시로 등록 (평문 저장 금지!)
 * 3. authenticate(): 입력 비밀번호를 BCrypt matches()로 검증
 *
 * 실무에서는:
 * - UserMapper.findByUsername(loginId) 로 DB 조회
 * - BCryptPasswordEncoder.matches(rawPw, user.getHashedPw()) 로 검증
 * - Spring Security 사용 시 이 클래스 전체가 UserDetailsService로 대체됨
 *
 * 테스트 계정:
 *   admin / 1234 (관리자)
 *   guest / 1234 (게스트)
 *
 * @author 학생 이름
 * @since 2026-04-24 (7주차)
 */
@Slf4j
@Repository
public class UserRepository {

    // 사용자 저장소: loginId → 해시된 비밀번호
    private final Map<String, String> users = new HashMap<>();

    /**
     * 애플리케이션 시작 시 테스트 계정을 BCrypt 해시로 등록
     * 실무에서는 DB에 이미 저장된 해시값을 조회함
     */
    public UserRepository() {
        // 평문 "1234"를 BCrypt 해시로 변환하여 저장
        users.put("admin", PasswordUtil.encode("1234"));
        users.put("guest", PasswordUtil.encode("1234"));
        log.info("UserRepository 초기화 완료 — 테스트 계정 2개 등록 (BCrypt 암호화 적용)");
    }

    /**
     * 사용자 인증 — BCrypt matches()로 비밀번호 검증
     *
     * @param loginId     로그인 아이디
     * @param rawPassword 원본 비밀번호 (사용자 입력값)
     * @return 인증 성공 여부
     */
    public boolean authenticate(String loginId, String rawPassword) {
        String hashedPassword = users.get(loginId);
        if (hashedPassword == null) {
            log.debug("존재하지 않는 사용자: {}", loginId);
            return false; // 아이디 없음
        }
        // BCrypt.matches(): 평문 vs 해시 비교 (equals() 사용 금지!)
        boolean result = PasswordUtil.matches(rawPassword, hashedPassword);
        log.debug("인증 결과 — loginId: {}, 성공: {}", loginId, result);
        return result;
    }

    /**
     * 사용자 존재 여부 확인
     *
     * @param loginId 확인할 로그인 아이디
     * @return 존재하면 true
     */
    public boolean existsByLoginId(String loginId) {
        return users.containsKey(loginId);
    }

    /**
     * 회원가입 (비밀번호를 BCrypt로 해시하여 저장)
     *
     * @param loginId     새 사용자의 로그인 아이디
     * @param rawPassword 원본 비밀번호 (해시 후 저장)
     */
    public void register(String loginId, String rawPassword) {
        if (existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 존재하는 아이디: " + loginId);
        }
        users.put(loginId, PasswordUtil.encode(rawPassword));
        log.info("신규 사용자 등록: {}", loginId);
    }
}
