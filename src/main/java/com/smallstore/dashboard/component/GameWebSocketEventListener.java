package com.smallstore.dashboard.component;

import com.smallstore.dashboard.controller.GameSocketController;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class GameWebSocketEventListener {

    private static final Map<String, String> sessionIdToUser = new ConcurrentHashMap<>();
    private static final Map<String, String> sessionIdToRoom = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Call this when user joins the room
    public static void registerSession(String sessionId, String username, String roomId) {
        sessionIdToUser.put(sessionId, username);
        sessionIdToRoom.put(sessionId, roomId);

        GameSocketController.addUserToRoom(roomId, username);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String username = sessionIdToUser.remove(sessionId);
        String roomId = sessionIdToRoom.remove(sessionId);

        if (username != null && roomId != null) {
            GameSocketController.removeUserFromRoom(roomId, username);
            // Broadcast updated online users
            Set<String> usersInRoom = GameSocketController.getUsersInRoom(roomId);
            messagingTemplate.convertAndSend("/topic/game/" + roomId + "/online", usersInRoom);
        }
    }
}
