package uz.uzkassa.smartpos.repository.postgres.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProductDetail;

@Repository
public interface IncomeOfProductDetailRepository extends JpaRepository<IncomeOfProductDetail, Long>, IncomeOfProductDetailRepositoryCustom {

    @Modifying
    @Query("delete from IncomeOfProductDetail where branchId =:branchId")
    void deleteByBranchId(@Param("branchId") Long branchId);

    @Modifying
    @Query(value = "delete from income_of_product_detail iopd using product_branch pb where iopd.product_id = pb.product_id and iopd.branch_id = pb.branch_id and iopd.city_id = pb.city_id and pb.deleted and iopd.city_id = :cityId", nativeQuery = true)
    void cleanup(@Param("cityId") Long cityId);
}
