package com.smallstore.dashboard.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;


@Entity
@Table(name = "sm_contactus")
@Getter
@Builder
public class Contactus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String emailid;
    String phone;
    String projectType;
    String comment;
}

