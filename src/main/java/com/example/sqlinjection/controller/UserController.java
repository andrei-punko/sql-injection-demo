package com.example.sqlinjection.controller;

import com.example.sqlinjection.dto.ApiResponse;
import com.example.sqlinjection.dto.LoginResponse;
import com.example.sqlinjection.dto.UserSearchResponse;
import com.example.sqlinjection.entity.User;
import com.example.sqlinjection.repository.UserRepository;
import com.example.sqlinjection.repository.VulnerableUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    /**
     * VULNERABLE ENDPOINT: Search user by username
     * <p>
     * Attack examples:
     * GET /api/users/search?username=admin' OR '1'='1
     * GET /api/users/search?username=admin'--
     * GET /api/users/search?username=' OR '1'='1'--
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserSearchResponse>> searchUser(@RequestParam String username) {
        String query = "SELECT * FROM users WHERE username = '" + username + "'";

        try {
            List<User> users = vulnerableUserRepository.findByUsernameVulnerable(username);
            UserSearchResponse data = new UserSearchResponse(users.size(), users);
            return ResponseEntity.ok(ApiResponse.success(data, null, query));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), query));
        }
    }

    /**
     * VULNERABLE ENDPOINT: Search user by email
     * <p>
     * Attack examples:
     * GET /api/users/by-email?email=admin@example.com' OR '1'='1
     */
    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<UserSearchResponse>> findByEmail(@RequestParam String email) {
        String query = "SELECT * FROM users WHERE email = '" + email + "'";

        try {
            List<User> users = vulnerableUserRepository.findByEmailVulnerable(email);
            UserSearchResponse data = new UserSearchResponse(users.size(), users);
            return ResponseEntity.ok(ApiResponse.success(data, null, query));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), query));
        }
    }

    /**
     * VULNERABLE ENDPOINT: Authentication (search by username and password)
     * <p>
     * Attack examples:
     * POST /api/users/login
     * Body: {"username": "admin", "password": "' OR '1'='1"}
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        try {
            List<User> users = vulnerableUserRepository.findByUsernameAndPasswordVulnerable(username, password);

            if (!users.isEmpty()) {
                LoginResponse loginResponse = new LoginResponse(users.get(0), "Authentication successful");
                return ResponseEntity.ok(ApiResponse.success(loginResponse, "Authentication successful", query));
            } else {
                LoginResponse loginResponse = new LoginResponse(null, "Invalid credentials");
                return ResponseEntity.ok(ApiResponse.success(loginResponse, "Invalid credentials", query));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), query));
        }
    }

    /**
     * VULNERABLE ENDPOINT: Search users
     * <p>
     * Attack examples:
     * GET /api/users/search-all?term=admin' OR '1'='1'--
     */
    @GetMapping("/search-all")
    public ResponseEntity<ApiResponse<UserSearchResponse>> searchAll(@RequestParam String term) {
        String query = "SELECT * FROM users WHERE username LIKE '%" + term + "%' OR email LIKE '%" + term + "%'";
        try {
            List<User> users = vulnerableUserRepository.searchUsersVulnerable(term);
            UserSearchResponse data = new UserSearchResponse(users.size(), users);
            return ResponseEntity.ok(ApiResponse.success(data, null, query));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), query));
        }
    }

    /**
     * SAFE ENDPOINT: For comparison with vulnerable method
     */
    @GetMapping("/safe/{username}")
    public ResponseEntity<ApiResponse<UserSearchResponse>> findUserSafe(@PathVariable String username) {
        try {
            List<User> users = userRepository.findByUsername(username);
            UserSearchResponse data = new UserSearchResponse(users.size(), users);
            return ResponseEntity.ok(ApiResponse.success(data, 
                    "This method uses parameterized queries and is protected from SQL injection", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get all users (for testing)
     */
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
