package uz.uzkassa.smartpos.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.ResponseData;
import uz.uzkassa.smartpos.dto.factura.DashboardDTO;
import uz.uzkassa.smartpos.dto.factura.InvoiceDTO;
import uz.uzkassa.smartpos.dto.filter.InvoiceFilter;
import uz.uzkassa.smartpos.service.InvoiceService;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/invoice")
@Secured({SMARTPOS_ADMIN, SUPPORT, SALES_MANAGER})
@RequiredArgsConstructor
public class AdminInvoiceResource {
    private final InvoiceService invoiceService;

    @GetMapping("/{id}")
    public ResponseData<InvoiceDTO> get(@PathVariable Long id) {
        log.debug("REST request to get Invoice: {}", id);
        InvoiceDTO result = invoiceService.findOne(id);
        return ResponseData.ok(result);
    }


    @GetMapping("/dashboard")
    public ResponseData<Page<DashboardDTO>> getDashboard(@ParameterObject InvoiceFilter filter) {
        Page<DashboardDTO> result = invoiceService.getDashboard(filter);
        return ResponseData.ok(result);
    }

    @PutMapping("/sync/{id}")
    public void sync(@PathVariable Long id) {
        invoiceService.sync(id);
    }
}
