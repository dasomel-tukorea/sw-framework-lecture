// Week 07 — Lab 04: BCrypt 비밀번호 암호화 (심화, 20분)
// PasswordUtil.java — BCrypt 해시/검증 유틸리티
// 경로: src/main/java/kr/ac/tukorea/swframework/util/PasswordUtil.java
package kr.ac.tukorea.swframework.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 비밀번호 암호화/검증 유틸리티
 *
 * 학습 포인트:
 * 1. BCrypt: 단방향 해시 알고리즘 — 같은 입력도 매번 다른 해시값 생성 (Salt 사용)
 * 2. encode()  : 평문 → BCrypt 해시 (회원가입/비밀번호 변경 시 사용)
 * 3. matches() : 평문 vs 해시 비교 (로그인 검증 시 사용)
 *               — equals()로 비교하면 항상 false! (Salt가 다르기 때문)
 *
 * 의존성 추가 필요 (build.gradle):
 *   implementation 'org.springframework.security:spring-security-crypto'
 *
 * 실무 참고:
 * - BCrypt strength(비용 파라미터) 기본값: 10 (2^10 = 1024번 반복)
 * - 높을수록 안전하지만 느려짐 (12~14 권장)
 * - DB에는 평문이 아닌 BCrypt 해시값을 저장할 것
 *
 * @author 학생 이름
 * @since 2026-04-24 (7주차)
 */
public class PasswordUtil {

    // BCryptPasswordEncoder: Thread-Safe, static 사용 가능
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 평문 비밀번호를 BCrypt 해시로 변환
     * 매번 다른 Salt가 적용되어 동일 입력도 다른 해시 생성
     *
     * @param rawPassword 원본 비밀번호
     * @return 해시된 비밀번호 (매번 다른 값)
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 평문 비밀번호와 BCrypt 해시값이 일치하는지 검증
     * 내부적으로 Salt를 추출하여 동일 조건으로 재해시 후 비교
     *
     * @param rawPassword     사용자가 입력한 원본 비밀번호
     * @param encodedPassword DB에 저장된 해시 비밀번호
     * @return 일치 여부
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    // 외부 인스턴스 생성 방지
    private PasswordUtil() {}
}
