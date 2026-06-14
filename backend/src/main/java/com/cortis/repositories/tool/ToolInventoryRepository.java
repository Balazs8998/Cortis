package java.com.cortis.repositories.tool;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.com.cortis.entity.company.ToolInventory;
import java.util.UUID;

@Repository
public interface ToolInventoryRepository extends JpaRepository<ToolInventory, UUID> {


}
