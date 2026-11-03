package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.dto.Result;
import uz.uzkassa.silen.dto.filter.FacturaInvestmentFilter;
import uz.uzkassa.silen.dto.filter.InvoiceFilter;
import uz.uzkassa.silen.dto.filter.OrganizationFilter;
import uz.uzkassa.silen.dto.invoice.*;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.service.CustomerService;
import uz.uzkassa.silen.service.InvoiceService;
import uz.uzkassa.silen.service.job.SyncInvoiceTuronStateJob;
import uz.uzkassa.silen.utils.ServerUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceResource {
    InvoiceService invoiceService;
    CustomerService customerService;
    SyncInvoiceTuronStateJob syncInvoiceTuronStateJob;

    @GetMapping("/types")
    public List<EnumDTO> getTypes() {
        return FacturaType.getAll();
    }

    @GetMapping("/statuses")
    public List<EnumDTO> getStatuses() {
        return Status.getAll();
    }

    @GetMapping("/single-sided-types")
    public List<EnumDTO> getSingleSidedTypes() {
        return SingleSidedType.getAll();
    }

    @GetMapping("/filter-types")
    public List<EnumDTO> getFilterTypes() {
        return InvoiceType.getAll();
    }

    @GetMapping("/product-origin")
    public ResponseEntity<List<EnumDTO>> getProductOrigin() {
        return ResponseEntity.ok().body(ProductOrigin.getAll());
    }

    @GetMapping("/customer")
    public ResponseEntity<FacturaCustomer> getInvoiceCustomer(@RequestParam(required = false) String tin) {
        if (tin == null) {
            tin = SecurityUtils.getCurrentOrganizationTin();
        }
        FacturaCustomer result = customerService.getCustomer(tin, true);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Result<Long>> create(@Valid @RequestBody final FacturaDTO facturaDTO) {
        if ((facturaDTO.getSingleSidedType() == null || facturaDTO.getSingleSidedType() == 0) && StringUtils.isBlank(facturaDTO.getBuyerTin())) {
            throw new BadRequestException("Supplier required");
        }
        return Optional.of(facturaDTO)
                .map(invoiceService::create)
                .map(Result::success)
                .map(ResponseEntity::ok)
                .orElseThrow(BadRequestException::new);
    }

    @PutMapping
    public ResponseEntity<Result<Long>> update(@Valid @RequestBody final FacturaDTO facturaDTO) {
        if (StringUtils.isBlank(facturaDTO.getFacturaId())) {
            throw new BadRequestException("FacturaId required");
        }
        if (StringUtils.isBlank(facturaDTO.getBuyerTin())) {
            throw new BadRequestException("Supplier required");
        }
        return Optional.of(facturaDTO)
                .map(invoiceService::update)
                .map(Result::success)
                .map(ResponseEntity::ok)
                .orElseThrow(BadRequestException::new);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<InvoiceDTO>> get(@PathVariable final Long id) {
        return Optional.of(id)
                .map(invoiceService::findOne)
                .map(Result::success)
                .map(ResponseEntity::ok)
                .orElseThrow(BadRequestException::new);
    }

    @GetMapping("/quantity/{id}")
    public ResponseEntity<Long> getItemQuantityInUnit(@PathVariable final Long id) {
        return ResponseEntity.ok(invoiceService.getQuantityInUnit(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> delete(@PathVariable final Long id) {
        invoiceService.delete(id);
        return ResponseEntity.ok(Result.success());
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceDTO>> getAll(@ModelAttribute final InvoiceFilter filter) {
        return Optional.of(filter)
                .map(invoiceService::findAll)
                .map(ResponseEntity::ok)
                .orElseThrow(BadRequestException::new);
    }

    @GetMapping("/items")
    public ResponseEntity<List<InvoiceSelectItemDTO>> getItems(@ModelAttribute final InvoiceFilter filter) {
        return ResponseEntity.ok(invoiceService.getItems(filter));
    }

    @GetMapping("/1c")
    public ResponseEntity<Page<InvoiceDTO>> getAllForOneC(@ModelAttribute final InvoiceFilter filter) {
        return Optional.of(filter)
                .map(invoiceService::findAllForOneC)
                .map(ResponseEntity::ok)
                .orElseThrow(BadRequestException::new);
    }

    @GetMapping("/dashboard-info")
    public ResponseEntity<Result<DashboardInfoDTO>> getDashboardInfo(@ModelAttribute final InvoiceFilter filter) {
        return Optional.of(filter)
                .map(invoiceService::getDashboardInfo)
                .map(Result::success)
                .map(ResponseEntity::ok)
                .orElseThrow(BadRequestException::new);
    }

    @PostMapping("/send")
    public ResponseEntity<Result<Void>> send(@Valid @RequestBody final FacturaSendDTO dto) {
        dto.setClientIp(ServerUtils.getCurrentUserIP());
        invoiceService.send(dto);
        return ResponseEntity.ok(Result.success());
    }

    @PostMapping("/cancel")
    public ResponseEntity<Result<Void>> cancel(@Valid @RequestBody final FacturaSendDTO dto) {
        dto.setClientIp(ServerUtils.getCurrentUserIP());
        invoiceService.acceptOrReject(dto, Status.CANCELLED);
        return ResponseEntity.ok(Result.success());
    }

    @PostMapping("/accept")
    public ResponseEntity<Result<Void>> accept(@Valid @RequestBody final FacturaSendDTO dto) {
        dto.setAction("accept");
        dto.setClientIp(ServerUtils.getCurrentUserIP());
        invoiceService.acceptOrReject(dto, Status.ACCEPTED);
        return ResponseEntity.ok(Result.success());
    }

    @PostMapping("/reject")
    public ResponseEntity<Result<Void>> reject(@Valid @RequestBody final FacturaSendDTO dto) {
        dto.setAction("reject");
        dto.setClientIp(ServerUtils.getCurrentUserIP());
        invoiceService.acceptOrReject(dto, Status.REJECTED);
        return ResponseEntity.ok(Result.success());
    }

    @GetMapping("/sync")
    public void sync(@RequestParam final Long invoiceId) {
        invoiceService.sync(invoiceId);
    }

    @GetMapping("/investment")
    public ResponseEntity<Object> getInvestmentObject(@ModelAttribute FacturaInvestmentFilter filter) {
        return ResponseEntity.ok(invoiceService.getInvestmentObject(filter));
    }

    @GetMapping("/investment/{tinOrPinfl}")
    public ResponseEntity<Object> checkInvestment(@PathVariable String tinOrPinfl) {
        return ResponseEntity.ok(invoiceService.checkInvestment(tinOrPinfl));
    }

    @GetMapping("/sync-all")
    public void syncAll() {
        invoiceService.syncAll();
    }

    @PutMapping("/sync-ikpu/{id}")
    public void syncProductsIkpu(@PathVariable Long id) {
        invoiceService.syncProductIKPU(id);
    }

    @PutMapping("/sync/turon-status/{time}")
    public ResponseEntity<Void> syncTuronStatus(@PathVariable LocalDateTime time, @ModelAttribute final OrganizationFilter organizationFilter) {
        syncInvoiceTuronStateJob.checkByOrganizationFilter(organizationFilter, time);
        return ResponseEntity.ok().build();
    }
}
