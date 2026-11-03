package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.PersonalIdentityInformation;
import uz.uzkassa.cts.enums.PersonalIdentityType;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalIdentityInformationRepository extends BaseRepository<PersonalIdentityInformation, Long> {

    Optional<PersonalIdentityInformation> findByEmployeeIdAndTypeAndDeletedIsFalse(Long employeeId, PersonalIdentityType type);

    List<PersonalIdentityInformation> findAllByEmployeeIdAndDeletedIsFalse(Long employeeId);

    boolean existsByEmployeeIdAndTypeAndDeletedIsFalse(Long employeeId, PersonalIdentityType type);

    boolean existsByEmployeeIdAndTypeAndIdNotAndDeletedIsFalse(Long employeeId, PersonalIdentityType type, Long id);
}
