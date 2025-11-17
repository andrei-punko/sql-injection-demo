package by.andd3dfx.sqlinjection.repository;

import by.andd3dfx.sqlinjection.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * VULNERABLE REPOSITORY - SQL INJECTION DEMONSTRATION
 * <p>
 * WARNING: This code contains security vulnerabilities!
 * DO NOT USE IN PRODUCTION!
 */
@Repository
public class VulnerableUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * VULNERABLE METHOD: Uses string concatenation in SQL query
     * <p>
     * Attack examples:
     * - username = "admin' OR '1'='1" - returns all users
     * - username = "admin'--" - comments out the rest of the query
     * - username = "admin'; DROP TABLE users;--" - drops the table
     */
    @SuppressWarnings("unchecked")
    public List<User> findByUsernameVulnerable(String username) {
        String sql = "SELECT * FROM users WHERE username = '" + username + "'";
        Query query = entityManager.createNativeQuery(sql, User.class);
        return query.getResultList();
    }

    /**
     * VULNERABLE METHOD: Search by email with string concatenation
     */
    @SuppressWarnings("unchecked")
    public List<User> findByEmailVulnerable(String email) {
        String sql = "SELECT * FROM users WHERE email = '" + email + "'";
        Query query = entityManager.createNativeQuery(sql, User.class);
        return query.getResultList();
    }

    /**
     * VULNERABLE METHOD: Search with multiple conditions
     */
    @SuppressWarnings("unchecked")
    public List<User> findByUsernameAndPasswordVulnerable(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
        Query query = entityManager.createNativeQuery(sql, User.class);
        return query.getResultList();
    }

    /**
     * VULNERABLE METHOD: Search with LIKE and concatenation
     */
    @SuppressWarnings("unchecked")
    public List<User> searchUsersVulnerable(String searchTerm) {
        String sql = "SELECT * FROM users WHERE " +
                "username LIKE '%" + searchTerm + "%' OR " +
                "email LIKE '%" + searchTerm + "%'";
        Query query = entityManager.createNativeQuery(sql, User.class);
        return query.getResultList();
    }
}

