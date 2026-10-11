package com.tambolaapp.tambola.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tk_playerdetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String hostid;
}
