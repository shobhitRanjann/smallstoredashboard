package com.smallstore.dashboard.component;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class WebSocketEventListener {

    private static final Map<String, String> sessionIdToUser = new ConcurrentHashMap<>();
    private static final Map<String, String> sessionIdToIp = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Same shared set of users from your ChatController
    public static final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        // Optional: Do something on connect
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String username = sessionIdToUser.remove(sessionId);
        String ip = sessionIdToIp.remove(sessionId);
        if (username != null) {
            onlineUsers.remove(username);
            messagingTemplate.convertAndSend("/topic/global/online", onlineUsers);
            log.info("User '{}' disconnected from IP {}", username, ip);
        }
    }

    public static void registerSession(String sessionId, String username, String ipAddress) {
        sessionIdToUser.put(sessionId, username);
        sessionIdToIp.put(sessionId, ipAddress);
        onlineUsers.add(username);
    }
}

