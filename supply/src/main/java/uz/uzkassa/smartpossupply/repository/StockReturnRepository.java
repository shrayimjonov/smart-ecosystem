package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.enums.TransferType;

import java.util.Optional;

@Repository
public interface StockReturnRepository extends StockBaseRepository, StockReturnRepositoryCustom {

    boolean existsByCompanyIdAndFacturaIdAndTransferTypeAndDeletedIsFalseAndIdNot(Long companyId, Long facturaId, TransferType transferType, Long id);

    @Query("select s from Stock s where s.id=:id")
    @EntityGraph(attributePaths = {"customer", "branch", "warehouse", "factura", "contract", "products","products.product"})
    Optional<Stock> getDetail(@Param("id") Long id);
}
