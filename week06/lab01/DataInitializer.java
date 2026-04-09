// [복사 위치] src/main/java/kr/ac/tukorea/swframework/DataInitializer.java
// [작업] 기존 파일을 이 파일로 교체 (Student 생성자 변경 반영)
package kr.ac.tukorea.swframework;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;

    public DataInitializer(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) {
        if (studentRepository.count() == 0) {
            // Student(이름, 학번, 이메일) — week06 필드 변경 반영
            studentRepository.save(new Student("홍길동", "202300001", "hong@tukorea.ac.kr"));
            studentRepository.save(new Student("김영희", "202300002", "kim@tukorea.ac.kr"));
            studentRepository.save(new Student("이철수", "202300003", "lee@tukorea.ac.kr"));
        }
        System.out.println("=== 초기 데이터 확인 ===");
        studentRepository.findAll().forEach(System.out::println);
    }
}
