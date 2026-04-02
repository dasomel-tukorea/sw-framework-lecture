// src/main/java/kr/ac/tukorea/swframework/controller/AuditTestController.java
package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.service.StudentInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 보안 감사 AOP 테스트 컨트롤러
 * - @RestController: @Controller + @ResponseBody 조합 — JSON/텍스트를 바로 반환
 *
 * 테스트 URL:
 *   http://localhost:8080/audit/student?id=1
 *   http://localhost:8080/audit/grade?id=42&subject=SW프레임워크&grade=95
 *
 * 확인 사항:
 *   URL 호출 후 콘솔에서 AuditAspect가 자동으로 기록한 감사 로그를 확인한다.
 *   StudentInfoService 코드에는 감사 로그 코드가 한 줄도 없음을 확인한다.
 */
@RestController
public class AuditTestController {

    private final StudentInfoService studentInfoService;

    // 생성자 주입 (Spring 권장 방식)
    public AuditTestController(StudentInfoService studentInfoService) {
        this.studentInfoService = studentInfoService;
    }

    /**
     * 학생 정보 조회 엔드포인트
     *
     * @param id 학생 ID (기본값: 1)
     */
    @GetMapping("/audit/student")
    public String getStudent(@RequestParam(defaultValue = "1") Long id) {
        return studentInfoService.getStudentInfo(id);
    }

    /**
     * 학생 성적 수정 엔드포인트 (민감 작업 — 감사 로그 기록 대상)
     *
     * @param id      학생 ID (기본값: 1)
     * @param subject 과목명 (기본값: SW프레임워크)
     * @param grade   점수 (기본값: 95)
     */
    @GetMapping("/audit/grade")
    public String updateGrade(
            @RequestParam(defaultValue = "1") Long id,
            @RequestParam(defaultValue = "SW프레임워크") String subject,
            @RequestParam(defaultValue = "95") int grade) {
        return studentInfoService.updateGrade(id, subject, grade);
    }
}
