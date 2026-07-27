package com.smallstore.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String messageId;
    private String sender;
    private String content;
    private String time;
    private List<String> readers = new ArrayList<>();
    // getters and setters

    // Add these fields
    private String messageType;  // "TEXT" or "IMAGE"
    private String imageData;    // Base64 payload
    private String imageName;    // Original filename
}

