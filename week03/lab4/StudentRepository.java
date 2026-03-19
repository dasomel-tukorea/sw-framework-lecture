// src/main/java/kr/ac/tukorea/swframework/repository/StudentRepository.java
package kr.ac.tukorea.swframework.repository;

import kr.ac.tukorea.swframework.domain.Student;
import org.springframework.data.repository.ListCrudRepository;

// ListCrudRepository를 상속하면 CRUD 메서드가 자동 생성된다!
// <Student, Long> → Student 도메인, Long 타입의 기본키
public interface StudentRepository extends ListCrudRepository<Student, Long> {
    // findAll(), findById(), save(), deleteById() 등이 이미 존재
}
