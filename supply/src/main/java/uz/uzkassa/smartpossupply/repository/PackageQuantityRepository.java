package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.EntityCompositeId;
import uz.uzkassa.smartpossupply.domain.PackageQuantity;

import java.util.List;

@Repository
public interface PackageQuantityRepository extends JpaRepository<PackageQuantity, EntityCompositeId> {

    List<PackageQuantity> findAllById(EntityCompositeId id);

}
