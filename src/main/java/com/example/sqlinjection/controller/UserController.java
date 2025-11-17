package com.example.sqlinjection.controller;

import com.example.sqlinjection.dto.ApiResponse;
import com.example.sqlinjection.dto.LoginResponse;
import com.example.sqlinjection.dto.UserDto;
import com.example.sqlinjection.dto.UserSearchResponse;
import com.example.sqlinjection.entity.User;
import com.example.sqlinjection.mapper.UserMapper;
import com.example.sqlinjection.repository.UserRepository;
import com.example.sqlinjection.repository.VulnerableUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller with vulnerable endpoints for SQL injection demonstration
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final VulnerableUserRepository vulnerableUserRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * VULNERABLE ENDPOINT: Search user by username
     * <p>
     * Attack examples:
     * GET /api/users/search?username=admin' OR '1'='1
     * GET /api/users/search?username=admin'--
     * GET /api/users/search?username=' OR '1'='1'--
     */
    @GetMapping("/search")
    public ApiResponse<UserSearchResponse> searchUser(@RequestParam String username) {
        String query = "SELECT * FROM users WHERE username = '" + username + "'";

        List<User> users = vulnerableUserRepository.findByUsernameVulnerable(username);
        List<UserDto> usersDto = userMapper.toDto(users);
        UserSearchResponse response = new UserSearchResponse(usersDto.size(), usersDto);
        return ApiResponse.success(response, null, query);
    }

    /**
     * VULNERABLE ENDPOINT: Search user by email
     * <p>
     * Attack examples:
     * GET /api/users/by-email?email=admin@example.com' OR '1'='1
     */
    @GetMapping("/by-email")
    public ApiResponse<UserSearchResponse> findByEmail(@RequestParam String email) {
        String query = "SELECT * FROM users WHERE email = '" + email + "'";


        List<User> users = vulnerableUserRepository.findByEmailVulnerable(email);
        List<UserDto> usersDto = userMapper.toDto(users);
        UserSearchResponse response = new UserSearchResponse(users.size(), usersDto);
        return ApiResponse.success(response, null, query);
    }

    /**
     * VULNERABLE ENDPOINT: Authentication (search by username and password)
     * <p>
     * Attack examples:
     * POST /api/users/login
     * Body: {"username": "admin", "password": "' OR '1'='1"}
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";


        List<User> users = vulnerableUserRepository.findByUsernameAndPasswordVulnerable(username, password);
        List<UserDto> usersDto = userMapper.toDto(users);

        if (!users.isEmpty()) {
            LoginResponse loginResponse = new LoginResponse(usersDto.get(0), "Authentication successful");
            return ApiResponse.success(loginResponse, "Authentication successful", query);
        }

        LoginResponse loginResponse = new LoginResponse(null, "Invalid credentials");
        return ApiResponse.success(loginResponse, "Invalid credentials", query);
    }

    /**
     * VULNERABLE ENDPOINT: Search users
     * <p>
     * Attack examples:
     * GET /api/users/search-all?term=admin' OR '1'='1'--
     */
    @GetMapping("/search-all")
    public ApiResponse<UserSearchResponse> searchAll(@RequestParam String term) {
        String query = "SELECT * FROM users WHERE username LIKE '%" + term + "%' OR email LIKE '%" + term + "%'";

        List<User> users = vulnerableUserRepository.searchUsersVulnerable(term);
        List<UserDto> usersDto = userMapper.toDto(users);
        UserSearchResponse data = new UserSearchResponse(usersDto.size(), usersDto);
        return ApiResponse.success(data, null, query);
    }

    /**
     * SAFE ENDPOINT: For comparison with vulnerable method
     */
    @GetMapping("/safe/{username}")
    public ApiResponse<UserSearchResponse> findUserSafe(@PathVariable String username) {
        List<User> users = userRepository.findByUsername(username);
        List<UserDto> usersDto = userMapper.toDto(users);
        UserSearchResponse data = new UserSearchResponse(usersDto.size(), usersDto);
        return ApiResponse.success(data, "This method uses parameterized queries and is protected from SQL injection", null);
    }

    /**
     * Get all users (for testing)
     */
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
