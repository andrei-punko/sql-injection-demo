package com.example.sqlinjection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponse {

    private int count;
    private List<UserDto> users;
}
