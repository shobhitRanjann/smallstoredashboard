package com.smallstore.dashboard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference
    private Set<Blog> blogs;

    public Tag(String tagName) {
        this.name = tagName;
    }

    public Tag() {
    }
// Constructors, Getters, and Setters
}
