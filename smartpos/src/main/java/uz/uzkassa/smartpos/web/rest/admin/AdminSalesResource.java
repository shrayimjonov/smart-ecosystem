package uz.uzkassa.smartpos.web.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatDto;
import uz.uzkassa.smartpos.dto.sales.SalesCabinetListDto;
import uz.uzkassa.smartpos.filter.SalesFilter;
import uz.uzkassa.smartpos.service.SalesService;

@RequestMapping("/api/v1/admin/sales")
@RestController
@RequiredArgsConstructor
public class AdminSalesResource {
    private final SalesService salesService;

    @GetMapping
    public Page<SalesCabinetListDto> findAllByFilter(@ParameterObject SalesFilter filter) {
        return salesService.findAllByFilter(filter, true);
    }

    @GetMapping("/statistics")
    public ReceiptStatDto statistics(@ParameterObject SalesFilter filter) {
        return salesService.statistics(filter, true);
    }
}
