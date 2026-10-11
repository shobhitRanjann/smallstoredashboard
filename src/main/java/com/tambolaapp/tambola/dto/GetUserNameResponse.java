package com.tambolaapp.tambola.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GetUserNameResponse {
     Long id;
     String username;
     String hostid;
}
