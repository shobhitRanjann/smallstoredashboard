package com.smallstore.dashboard.dto;
import com.smallstore.dashboard.entity.BlogStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class BlogRequest {
    private String title;
    private String content;
    private String createdBy;
    private Set<String> tags;
    private List<BlogImageDTO> blogImages;
    private BlogStatus status;

    // Getters and Setters
}
