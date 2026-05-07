# Lab 03 — Dynamic SQL 심화

> Lab 01의 정적 CRUD를 확장 — 런타임 SQL 조립 5종 마스터

## 파일

| 파일 | 적용 위치 | 설명 |
|---|---|---|
| `StudentMapper_Dynamic.xml` | `lab01/StudentMapper.xml`에 **추가** | `<sql>`/`<include>` 재사용 + 5종 동적 태그 |
| `SearchController.java` | `controller/` 패키지 | 검색·다건 조회 엔드포인트 |

## 추가되는 Mapper Interface 메서드

`lab01/StudentMapper.java`에 이미 정의됨 — 본 lab의 XML이 매핑된다:

```java
List<Student> findByName(@Param("name") String name);
List<Student> findBySearchType(@Param("searchType") String t, @Param("keyword") String k);
int           updateSelective(Student student);
List<Student> findByIds(@Param("ids") List<Long> ids);
int           insertBatch(@Param("list") List<Student> students);
List<Student> findByCondition(@Param("name") String n, @Param("major") String m);
```

## 5종 결정 트리

```
                    조건이 단 하나?
                    │
             ┌──────┴──────┐
             YES            NO
             │              │
           <if>          분기 N개?
                            │
                     ┌──────┴──────┐
                     YES (택1)      NO
                     │              │
                  <choose>     UPDATE 부분 컬럼?
                                    │
                             ┌──────┴──────┐
                             YES            NO
                             │              │
                           <set>      IN/배치?
                                            │
                                     ┌──────┴──────┐
                                     YES            NO (특수)
                                     │              │
                                <foreach>        <trim>
```

## 실습 단계

1. `lab03/StudentMapper_Dynamic.xml`의 `<sql>` 블록과 6개 SQL을 `lab01/StudentMapper.xml`에 **추가** (namespace 동일)
2. `lab03/SearchController.java`를 controller 패키지에 복사
3. `./gradlew bootRun` 후 테스트 (week09.http의 Lab02 섹션 참고)

## 확인 포인트

- [ ] `GET /students/search?type=name&keyword=홍` → '홍길동'만 조회
- [ ] `GET /students/search?type=email&keyword=tukorea` → 전체(이메일 부분일치) 조회
- [ ] `GET /students/search` → 전체 목록
- [ ] `GET /students/by-ids?ids=1,2,3` → 3건 (또는 존재하는 만큼)
- [ ] SQL 로그에서 동적으로 조립된 WHERE/SET 절 확인

## 실무 팁

- **`<where>`/`<set>` 우선 사용**: 대부분 두 태그면 충분
- **`<trim>`은 특수 사례**: SQL 키워드가 WHERE/SET이 아닐 때 (예: `INSERT INTO t (a,b,c)`의 컬럼 목록 조립)
- **`<foreach>` 배치 INSERT**: 1000건 이상은 배치로 — 단건 N회는 트랜잭션 부담
- **`<sql>` + `<include>`**: 검색 조건이 list/count 양쪽에 쓰일 때 재사용 (sw-framework-demo의 `BoardMapper.xml` 패턴 참고)
