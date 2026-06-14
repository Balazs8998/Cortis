package java.com.cortis.repositories.feature_value;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.com.cortis.entity.core.EntityFeatureValue;
import java.util.UUID;

@Repository
public interface EntityFeatureValueRepository extends JpaRepository<EntityFeatureValue, UUID> {
}
