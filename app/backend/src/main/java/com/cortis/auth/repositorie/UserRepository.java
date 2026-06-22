package com.cortis.auth.repositorie;

import com.cortis.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("""
    SELECT DISTINCT u
    FROM UserEntity u
    LEFT JOIN FETCH u.userRoles ur
    LEFT JOIN FETCH ur.role r
    LEFT JOIN FETCH r.rolePermissions rp
    LEFT JOIN FETCH rp.permission p
    WHERE u.username = :username
      AND u.deletedAt IS NULL
""")
    Optional<UserEntity> findByUsernameWithAuthorities(
            @Param("username") String username
    );

    Optional<UserEntity> findByUsername(String username);}


