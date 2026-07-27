package com.smallstore.dashboard.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Getter
@Setter
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String blogTitle;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String blogContent;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BlogImage> blogImages;

    @ManyToMany
    @JoinTable(
            name = "blog_tags",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonManagedReference
    private Set<Tag> tags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlogStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime updateDate;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(nullable = false)
    private int likeCount = 0;

    // Constructors
    public Blog() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.status = BlogStatus.DRAFT;
    }

    // Auto-generate slug from title
    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
        this.slug = blogTitle.toLowerCase().replaceAll("[^a-z0-9]+", "-");
    }

    @PreUpdate
    public void setLastUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    // Increment view count
    public void incrementViewCount() {
        this.viewCount++;
    }
    public void like() {
        this.likeCount++;
    }

}

