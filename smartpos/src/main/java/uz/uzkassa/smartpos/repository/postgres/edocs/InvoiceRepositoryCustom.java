package uz.uzkassa.smartpos.repository.postgres.edocs;

import org.springframework.data.domain.Page;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.factura.InvoiceListDto;
import uz.uzkassa.smartpos.dto.filter.InvoiceFilter;

import java.util.List;

public interface InvoiceRepositoryCustom {
    Page<InvoiceListDto> findAllByFilter(InvoiceFilter filter, boolean forExcel);

    List<CommonDTO> search(InvoiceFilter filter);
}
