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
@Table(name = "tk_playervalues")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerGeneratedValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
     private int key;
     private int value;
     private boolean checked;
     private long userid;
}
