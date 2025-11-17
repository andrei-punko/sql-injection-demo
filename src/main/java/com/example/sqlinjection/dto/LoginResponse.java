package com.example.sqlinjection.dto;

import com.example.sqlinjection.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private User user;
    private String message;
}
