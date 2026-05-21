// [복사 위치] src/main/java/kr/ac/tukorea/swframework/service/MemberService.java
// [작업] Week 11 lab05 — 회원가입/로그인 서비스
//        - Lombok 미사용 (생성자 주입 패턴을 수강생이 직접 확인)
//        - W07 PasswordUtil 정적 메서드를 그대로 재사용 (encoder Bean 추가 안 함)
//        - 저장소만 HashMap → member 테이블로 마이그레이션
package kr.ac.tukorea.swframework.service;

import kr.ac.tukorea.swframework.dto.MemberDTO;
import kr.ac.tukorea.swframework.mapper.MemberMapper;
import kr.ac.tukorea.swframework.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입/로그인 서비스 — Week 11 lab05
 *
 * 마이그레이션 매핑 (W07 → W11):
 *   비밀번호 해싱  : PasswordUtil.encode  →  동일 (재사용)
 *   검증          : PasswordUtil.matches →  동일 (재사용)
 *   저장소        : HashMap              →  member 테이블 (영구 저장)
 *   회원가입      : 없음                  →  /members/signup 신규
 *   로그인 키      : loginId              →  동일 (W07 UserRepository 호환)
 */
@Service
@Transactional
public class MemberService {

    private final MemberMapper memberMapper;

    // 생성자 주입 — PasswordEncoder Bean 없이 PasswordUtil 정적 메서드 사용
    public MemberService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    /**
     * 회원가입 처리.
     *   1) 평문 비밀번호를 W07 PasswordUtil.encode() 로 BCrypt 해시 변환
     *   2) role 이 비어 있으면 'USER' 로 기본값 세팅
     *   3) DB 에 영구 저장 (앱 재시작에도 유지)
     */
    public void signup(MemberDTO memberDTO) {
        String hashed = PasswordUtil.encode(memberDTO.getPassword());
        memberDTO.setPassword(hashed);

        if (memberDTO.getRole() == null || memberDTO.getRole().isBlank()) {
            memberDTO.setRole("USER");
        }

        memberMapper.insert(memberDTO);
    }

    /**
     * 로그인 인증.
     *   1) loginId 로 회원 조회 (DB)
     *   2) W07 PasswordUtil.matches() 로 평문 vs BCrypt 해시 비교 (equals 금지)
     *
     * @return 인증 성공 시 MemberDTO, 실패 시 null
     */
    @Transactional(readOnly = true)
    public MemberDTO login(String loginId, String rawPassword) {
        MemberDTO member = memberMapper.findByLoginId(loginId);

        // loginId 미존재
        if (member == null) {
            return null;
        }

        // PasswordUtil.matches — Salt 자동 비교 (equals 금지)
        if (!PasswordUtil.matches(rawPassword, member.getPassword())) {
            return null;
        }

        return member;
    }
}
