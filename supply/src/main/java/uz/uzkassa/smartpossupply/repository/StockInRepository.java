package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.enums.TransferType;

import java.util.Optional;

@Repository
public interface StockInRepository extends StockBaseRepository, StockInRepositoryCustom {

    @Query("select s from Stock s where s.id=:id")
    @EntityGraph(attributePaths = {"order", "supplier", "customer", "companyCommittent", "branch", "warehouse", "factura", "party", "stockTransfer", "oldStock", "acceptanceTransferAct", "aic", "products", "products.product", "products.product.parent", "products.productSerial", "products.unit", "products.companyCommittent", "products.lgota", "products.productContract", "products.stock"})
    Optional<Stock> getDetail(@Param("id") Long id);

    Optional<Stock> findFirstByCompanyIdAndPartyIdAndTransferTypeAndDeletedIsFalse(
            Long companyId,
            Long partyId,
            TransferType transferType
    );

    Optional<Stock> findFirstByCompanyIdAndAcceptanceTransferActIdAndTransferTypeAndDeletedIsFalse(
            Long companyId,
            Long actId,
            TransferType transferType
    );

    Optional<Stock> findFirstByCompanyIdAndAicIdAndTransferTypeAndDeletedIsFalse(
            Long companyId,
            Long aicId,
            TransferType transferType
    );
}
