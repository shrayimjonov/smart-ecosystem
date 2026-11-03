package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.PurchaseOrder;
import uz.uzkassa.silen.dto.filter.PurchaseOrderFilter;

public interface PurchaseOrderRepositoryCustom {
    Page<PurchaseOrder> findAllByFilter(PurchaseOrderFilter filter);
}
