package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.CustomerGroup;

import java.util.Optional;

@Repository
public interface CustomerGroupRepository extends BaseRepository<CustomerGroup, Long>, CustomerGroupRepositoryCustom {

    boolean existsByNameAndCompanyId(String name, Long companyId);

    boolean existsByNameAndCompanyIdAndIdNot(String name, Long companyId, Long id);

    @EntityGraph(attributePaths = "customers")
    @Query("select cg from CustomerGroup cg where cg.id=:id")
    Optional<CustomerGroup> getDetail(@Param("id") Long id);
}
