// [복사 위치] src/main/java/kr/ac/tukorea/swframework/service/StudentService.java
// [작업] Week 11 lab01 — 페이징/검색 메서드 추가
//        - Controller 가 인터페이스에만 의존하도록 추상화 (DI 결합도 ↓)
//        - 구현체는 StudentServiceImpl
package kr.ac.tukorea.swframework.service;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.dto.PageDTO;

import java.util.List;

/**
 * 학생 도메인 서비스 계층 추상화 — Week 11
 *
 * lab01 에서 페이징/검색 두 메서드를 추가하고, 나머지는 W09 기본 CRUD 시그니처를 유지한다.
 *
 * 메서드 명명 규칙:
 *   - 조회 계열은 get*  (getListWithPaging, getTotalCount, getDetail)
 *   - 변경 계열은 create / modify / remove
 */
public interface StudentService {

    /** 페이징 + 검색이 적용된 학생 목록 조회 */
    List<Student> getListWithPaging(PageDTO pageDTO);

    /** 검색 조건이 반영된 전체 건수 — 페이지 수 계산용 */
    int getTotalCount(PageDTO pageDTO);

    /** 학생 상세 조회 */
    Student getDetail(Long id);

    /** 학생 등록 — useGeneratedKeys 로 student.id 가 채워진다 */
    void create(Student student);

    /** 학생 수정 (전체 필드) */
    void modify(Student student);

    /** 학생 삭제 */
    void remove(Long id);
}
