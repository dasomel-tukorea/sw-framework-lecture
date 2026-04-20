package kr.ac.tukorea.swframework.config;

import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionListener implements HttpSessionListener {

    // 활성화된 세션을 저장할 스레드 안전한 Map
    private static final Map<String, HttpSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        activeSessions.put(session.getId(), session);
        System.out.println("세션 생성됨: " + session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        activeSessions.remove(session.getId());
        System.out.println("세션 소멸됨: " + session.getId());
    }

    // 현재 활성화된 모든 세션을 반환하는 메서드
    public static Map<String, HttpSession> getActiveSessions() {
        return activeSessions;
    }
}