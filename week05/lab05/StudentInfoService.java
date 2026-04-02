// src/main/java/kr/ac/tukorea/swframework/service/StudentInfoService.java
package kr.ac.tukorea.swframework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 학생 정보 서비스 (보안 감사 AOP 테스트용)
 *
 * 핵심 포인트:
 *   이 클래스에는 감사 로그 코드가 한 줄도 없다.
 *   AuditAspect가 이 클래스의 모든 메서드 호출을 자동으로 감사 로그로 기록한다.
 *   → "비즈니스 로직과 감사 코드의 완전한 분리" 실증
 *
 * 테스트 URL:
 *   http://localhost:8080/audit/student?id=1
 *   http://localhost:8080/audit/grade?id=42&subject=SW프레임워크&grade=95
 */
@Service
@Slf4j
public class StudentInfoService {

    /**
     * 학생 정보 조회 (감사 대상 — 민감 데이터 접근)
     *
     * @param studentId 조회할 학생 ID
     * @return 학생 정보 문자열
     */
    public String getStudentInfo(Long studentId) {
        log.info("학생 정보 조회 비즈니스 로직 실행");
        return "학생 ID: " + studentId + ", 이름: 홍길동, 학과: IT경영";
    }

    /**
     * 학생 성적 수정 (감사 대상 — 민감 작업)
     *
     * @param studentId 학생 ID
     * @param subject   과목명
     * @param grade     점수
     * @return 처리 결과 문자열
     */
    public String updateGrade(Long studentId, String subject, int grade) {
        log.info("성적 수정 비즈니스 로직 실행");
        return "학생 " + studentId + "의 " + subject + " 성적을 " + grade + "점으로 수정";
    }
}
