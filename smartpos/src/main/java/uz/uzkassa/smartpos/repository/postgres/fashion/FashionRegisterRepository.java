package uz.uzkassa.smartpos.repository.postgres.fashion;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FashionRegisterRepository extends JpaRepository<FashionRegister, Long>, JpaSpecificationExecutor<FashionRegister>, FashionRegisterRepositoryCustom {
    Optional<FashionRegister> findByBranchIdAndProductIdAndCityIdAndProductItemId(Long branchId, Long productId, Long cityId, Long productItemId);

    Optional<FashionRegister> findByBranchIdAndProductIdAndCityIdAndProductItemIdIsNull(Long branchId, Long productId, Long cityId);

    List<FashionRegister> findAllByBranchIdAndProductIdInAndProductItemIdIsNull(Long branchId, Collection<Long> productIds);

    @Query(value = "select sum(qty) from fashion_register where branch_id = :branchId and product_item_id = :productItemId", nativeQuery = true)
    BigDecimal getStockAmountByBranchIdAndProductItemId(@Param("branchId") Long branchId, @Param("productItemId") Long productItemId);

    @Query(value = "select sum(qty) from fashion_register where branch_id = :branchId and product_id = :productId", nativeQuery = true)
    BigDecimal getStockAmountByBranchIdAndProductId(@Param("branchId") Long branchId, @Param("productId") Long productId);

    @Query(value = "select fr.qty from FashionRegister fr where fr.branchId = :branchId and fr.productItemId = :productItemId")
    BigDecimal getQty(@Param("branchId") Long branchId, @Param("productItemId") Long productItemId);

    @Query(value = "select sum(fr.qty) from FashionRegister fr where fr.branchId = :branchId and fr.productId = :productId")
    BigDecimal getQtyByProduct(@Param("branchId") Long branchId, @Param("productId") Long productId);

    List<FashionRegister> findAllByBranchIdAndProductItemIdIn(Long branchId, Collection<Long> productItemId);

    void deleteAllByBranchIdAndCityIdAndProductIdIn(Long branchId, Long cityId, List<Long> categoryIds);

    void deleteByBranchIdAndProductId(Long branchId, Long id);

    void deleteAllByProductItemId(Long productItemId);

    void deleteAllByProductItemColorGroupId(Long colorGroupId);

    @Query(value = "select exists(select id from fashion_register where product_item_id = ?1 and qty > 0)", nativeQuery = true)
    boolean getByProductItemId(Long productItemId);

    @Query(value = """
            select exists(select id
                          from fashion_register fr
                                   inner join product_item pi on pi.generic_id = fr.product_item_id and pi.branch_id = fr.branch_id
                          where pi.color_group_id = ?1 and fr.qty > 0)""", nativeQuery = true)
    boolean getByProductItemColorGroupId(Long colorGroupId);

    void deleteAllByBranchId(@NotNull Long branchId);
}