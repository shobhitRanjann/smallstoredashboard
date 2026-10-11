package com.tambolaapp.tambola.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetUserNameRequest {
    String username;
    String hostid;
}
