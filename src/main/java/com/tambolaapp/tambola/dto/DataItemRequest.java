package com.tambolaapp.tambola.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataItemRequest {
    boolean checked;
    int key;
    String userid;
    int value;
}
