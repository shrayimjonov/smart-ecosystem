package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Accessory;
import uz.uzkassa.cts.repository.custom.AccessoryRepositoryCustom;

import java.util.Set;

@Repository
public interface AccessoryRepository extends BaseRepository<Accessory, Long>, AccessoryRepositoryCustom {

    @EntityGraph(attributePaths = {"model"})
    Page<Accessory> findAll(Specification<Accessory> specification, Pageable pageable);

    @Query("select a from Accessory a where a.id  in (?1)")
    Set<Accessory> findByIdIn(Set<Long> ids);
}
