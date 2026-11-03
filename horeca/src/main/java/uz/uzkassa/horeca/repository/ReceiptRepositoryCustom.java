package uz.uzkassa.horeca.repository;

import org.springframework.data.domain.Page;
import uz.uzkassa.horeca.domain.receipt.Receipt;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.receipt.ReceiptStatisticDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.filters.ReceiptFilter;

public interface ReceiptRepositoryCustom {
    Page<Receipt> getResultList(ReceiptFilter filter);

    ReceiptStatisticDTO getStatistics(ReceiptFilter filter);
}
