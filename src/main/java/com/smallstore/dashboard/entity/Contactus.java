package com.smallstore.dashboard.entity;

import jakarta.persistence.*;
import lombok.Builder;


@Entity
@Table(name = "sm_contactus")
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

