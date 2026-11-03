package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.discount.Discount;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */
@Repository
public interface DiscountRepository extends BaseRepository<Discount, Long>, DiscountRepositoryCustom {

    @EntityGraph(attributePaths = {"branch", "creator", "items"})
    @Query("select d from Discount d where d.id=:id")
    Optional<Discount> getDetail(@Param("id") Long id);


    @EntityGraph(attributePaths = {"branch", "creator", "items"})
    @Query(""" 
            select distinct d 
                from Discount d 
                where d.branchId in :branchIds 
                  and d.deleted = false
            """)
    List<Discount> getDetailForOffline(@Param("branchIds") Set<Long> branchIds);
}