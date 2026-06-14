package com.cortis.repositories.personal;

import com.cortis.entity.user.PermissionEntity;
import jakarta.persistence.Persistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {
}
