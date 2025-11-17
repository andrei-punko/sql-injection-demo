package com.example.sqlinjection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String error;
    private String query;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, null, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, null);
    }

    public static <T> ApiResponse<T> success(T data, String message, String query) {
        return new ApiResponse<>(true, message, data, null, query);
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, null, null, error, null);
    }

    public static <T> ApiResponse<T> error(String error, String query) {
        return new ApiResponse<>(false, null, null, error, query);
    }
}
