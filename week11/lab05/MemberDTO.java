// [복사 위치] src/main/java/kr/ac/tukorea/swframework/dto/MemberDTO.java
// [작업] Week 11 lab05 — 회원 정보 영구 저장 (HashMap → member 테이블)
//        - Lombok 미사용 (수강생이 보일러플레이트와 검증 어노테이션의 관계를 직접 확인)
//        - W07 UserRepository 의 loginId 키 호환 — LoginInterceptor / LoginController 무수정
package kr.ac.tukorea.swframework.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 회원 정보를 계층 간에 전달하기 위한 DTO 클래스 — Week 11 lab05
 *
 * member 테이블 컬럼 매핑:
 *   id          BIGINT       PK (AUTO_INCREMENT)
 *   login_id    VARCHAR(50)  UNIQUE  ↔ loginId (W07 UserRepository 호환 키)
 *   password    VARCHAR(255) BCrypt 해시 (60자 + 여유)
 *   name        VARCHAR(50)
 *   email       VARCHAR(100) (선택)
 *   role        VARCHAR(20)  DEFAULT 'USER'
 *   created_at  DATETIME     ↔ createdAt
 *
 * 검증 어노테이션은 회원가입에서만 의미가 있다.
 * 로그인 시에는 별도의 LoginForm 또는 @RequestParam 으로 처리하므로 @Valid 적용 X.
 */
public class MemberDTO {

    private Long id;

    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 4, max = 50, message = "아이디는 4~50자여야 합니다.")
    private String loginId;       // W07 LoginController / UserRepository 와 동일 키

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다.")
    private String password;      // 가입 시 평문 → 서비스에서 BCrypt 해시로 교체

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;         // 선택 입력

    private String role;          // DB DEFAULT 'USER' — 서비스에서 미지정 시 채움

    private LocalDateTime createdAt;

    public MemberDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
