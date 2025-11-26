package by.andd3dfx.sqlinjection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@WebAppConfiguration
class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @SneakyThrows
    void findByUsername() {
        mockMvc.perform(get("/api/users/by-username").param("username", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(4))
                .andExpect(jsonPath("$.data[0].username").value("john"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist())
                .andExpect(jsonPath("$.data[0].email").value("john@example.com"))
                .andExpect(jsonPath("$.data[0].role").value("USER"));
    }

    @Test
    @SneakyThrows
    void findByUsername_OR_1eq1() {
        mockMvc.perform(get("/api/users/by-username").param("username", "admin' OR '1'='1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5));

        mockMvc.perform(get("/api/users/by-username").param("username", "' OR '1'='1'--"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5));
    }

    @Test
    @SneakyThrows
    void findByUsername_UNION() {
        mockMvc.perform(get("/api/users/by-username").param("username", "' UNION SELECT * FROM users--"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].username").value("admin"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist());

        mockMvc.perform(get("/api/users/by-username").param("username",
                        "' UNION SELECT u.ID, u.USERNAME, u.PASSWORD as EMAIL, u.PASSWORD, u.ROLE FROM users u--"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].username").value("ADMIN"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist())
                .andExpect(jsonPath("$.data[0].email").value("admin"))
                .andExpect(jsonPath("$.data[0].role").value("admin123"));
    }

    @Test
    @SneakyThrows
    void findByEmail() {
        mockMvc.perform(get("/api/users/by-email").param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(4))
                .andExpect(jsonPath("$.data[0].username").value("john"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist())
                .andExpect(jsonPath("$.data[0].email").value("john@example.com"))
                .andExpect(jsonPath("$.data[0].role").value("USER"));
    }

    @Test
    @SneakyThrows
    void findByEmail_OR_1eq1() {
        mockMvc.perform(get("/api/users/by-email").param("email", "admin@example.com' OR '1'='1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5));

        mockMvc.perform(get("/api/users/by-email").param("email", "' OR '1'='1'--"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5));
    }

    @Test
    @SneakyThrows
    void login() {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user1\", \"password\": \"password1\"}")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));
    }

    @Test
    @SneakyThrows
    void login_OR_1eq1() {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"user1\", \"password\": \"' OR '1'='1\"}")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"any' OR '1'='1'--\", \"password\": \"Karapuzik\"}")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));
    }

    @Test
    @SneakyThrows
    void findByTerm() {
        mockMvc.perform(get("/api/users/by-term").param("term", "ser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(2))
                .andExpect(jsonPath("$.data[0].username").value("user1"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist())
                .andExpect(jsonPath("$.data[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$.data[0].role").value("USER"))
                .andExpect(jsonPath("$.data[1].id").value(3))
                .andExpect(jsonPath("$.data[1].username").value("user2"))
                .andExpect(jsonPath("$.data[1].password").doesNotExist())
                .andExpect(jsonPath("$.data[1].email").value("user2@example.com"))
                .andExpect(jsonPath("$.data[1].role").value("USER"));
    }

    @Test
    @SneakyThrows
    void findByTerm_OR_1eq1() {
        mockMvc.perform(get("/api/users/by-term").param("term", "admin' OR '1'='1'--"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5));

        mockMvc.perform(get("/api/users/by-term").param("term", "' OR '1'='1'--"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5));
    }

    @Test
    @SneakyThrows
    void findUserSafe() {
        mockMvc.perform(get("/api/users/safe/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].email").value("admin@example.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));

        mockMvc.perform(get("/api/users/safe/john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[0].username").value("john"))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[0].role").value("USER"));
    }

    @Test
    @SneakyThrows
    void getAllUserEntities() {
        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].password").value("admin123"))
                .andExpect(jsonPath("$[0].email").value("admin@example.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }
}
