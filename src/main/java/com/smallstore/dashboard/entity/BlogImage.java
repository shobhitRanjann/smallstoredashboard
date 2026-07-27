package com.smallstore.dashboard.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BlogImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private int imageOrder;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    @JsonBackReference
    private Blog blog;
}

