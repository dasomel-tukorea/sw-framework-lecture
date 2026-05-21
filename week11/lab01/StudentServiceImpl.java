// [복사 위치] src/main/java/kr/ac/tukorea/swframework/service/StudentServiceImpl.java
// [작업] Week 11 lab01 — StudentService 인터페이스 구현체
//        - 생성자 주입 (단일 생성자 → @Autowired 생략)
//        - 클래스 전체 @Transactional, 조회는 readOnly=true 로 최적화
//        - Lombok 미사용 — DI 보일러플레이트를 수강생이 직접 확인
package kr.ac.tukorea.swframework.service;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.dto.PageDTO;
import kr.ac.tukorea.swframework.mapper.StudentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 학생 비즈니스 로직 서비스 구현체 — Week 11
 *
 * lab01 의 페이징/검색 흐름:
 *   1) Controller 가 PageDTO 를 @ModelAttribute 로 받음
 *   2) getTotalCount(pageDTO) 로 전체 건수 채움
 *   3) getListWithPaging(pageDTO) 로 LIMIT/OFFSET 결과 조회
 *
 * lab02 에서 정렬, lab03 에서 블록 페이징, lab04 에서 파일 첨부가 본 구현체 위에 확장된다.
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getListWithPaging(PageDTO pageDTO) {
        return studentMapper.findAllWithPaging(pageDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalCount(PageDTO pageDTO) {
        return studentMapper.countAll(pageDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Student getDetail(Long id) {
        return studentMapper.findById(id);
    }

    @Override
    public void create(Student student) {
        studentMapper.insert(student);
    }

    @Override
    public void modify(Student student) {
        studentMapper.update(student);
    }

    @Override
    public void remove(Long id) {
        studentMapper.delete(id);
    }
}
