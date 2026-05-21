// [복사 위치] src/main/java/kr/ac/tukorea/swframework/mapper/MemberMapper.java
// [작업] Week 11 lab05 — 회원 가입/조회용 MyBatis Mapper 인터페이스
//        namespace 는 본 인터페이스의 FQCN 과 정확히 일치해야 한다.
package kr.ac.tukorea.swframework.mapper;

import kr.ac.tukorea.swframework.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 회원 CRUD Mapper — Week 11 lab05
 *
 * 본 lab 의 핵심 시나리오:
 *   1) signup → insert(MemberDTO)   [password 는 BCrypt 해시 상태로 저장]
 *   2) login  → findByLoginId(loginId) → PasswordUtil.matches 로 검증
 *   3) 세션 갱신 시 id 로 단건 재조회 가능 (findById)
 */
@Mapper
public interface MemberMapper {

    /** 회원 등록 — password 는 BCrypt 해시값이 전달됨 */
    void insert(MemberDTO memberDTO);

    /** loginId 로 회원 조회 (로그인 인증용 — W07 LoginController 호환) */
    MemberDTO findByLoginId(String loginId);

    /** id 로 회원 단건 조회 (관리 화면 등에서 사용) */
    MemberDTO findById(Long id);
}
