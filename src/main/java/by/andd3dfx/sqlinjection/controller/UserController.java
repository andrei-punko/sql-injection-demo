package by.andd3dfx.sqlinjection.controller;

import by.andd3dfx.sqlinjection.dto.ApiResponse;
import by.andd3dfx.sqlinjection.dto.UserDto;
import by.andd3dfx.sqlinjection.entity.User;
import by.andd3dfx.sqlinjection.mapper.UserMapper;
import by.andd3dfx.sqlinjection.repository.UserRepository;
import by.andd3dfx.sqlinjection.repository.VulnerableUserRepository;
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
    public ApiResponse<List<UserDto>> findByUsername(@RequestParam String username) {
        String query = "SELECT * FROM users WHERE username = '" + username + "'";

        List<User> users = vulnerableUserRepository.findByUsernameVulnerable(username);
        List<UserDto> usersDto = userMapper.toDto(users);
        return new ApiResponse<>(usersDto, query);
    }

    /**
     * VULNERABLE ENDPOINT: Search user by email
     * <p>
     * Attack examples:
     * GET /api/users/by-email?email=admin@example.com' OR '1'='1
     */
    @GetMapping("/by-email")
    public ApiResponse<List<UserDto>> findByEmail(@RequestParam String email) {
        String query = "SELECT * FROM users WHERE email = '" + email + "'";

        List<User> users = vulnerableUserRepository.findByEmailVulnerable(email);
        List<UserDto> usersDto = userMapper.toDto(users);
        return new ApiResponse<>(usersDto, query);
    }

    /**
     * VULNERABLE ENDPOINT: Authentication (search by username and password)
     * <p>
     * Attack examples:
     * POST /api/users/login
     * Body: {"username": "admin", "password": "' OR '1'='1"}
     */
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        List<User> users = vulnerableUserRepository.findByUsernameAndPasswordVulnerable(username, password);
        List<UserDto> usersDto = userMapper.toDto(users);

        var status = usersDto.isEmpty() ? "FAIL" : "SUCCESS";
        return new ApiResponse<>(status, query);
    }

    /**
     * VULNERABLE ENDPOINT: Search users
     * <p>
     * Attack examples:
     * GET /api/users/search-all?term=admin' OR '1'='1'--
     */
    @GetMapping("/search-all")
    public ApiResponse<List<UserDto>> searchAll(@RequestParam String term) {
        String query = "SELECT * FROM users WHERE username LIKE '%" + term + "%' OR email LIKE '%" + term + "%'";

        List<User> users = vulnerableUserRepository.searchUsersVulnerable(term);
        List<UserDto> usersDto = userMapper.toDto(users);
        return new ApiResponse<>(usersDto, query);
    }

    /**
     * SAFE ENDPOINT: For comparison with vulnerable method
     */
    @GetMapping("/safe/{username}")
    public List<UserDto> findUserSafe(@PathVariable String username) {
        List<User> users = userRepository.findByUsername(username);
        return userMapper.toDto(users);
    }

    /**
     * Get all users (for testing)
     */
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
