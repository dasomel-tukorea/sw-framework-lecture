// [복사 위치] src/main/java/kr/ac/tukorea/swframework/config/XssEscapeFilterConfig.java
// [작업] 파일이 없으면 생성 (config 패키지 없으면 함께 생성)
//
// ┌─────────────────────────────────────────────────────────────────┐
// │  XSS Servlet Filter — 동작 원리 학습용 구현                      │
// │                                                                 │
// │  "서블릿 필터 레벨 XSS 방어" 가 어떻게 동작하는지 이해합니다.     │
// │                                                                 │
// │  (Spring Boot 3.x / Jakarta EE 환경에서 직접 구현한 버전)          │
// └─────────────────────────────────────────────────────────────────┘
//
// 핵심 패턴: Decorator (HttpServletRequestWrapper)
//   → 요청 파라미터를 꺼낼 때마다 HtmlUtils.htmlEscape() 를 자동 적용
//   → Controller 가 값을 받는 시점에는 이미 이스케이프된 상태
//
// 적용 URL: /students/filter-test 만 (다른 URL에는 영향 없음)

package kr.ac.tukorea.swframework.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class XssEscapeFilterConfig {

    /**
     * XSS 이스케이프 필터 등록
     *
     * addUrlPatterns("/students/filter-test"): 필터 테스트 URL 에만 적용
     * → 실제 운영에서는 "/*" 로 전체 적용 후 예외 URL 만 별도 설정에서 제외
     */
    @Bean
    public FilterRegistrationBean<XssEscapeFilter> xssEscapeFilter() {
        FilterRegistrationBean<XssEscapeFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssEscapeFilter());
        registration.setOrder(1);
        registration.addUrlPatterns("/students/filter-test"); // 이 URL 에만 필터 적용
        return registration;
    }

    // ── 필터 구현 ──────────────────────────────────────────────────────

    /**
     * XssEscapeFilter
     *
     * HttpServletRequest 를 XssEscapeRequestWrapper 로 감싸기
     * → 이후 모든 getParameter() 호출에서 자동 이스케이프 적용
     */
    public static class XssEscapeFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            // 원본 요청을 Wrapper 로 감싸서 전달
            chain.doFilter(new XssEscapeRequestWrapper((HttpServletRequest) request), response);
        }
    }

    // ── 요청 래퍼 구현 ─────────────────────────────────────────────────

    /**
     * XssEscapeRequestWrapper (Decorator 패턴)
     *
     * HttpServletRequestWrapper 를 상속하여 파라미터 조회 메서드를 오버라이드.
     * 부모의 값을 꺼낸 후 HtmlUtils.htmlEscape() 를 적용하여 반환.
     */
    public static class XssEscapeRequestWrapper extends HttpServletRequestWrapper {

        public XssEscapeRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        /**
         * 단일 파라미터 조회 — form input 하나씩 꺼낼 때 호출됨
         * @RequestParam, @ModelAttribute 모두 내부적으로 이 메서드를 사용
         */
        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return escape(value);
        }

        /**
         * 다중 값 파라미터 조회 — checkbox 처럼 같은 이름으로 여러 값이 올 때
         */
        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) return null;
            return Arrays.stream(values)
                    .map(this::escape)
                    .toArray(String[]::new);
        }

        /**
         * 전체 파라미터 맵 조회
         */
        @Override
        public Map<String, String[]> getParameterMap() {
            return super.getParameterMap().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> Arrays.stream(e.getValue())
                                    .map(this::escape)
                                    .toArray(String[]::new)
                    ));
        }

        /**
         * HtmlUtils.htmlEscape() — Spring 내장 HTML 이스케이프 유틸
         *
         * 변환표:
         *   <  →  &lt;
         *   >  →  &gt;
         *   "  →  &quot;
         *   &  →  &amp;
         *   '  →  &#39;
         */
        private String escape(String value) {
            return value != null ? HtmlUtils.htmlEscape(value) : null;
        }
    }
}
