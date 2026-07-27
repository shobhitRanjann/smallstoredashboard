package com.smallstore.dashboard.controller;

import com.smallstore.dashboard.component.GameWebSocketEventListener;
import com.smallstore.dashboard.dto.ChatMessage;
import com.smallstore.dashboard.dto.ReadReceipt;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "https://smallstore.in", allowedHeaders = "*", allowCredentials = "true")
public class GameSocketController {

    // Track online users per game room
    private static final Map<String, Set<String>> roomUsers = new ConcurrentHashMap<>();

    @MessageMapping("/game/{roomId}/send")
    @SendTo("/topic/game/{roomId}/messages")
    public ChatMessage send(@DestinationVariable String roomId, ChatMessage message) {
        message.setMessageId(UUID.randomUUID().toString());
        message.setTime(LocalDateTime.now().toString());
        message.setReaders(new ArrayList<>());
        return message;
    }

    @MessageMapping("/game/{roomId}/typing")
    @SendTo("/topic/game/{roomId}/typing")
    public Map<String, String> typing(@DestinationVariable String roomId, ChatMessage message) {
        return Map.of("sender", message.getSender());
    }

    @MessageMapping("/game/{roomId}/join")
    @SendTo("/topic/game/{roomId}/online")
    public Set<String> join(@DestinationVariable String roomId, @Payload String username,  SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        GameWebSocketEventListener.registerSession(sessionId, username, roomId);
        return GameSocketController.getUsersInRoom(roomId);
    }

    @MessageMapping("/game/{roomId}/leave")
    @SendTo("/topic/game/{roomId}/online")
    public Set<String> leave(@DestinationVariable String roomId, String username) {
        roomUsers.computeIfPresent(roomId, (k, set) -> { set.remove(username); return set; });
        return roomUsers.getOrDefault(roomId, Set.of());
    }

    @MessageMapping("/game/{roomId}/read")
    @SendTo("/topic/game/{roomId}/read")
    public ReadReceipt handleRead(@DestinationVariable String roomId, ReadReceipt receipt) {
        return receipt;
    }

    public static void addUserToRoom(String roomId, String username) {
        roomUsers.putIfAbsent(roomId, ConcurrentHashMap.newKeySet());
        roomUsers.get(roomId).add(username);
    }

    public static void removeUserFromRoom(String roomId, String username) {
        roomUsers.computeIfPresent(roomId, (k, set) -> {
            set.remove(username);
            return set;
        });
    }

    public static Set<String> getUsersInRoom(String roomId) {
        return roomUsers.getOrDefault(roomId, Set.of());
    }
}
