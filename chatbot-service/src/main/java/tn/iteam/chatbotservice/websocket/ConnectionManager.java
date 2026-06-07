package tn.iteam.chatbotservice.websocket;

import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class ConnectionManager {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    public List<WebSocketSession> getSessions() {
        return new CopyOnWriteArrayList<>(sessions);
    }

    public int getActiveConnections() {
        return sessions.size();
    }

}

