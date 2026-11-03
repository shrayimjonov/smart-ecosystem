package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.repository.custom.BranchRepositoryCustom;

import java.util.List;

@Repository
public interface BranchRepository extends BaseRepository<Branch, Long>, BranchRepositoryCustom {

    List<Branch> findAllByIdIn(List<Long> ids);

    List<Branch> findAllByOrderByNameAsc();

    boolean existsByNameIsIgnoreCaseAndCompanyIdAndDeletedIsFalse(String name, Long companyId);

    boolean existsByNameIsIgnoreCaseAndCompanyIdAndDeletedIsFalseAndIdNot(String name, Long companyId, Long id);

    List<Branch> findAllByCompanyIdAndStatusAndParentIdIsNullAndDeletedIsFalse(Long companyId, CommonStatus status);

    List<Branch> findAllByCompanyIdAndStatusAndDeletedIsFalse(Long companyId, CommonStatus status);

    @Query("select b.id from Branch b where b.deleted = false and b.status=:status and b.companyId=:companyId")
    List<Long> findAllIdsByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") CommonStatus status);
}
