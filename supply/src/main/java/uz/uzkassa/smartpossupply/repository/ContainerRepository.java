package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.container.Container;

import java.util.Optional;

@Repository
public interface ContainerRepository extends BaseRepository<Container, Long>, ContainerRepositoryCustom {

    @EntityGraph(attributePaths = {"items", "items.product", "items.product.unit", "items.product.category"})
    @Query("select c from Container c where c.id = :id")
    Optional<Container> getDetail(@Param("id") Long id);

    boolean existsByCompanyIdAndIdentificationNumberIgnoreCase(Long companyId, String identificationNumber);

    boolean existsByCompanyIdAndIdentificationNumberIgnoreCaseAndIdNot(Long companyId, String identificationNumber, Long id);
}
