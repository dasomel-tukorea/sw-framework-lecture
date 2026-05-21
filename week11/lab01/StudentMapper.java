// [복사 위치] src/main/java/kr/ac/tukorea/swframework/mapper/StudentMapper.java
// [작업] Week 09 의 기본 CRUD Mapper 에 페이징·검색용 메서드 2개를 추가
//        - findAllWithPaging : LIMIT/OFFSET + 검색 조건이 적용된 목록 조회
//        - countAll          : 페이지 수 계산을 위한 검색 조건부 COUNT(*)
//        namespace 는 본 인터페이스의 FQCN 과 정확히 일치해야 한다.
package kr.ac.tukorea.swframework.mapper;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.dto.PageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 학생 CRUD + 페이징/검색 Mapper — Week 11
 *
 * 기본 CRUD 는 W09 lab02 에서 정의한 시그니처를 그대로 유지하고,
 * 페이징과 카운트 두 메서드만 새로 추가한다. (XML 도 동일 namespace 안에서 확장)
 */
@Mapper
public interface StudentMapper {

    // === Week 11 lab01 추가 ===

    /**
     * 페이징 + 검색 조건이 반영된 학생 목록 조회.
     * MyBatis 는 PageDTO 의 getter (getOffset/getSize/getKeyword 등)를
     * #{offset} / #{size} / #{keyword} 로 그대로 사용 가능하다.
     */
    List<Student> findAllWithPaging(PageDTO pageDTO);

    /**
     * 검색 조건이 반영된 전체 학생 수 — PageDTO.totalPages 계산용.
     */
    int countAll(PageDTO pageDTO);

    // === Week 09 기본 CRUD (W09 → W11 그대로 유지) ===

    Student findById(Long id);

    void insert(Student student);

    void update(Student student);

    void delete(Long id);
}
