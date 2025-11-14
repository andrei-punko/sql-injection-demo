package com.example.sqlinjection.repository;

import com.example.sqlinjection.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Safe method with parameterized query
    @Query("SELECT u FROM User u WHERE u.username = :username")
    List<User> findByUsernameSafe(@Param("username") String username);
    
    // Standard Spring Data JPA method (safe)
    List<User> findByUsername(String username);
}

