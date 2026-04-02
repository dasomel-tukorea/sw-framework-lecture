// src/main/java/kr/ac/tukorea/swframework/component/PrototypeBean.java
package kr.ac.tukorea.swframework.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Prototype 스코프 빈
 * - getBean() 호출마다 새 인스턴스가 생성됨
 * - 생성 시 UUID를 할당하여 인스턴스 구별 가능
 * - ⚠️ Prototype 빈은 @PreDestroy가 자동 호출되지 않음
 *      (컨테이너가 생성·주입까지만 관리, 소멸 책임은 클라이언트에 있음)
 *
 * 확인 방법:
 *   http://localhost:8080/scope-test 접속 후 콘솔 로그 확인
 *   → PrototypeBean 생성자가 getBean() 호출 때마다 호출됨
 *   → p1 == p2: false (다른 인스턴스)
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // Prototype 스코프: 요청마다 새 인스턴스
public class PrototypeBean {

    // 생성 시 UUID 할당 — 인스턴스마다 고유한 값
    private final String instanceId = UUID.randomUUID().toString().substring(0, 8);

    public PrototypeBean() {
        log.info("[Prototype] 생성자 호출 — 인스턴스 ID: {}, 객체: {}", instanceId, this);
    }

    public String getInstanceId() {
        return instanceId;
    }
}
