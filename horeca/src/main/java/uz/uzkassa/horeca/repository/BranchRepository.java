package uz.uzkassa.horeca.repository;

import jakarta.validation.constraints.NotNull;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends BaseRepository<Branch, Long>, BranchRepositoryCustom {
    boolean existsByCompanyIdAndDeletedIsFalseAndNameIgnoreCase(Long companyId, String name);

    boolean existsByCompanyIdAndDeletedIsFalseAndNameIgnoreCaseAndIdNot(Long companyId, String name, Long id);

    Optional<Branch> findFirstByCtsId(Long ctsId);

    List<Branch> findAllByCompanyIdAndDeletedIsFalseAndIdNot(Long companyId, Long id);

    Long countAllByCompanyIdAndStatus(@NotNull Long companyId, CommonStatus status);
}
