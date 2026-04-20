package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.config.SessionListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SessionController {

    @GetMapping("/active-sessions")
    public List<String> getActiveSessions() {
        Map<String, HttpSession> sessions = SessionListener.getActiveSessions();
        List<String> sessionInfoList = new ArrayList<>();

        for (Map.Entry<String, HttpSession> entry : sessions.entrySet()) {
            String sessionId = entry.getKey();
            HttpSession session = entry.getValue();

            // 세션에 저장된 특정 속성(예: 로그인 유저 정보)을 꺼내볼 수도 있습니다.
            Object loginUser = session.getAttribute("loginUser");

            String info = "Session ID: " + sessionId +
                    ", Creation Time: " + session.getCreationTime() +
                    ", Logged In User: " + (loginUser != null ? loginUser.toString() : "None");

            sessionInfoList.add(info);
        }

        return sessionInfoList; // 브라우저에서 JSON 형태로 활성 세션 목록 확인 가능
    }
}