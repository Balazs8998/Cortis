package com.cortis.repositories.feature_value;

import com.cortis.entity.core.EntityFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EntityFeatureRepository extends JpaRepository <EntityFeature, UUID>{

}
