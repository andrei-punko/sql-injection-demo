package by.andd3dfx.sqlinjection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SqlInjectionDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqlInjectionDemoApplication.class, args);
        System.out.println("\n==========================================");
        System.out.println("SQL Injection Demo Application started!");
        System.out.println("==========================================");
        System.out.println("API available at: http://localhost:8080/api/users");
        System.out.println("H2 Console: http://localhost:8080/h2-console");
        System.out.println("\nVulnerable request examples:");
        System.out.println("GET /api/users/search?username=admin' OR '1'='1");
        System.out.println("GET /api/users/search?username=admin'--");
        System.out.println("POST /api/users/login");
        System.out.println("  Body: {\"username\": \"admin\", \"password\": \"' OR '1'='1\"}");
        System.out.println("==========================================\n");
    }
}

