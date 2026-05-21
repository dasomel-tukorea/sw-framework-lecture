# Lab 05 — 회원 정보 영구 저장 (선택)

> "W07에서 완성한 BCrypt 자산을 그대로 사용 — HashMap → member 테이블로 저장소만 마이그레이션"

## swframework 현재 인증 구조 (W07 완성)

```java
// PasswordUtil.java (W07 — 이미 BCrypt 적용)
public static String encode(String rawPassword) {
    return encoder.encode(rawPassword);
}
public static boolean matches(String rawPassword, String encodedPassword) {
    return encoder.matches(rawPassword, encodedPassword);
}

// UserRepository.java (W07 — 단, 저장소가 HashMap)
private final Map<String, String> users = new HashMap<>();
public UserRepository() {
    users.put("admin", PasswordUtil.encode("1234"));      // 메모리 저장
    users.put("guest", PasswordUtil.encode("1234"));
}
public boolean authenticate(String loginId, String rawPassword) {
    String hashedPassword = users.get(loginId);
    return hashedPassword != null && PasswordUtil.matches(rawPassword, hashedPassword);
}
```

> **BCrypt 자체는 새로 학습할 게 아님** — 본 lab은 **저장소 마이그레이션 + 회원가입 폼 추가**가 핵심.

## 학습 포인트

- **W07 PasswordUtil 그대로 활용** — 새 PasswordEncoder Bean 만들지 않음
- **HashMap → member 테이블** 마이그레이션 (앱 재시작 시 계정 소실 해결)
- **회원가입 화면 신규** — `POST /members/signup`
- **MemberController가 로그인/로그아웃 통합** — `/members/login`, `/members/logout` (W07 LoginController 의존 제거)

## 파일 (W11 추가분)

| 파일 | 적용 위치 (swframework) | 설명 |
|---|---|---|
| `MemberDTO.java` | `dto/` | **신규** (`kr.ac.tukorea.swframework.dto.MemberDTO`) |
| `LoginForm.java` | `dto/` | W07 자산을 무-Lombok 형태로 정리한 세션 보관용 DTO |
| `MemberMapper.java` / `MemberMapper.xml` | `mapper/` · `resources/mapper/` | **신규** — `insert`, `findByLoginId`, `findById` |
| `MemberService.java` | `service/` | **신규** — `signup(MemberDTO)`, `login(loginId, password)` (PasswordUtil 재사용) |
| `MemberController.java` | `controller/` | **신규** — 회원가입 + 로그인 + 로그아웃을 `/members` 하위로 통합 |

## member 테이블

```sql
CREATE TABLE member (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    login_id   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,           -- BCrypt 해시 (60자 + 여유)
    name       VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NULL,               -- MemberDTO @Email — 선택 입력
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- 기존 admin/guest 마이그레이션 (PasswordUtil.encode("1234")로 생성한 해시로 교체)
INSERT INTO member (login_id, password, name, role) VALUES
('admin', '<PasswordUtil.encode(\"1234\") 결과>', '관리자', 'ADMIN'),
('guest', '<PasswordUtil.encode(\"1234\") 결과>', '게스트', 'USER');
```

## 회원가입 흐름

```
[사용자]
  POST /members/signup
  loginId=user01&password=plain1234&name=홍길동
  │
  ▼
[MemberController.signup()]   @Valid MemberDTO + BindingResult
  │
  ▼  PasswordUtil.encode("plain1234")        ← W07 자산 재사용
  │  → "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
[MemberService.signup()]
  │  role 미지정 시 'USER' 기본값
  ▼  memberMapper.insert(memberDTO)
[DB]
  member 테이블에 영구 저장 (앱 재시작해도 유지)
  │
  ▼  redirect:/members/login   (PRG)
```

## 로그인 흐름 (MemberController로 통합)

```
[사용자]
  POST /members/login  loginId=user01&password=plain1234
  │
  ▼  memberService.login(loginId, password)      ← UserRepository 대체
[MemberService.login()]
  │  memberMapper.findByLoginId("user01")
  ▼  → "$2a$10$N9qo8uL..." 해시 조회 (DB)
  │  PasswordUtil.matches("plain1234", 해시)     ← W07 자산 재사용
  ▼  → MemberDTO / null
[MemberController.login()]
  ├─ 성공 → session.setAttribute("loginUser", new LoginForm(loginId, name, role))
  │         session.setMaxInactiveInterval(1800)  → redirect:/students
  └─ 실패 → model.addAttribute("error", ...) → member/login
```

## MemberService 핵심 코드 (PasswordUtil 재사용)

```java
// src/main/java/kr/ac/tukorea/swframework/service/MemberService.java
@Service
@Transactional
public class MemberService {

    private final MemberMapper memberMapper;

    public MemberService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    /** 회원가입 — W07 PasswordUtil.encode() 재사용 */
    public void signup(MemberDTO memberDTO) {
        String hashed = PasswordUtil.encode(memberDTO.getPassword());
        memberDTO.setPassword(hashed);

        if (memberDTO.getRole() == null || memberDTO.getRole().isBlank()) {
            memberDTO.setRole("USER");
        }
        memberMapper.insert(memberDTO);
    }

    /** 로그인 — DB 조회 + W07 PasswordUtil.matches() 검증 */
    @Transactional(readOnly = true)
    public MemberDTO login(String loginId, String rawPassword) {
        MemberDTO member = memberMapper.findByLoginId(loginId);
        if (member == null) return null;
        if (!PasswordUtil.matches(rawPassword, member.getPassword())) return null;
        return member;
    }
}
```

## MemberController — 회원가입 + 로그인 통합

```java
// src/main/java/kr/ac/tukorea/swframework/controller/MemberController.java
@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("memberDTO") MemberDTO memberDTO,
                         BindingResult result) {
        if (result.hasErrors()) return "member/signup";
        memberService.signup(memberDTO);
        return "redirect:/members/login";           // PRG
    }

    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session, Model model) {
        MemberDTO member = memberService.login(loginId, password);
        if (member == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "member/login";
        }

        // W07 LoginInterceptor 호환 — 세션 키 "loginUser" + LoginForm 보관
        LoginForm loginUser = new LoginForm(member.getLoginId(), member.getName(), member.getRole());
        session.setAttribute("loginUser", loginUser);
        session.setMaxInactiveInterval(1800);       // 30분

        return "redirect:/students";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/members/login";
    }
}
```

> W07 계정 잠금(5회/5분) 로직은 본 lab 범위 외 — 필요 시 `MemberController.login()` 앞단에 W07 LoginController 의 `failCountMap` / `lockTimeMap` 로직을 그대로 이식할 수 있다.

## 마이그레이션 매핑 (W07 → W11)

| 항목 | Before (W07) | After (W11) |
|---|---|---|
| 비밀번호 해싱 | PasswordUtil.encode (BCrypt) | **동일** — 재사용 |
| 검증 | PasswordUtil.matches | **동일** — 재사용 |
| 저장소 | `UserRepository` HashMap | **member 테이블** |
| 회원가입 | 없음 (앱 시작 시 admin·guest만) | **POST /members/signup** 폼 신규 |
| 로그인 컨트롤러 | W07 `LoginController` (UserRepository 사용) | **W11 `MemberController`** (`/members/login`) — MemberService 의존 |
| 계정 잠금 (5회 → 5분) | ConcurrentHashMap | **유지 가능** (본 lab 범위 외 — 필요 시 이식) |

## 확인 포인트

- [ ] 회원가입 후 DB `member.password` 컬럼이 `$2a$10$...` 형식 (W07 PasswordUtil 결과)
- [ ] 앱 재시작 후에도 admin·guest 로그인 가능 (DB 영속)
- [ ] 회원가입한 user01도 재시작 후 로그인 가능
- [ ] 틀린 비밀번호 5회 → 5분 계정 잠금 (W07 로직 유지)
- [ ] DB `password VARCHAR(20)`이면 해시 잘려 항상 실패 → `VARCHAR(255)` 필수

## 흔한 실수 4가지

| 실수 | 결과 | 해결 |
|---|---|---|
| `VARCHAR(20)` 컬럼 | BCrypt 해시 60자가 잘림 | `VARCHAR(255)` |
| 새 `BCryptPasswordEncoder` Bean 등록 | W07 `PasswordUtil`의 static `encode/matches`와 중복 | W07 `PasswordUtil` 정적 메서드 그대로 재사용 |
| `findByEmail` 같은 다른 키로 조회 | 인증 로직 어긋남 | `MemberMapper.findByLoginId(loginId)` 로 통일 |
| admin/guest를 평문으로 INSERT | 로그인 실패 (해시 비교 X) | `PasswordUtil.encode("1234")`로 미리 해시 생성 후 INSERT |

## 주차 연결

- **W07** PasswordUtil + UserRepository + 계정 잠금 → 본 lab의 **자산 재사용 + 저장소 확장**
- **W10 lab02** `@ControllerAdvice` → 회원가입 검증 실패도 깔끔하게
- **W12+** Docker — member 테이블도 컨테이너 DB로 함께 배포
