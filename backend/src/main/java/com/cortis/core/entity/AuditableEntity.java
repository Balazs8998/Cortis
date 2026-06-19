package com.cortis.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class AuditableEntity {

    /*
     * Ezeket az adatbázis tölti:
     * DEFAULT current_setting(...)
     */
    @Column(
            name = "created_by",
            insertable = false,
            updatable = false
    )
    private String createdBy;

    /*
     * Ezt is az adatbázis tölti:
     * DEFAULT now()
     */
    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
