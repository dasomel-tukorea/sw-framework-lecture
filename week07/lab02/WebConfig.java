// Week 07 — Lab 02: 인터셉터 로그인 체크 (심화, 20분)
// WebConfig.java — Spring MVC 설정 클래스 (인터셉터 등록)
package kr.ac.tukorea.swframework.config;

import kr.ac.tukorea.swframework.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정 클래스
 *
 * WebMvcConfigurer를 구현하여 Spring MVC의 동작을 커스터마이징한다.
 * 여기서는 인터셉터를 등록하여 비로그인 사용자의 접근을 차단한다.
 *
 * 주의사항:
 * - @Configuration 어노테이션 필수 (Spring이 설정 클래스로 인식)
 * - excludePathPatterns에 /login을 반드시 포함 (무한 리다이렉트 방지)
 * - 정적 리소스(CSS/JS/이미지)도 제외해야 페이지가 정상 렌더링됨
 *
 * @author 학생 이름
 * @since 2026-04-24 (7주차)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 인터셉터 등록
     *
     * addPathPatterns("/**")     — 모든 경로에 인터셉터 적용
     * excludePathPatterns(...)   — 아래 경로는 인터셉터에서 제외
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")              // 모든 경로에 적용
                .excludePathPatterns(
                        "/", "/login", "/logout",     // 홈, 로그인/로그아웃 페이지
                        "/css/**", "/js/**",          // 정적 리소스 (CSS, JavaScript)
                        "/images/**",                 // 정적 리소스 (이미지)
                        "/error"                      // Spring Boot 기본 에러 페이지
                );
    }

    /*
     * [참고] 인터셉터 vs 서블릿 필터 vs AOP
     *
     * | 구분 | 서블릿 필터 | 인터셉터 | AOP |
     * |---|---|---|---|
     * | 실행 위치 | DispatcherServlet 이전 | DispatcherServlet 이후 | 메서드 실행 전후 |
     * | 적용 대상 | 모든 요청 | Controller 요청만 | Bean 메서드 |
     * | Spring 활용 | Spring Bean 사용 어려움 | Spring Bean 사용 가능 | Spring Bean 완전 지원 |
     * | 주 용도 | 인코딩, 보안 | 로그인 체크, 로깅 | 트랜잭션, 로깅 |
     *
     * → 이 과목에서는 로그인 체크에 인터셉터를 사용 (Spring MVC에 최적화)
     */
}
