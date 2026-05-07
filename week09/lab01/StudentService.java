// Week 09 — MyBatis CRUD
// StudentService.java — 학생 서비스 클래스
// 프로젝트 경로: src/main/java/kr/ac/tukorea/swframework/service/StudentService.java
package kr.ac.tukorea.swframework.service;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.mapper.StudentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 학생 비즈니스 로직 서비스 클래스
 *
 * - @Service: Spring이 이 클래스를 서비스 빈으로 등록한다 (Week 04 DI)
 * - @Transactional: 메서드 실행 중 예외 발생 시 자동 롤백한다
 * - Controller와 Mapper 사이의 중간 역할
 *
 * ExecutionTimeAspect(Week 05)에 의해 모든 메서드의 실행 시간이 자동 측정된다.
 */
@Service
@Transactional
public class StudentService {

    private final StudentMapper studentMapper;

    // 생성자 주입 (DI) — @Autowired 생략 가능 (생성자가 1개일 때)
    public StudentService(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    /**
     * 전체 학생 목록 조회
     */
    @Transactional(readOnly = true)  // 읽기 전용 트랜잭션 (성능 최적화)
    public List<Student> findAll() {
        return studentMapper.findAll();
    }

    /**
     * 학생 상세 조회
     */
    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return studentMapper.findById(id);
    }

    /**
     * 학생 등록
     */
    public void save(Student student) {
        studentMapper.insert(student);
    }

    /**
     * 학생 정보 수정
     */
    public void update(Student student) {
        studentMapper.update(student);
    }

    /**
     * 학생 삭제
     */
    public void delete(Long id) {
        studentMapper.delete(id);
    }
}
