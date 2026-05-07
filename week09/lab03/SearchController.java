// Week 09 — Lab 02 Dynamic SQL 검색 컨트롤러
// 프로젝트 경로: src/main/java/kr/ac/tukorea/swframework/controller/SearchController.java
package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.mapper.StudentMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 학생 검색 컨트롤러 — Dynamic SQL 활용 예시
 *
 * <p>학습 포인트
 * <ul>
 *   <li>{@code @RequestParam(required=false)} — 빈값일 때 동적 조건 생략</li>
 *   <li>{@code <if>} + {@code <where>} 조합으로 조건 누락 시 WHERE 절 자동 제거</li>
 *   <li>{@code <choose>}로 검색 유형 분기</li>
 * </ul>
 *
 * 테스트 시나리오
 * <pre>
 *   GET /students/search?type=name&keyword=홍   → 이름 검색
 *   GET /students/search?type=email&keyword=tu  → 이메일 검색
 *   GET /students/search                          → 전체 목록
 * </pre>
 */
@Controller
@RequestMapping("/students")
public class SearchController {

    private final StudentMapper studentMapper;

    public SearchController(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    /**
     * 검색 유형(type)과 검색어(keyword)에 따라 동적으로 분기.
     *
     * @param type    name | email | (그 외 → 전체)
     * @param keyword 검색어 (빈값이면 전체 조회)
     * @param model   View 데이터
     * @return student/list.html
     */
    @GetMapping("/search")
    public String search(@RequestParam(required = false, defaultValue = "") String type,
                         @RequestParam(required = false, defaultValue = "") String keyword,
                         Model model) {

        List<Student> students = studentMapper.findBySearchType(type, keyword);

        model.addAttribute("students", students);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        return "student/list";
    }

    /**
     * <foreach> IN 절 예시 — 여러 ID 한 번에 조회
     *
     * 사용 예: GET /students/by-ids?ids=1,2,3
     */
    @GetMapping("/by-ids")
    public String findByIds(@RequestParam List<Long> ids, Model model) {
        List<Student> students = studentMapper.findByIds(ids);
        model.addAttribute("students", students);
        return "student/list";
    }
}
