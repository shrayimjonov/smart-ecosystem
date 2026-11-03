package uz.uzkassa.smartpos.repository.postgres.receipt;

import org.springframework.data.domain.Page;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;

public interface ReceiptRepositoryCustom {

    Page<Receipt> findAllDrafts(ReceiptFilter filter);

}
