// [복사 위치] src/main/java/kr/ac/tukorea/swframework/dto/LoginForm.java
// [작업] Week 11 lab05 — W07 LoginForm 패턴을 무-Lombok 형태로 정리.
//        - 로그인 성공 시 세션에 저장될 사용자 식별 정보 컨테이너
//        - Serializable — 세션 클러스터링/분산 환경에서도 안전하게 직렬화
package kr.ac.tukorea.swframework.dto;

import java.io.Serializable;

/**
 * 로그인 세션에 저장되는 사용자 정보 — Week 07 자산을 lab05 에 맞춰 유지.
 *
 * 사용 위치:
 *   - LoginController(W07) / MemberController(W11) 로그인 성공 시
 *     session.setAttribute("loginUser", new LoginForm(...))
 *   - LoginInterceptor 가 session.getAttribute("loginUser") 로 인증 여부 확인
 *   - 화면에서 ${session.loginUser.name} / .role 등으로 접근
 *
 * 필드는 W07 dto/LoginForm.java 의 시그니처와 동일하게 유지한다.
 */
public class LoginForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String loginId;
    private String name;
    private String role;

    public LoginForm() {}

    public LoginForm(String loginId, String name, String role) {
        this.loginId = loginId;
        this.name = name;
        this.role = role;
    }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    /** ADMIN 권한 여부 — 화면/인터셉터에서 권한 분기에 사용 */
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }
}
