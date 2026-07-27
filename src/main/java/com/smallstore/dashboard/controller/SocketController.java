package com.smallstore.dashboard.controller;

import com.smallstore.dashboard.component.WebSocketEventListener;
import com.smallstore.dashboard.dto.ChatMessage;
import com.smallstore.dashboard.dto.ReadReceipt;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Log4j2
@CrossOrigin(origins = "https://smallstore.in", allowedHeaders = "*", allowCredentials = "true")
public class SocketController {

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    @MessageMapping("/global/send")
    @SendTo("/topic/global/messages")
    public ChatMessage send(ChatMessage message) {
        log.info("message sending start :: "+message);
        message.setMessageId(UUID.randomUUID().toString()); // ✅ Assign unique ID
        message.setTime(LocalDateTime.now().toString()); // ✅ Use full timestamp
        message.setReaders(new ArrayList<>()); // ✅ Initially no readers
        log.info("message sending :: "+message);
        return message;
    }

    @MessageMapping("/global/typing")
    @SendTo("/topic/global/typing")
    public Map<String, String> typing(ChatMessage message) {
        return Map.of("sender", message.getSender());
    }

    @MessageMapping("/global/join")
    @SendTo("/topic/global/online")
    public Set<String> join(@Payload String username, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String ip = Optional.ofNullable(headerAccessor.getSessionAttributes())
                .map(attr -> attr.get("ipAddress"))
                .map(Object::toString)
                .orElse("unknown");
        WebSocketEventListener.registerSession(sessionId, username, ip);
        log.info("User '{}' connected from IP: {}", username, ip);
        return WebSocketEventListener.onlineUsers;
    }


    @MessageMapping("/global/leave")
    @SendTo("/topic/global/online")
    public Set<String> leave(String username) {
        onlineUsers.remove(username);
        return onlineUsers;
    }

    @MessageMapping("/global/read")
    @SendTo("/topic/global/read")
    public ReadReceipt handleRead(ReadReceipt receipt) {
        log.info(receipt.getReader() + " read message " + receipt.getMessageId());
        // You can persist this info if needed later
        return receipt;
    }
}
