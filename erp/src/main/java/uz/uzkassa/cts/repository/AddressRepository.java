package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Address;
import uz.uzkassa.cts.enums.AddressType;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends BaseRepository<Address, Long> {

    Optional<Address> findFirstByEntityIdAndEntityTypeAndTypeAndDeletedIsFalse(Long entityId, EntityTypeEnum entityType, AddressType type);

    List<Address> findAllByEntityIdAndEntityTypeAndDeletedIsFalse(Long entityId, EntityTypeEnum entityType);

    boolean existsByEntityIdAndEntityTypeAndTypeAndDeletedFalse(Long entityId, EntityTypeEnum entityType, AddressType type);

    boolean existsByEntityIdAndEntityTypeAndTypeAndDeletedFalseAndIdNot(Long entityId, EntityTypeEnum entityType, AddressType type, Long id);

}
