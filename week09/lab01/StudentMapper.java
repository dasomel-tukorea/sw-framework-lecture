// Week 09 — MyBatis CRUD
// StudentMapper.java — MyBatis Mapper 인터페이스
// 프로젝트 경로: src/main/java/kr/ac/tukorea/swframework/mapper/StudentMapper.java
package kr.ac.tukorea.swframework.mapper;

import kr.ac.tukorea.swframework.domain.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 학생 CRUD용 MyBatis Mapper 인터페이스
 *
 * - @Mapper: MyBatis가 런타임에 자동으로 구현체를 생성한다
 * - StudentMapper.xml의 SQL과 1:1로 매핑된다
 * - namespace는 이 인터페이스의 FQCN(Fully Qualified Class Name)과 일치해야 한다
 */
@Mapper
public interface StudentMapper {

    /**
     * 전체 학생 목록 조회
     * @return 학생 목록
     */
    List<Student> findAll();

    /**
     * 학생 상세 조회
     * @param id 학생 번호
     * @return 학생 정보
     */
    Student findById(Long id);

    /**
     * 학생 등록
     * @param student 등록할 학생 정보
     */
    void insert(Student student);

    /**
     * 학생 정보 수정
     * @param student 수정할 학생 정보
     */
    void update(Student student);

    /**
     * 학생 삭제
     * @param id 삭제할 학생 번호
     */
    void delete(Long id);

    // ============================================================
    // Dynamic SQL 심화 — Week 09 심화 실습
    // ============================================================

    /**
     * 이름으로 학생 검색 (<if> + <where>)
     * @param name 검색할 이름 (부분 일치)
     * @return 검색된 학생 목록
     */
    List<Student> findByName(@Param("name") String name);

    /**
     * 검색 유형별 분기 검색 (<choose> / <when> / <otherwise>)
     * @param searchType 검색 유형: "name" | "email" | 그 외(전체)
     * @param keyword    검색어
     * @return 검색된 학생 목록
     */
    List<Student> findBySearchType(@Param("searchType") String searchType,
                                   @Param("keyword") String keyword);

    /**
     * null이 아닌 컬럼만 선택적으로 UPDATE (<set>)
     * @param student 수정할 학생 정보 (null 필드는 수정 제외)
     * @return 수정된 행 수
     */
    int updateSelective(Student student);

    /**
     * 여러 ID로 한 번에 조회 (<foreach> — IN 절)
     * @param ids 조회할 학생 번호 목록
     * @return 학생 목록
     */
    List<Student> findByIds(@Param("ids") List<Long> ids);

    /**
     * 여러 학생 한 번에 INSERT (<foreach> — 배치 INSERT)
     * @param students 등록할 학생 목록
     * @return 삽입된 행 수
     */
    int insertBatch(@Param("list") List<Student> students);

    /**
     * 이름/전공 조건으로 검색 (<trim> — 커스텀 접두사/접미사)
     * @param name  검색할 이름 (null 또는 빈 문자열이면 조건 제외)
     * @param major 검색할 전공 (null 또는 빈 문자열이면 조건 제외)
     * @return 검색된 학생 목록
     */
    List<Student> findByCondition(@Param("name") String name,
                                   @Param("major") String major);
}
