package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.hall.Hall;

public interface HallRepository extends BaseRepository<Hall, Long>, HallRepositoryCustom {
    boolean existsByBranchIdAndDeletedIsFalseAndNameIgnoreCase(Long branchId, String name);

    boolean existsByBranchIdAndDeletedIsFalseAndNameIgnoreCaseAndIdNot(Long branchId, String name, Long id);
}
