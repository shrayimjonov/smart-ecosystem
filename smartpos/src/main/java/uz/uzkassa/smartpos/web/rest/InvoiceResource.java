package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.Invoice;
import uz.uzkassa.smartpos.dto.EnumDTO;
import uz.uzkassa.smartpos.dto.ResponseData;
import uz.uzkassa.smartpos.dto.SelectItemDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.factura.*;
import uz.uzkassa.smartpos.dto.filter.InvoiceFilter;
import uz.uzkassa.smartpos.dto.nic.FacturaSendDTO;
import uz.uzkassa.smartpos.dto.nic.InvoiceCallback;
import uz.uzkassa.smartpos.dto.nic.ProviderResultDTO;
import uz.uzkassa.smartpos.dto.nic.SignVM;
import uz.uzkassa.smartpos.dto.warehouse.InvoiceConvertedDTO;
import uz.uzkassa.smartpos.enums.FacturaType;
import uz.uzkassa.smartpos.enums.ProductGroup;
import uz.uzkassa.smartpos.enums.SingleSidedType;
import uz.uzkassa.smartpos.enums.Status;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.FacturaClient;
import uz.uzkassa.smartpos.integration.tasnif.dto.CompanyProductDTO;
import uz.uzkassa.smartpos.service.InvoiceService;
import uz.uzkassa.smartpos.service.LocalizationService;

import java.util.List;

@RestController
@RequestMapping("/api/invoice")
@Slf4j
@RequiredArgsConstructor
public class InvoiceResource {
    private final InvoiceService invoiceService;
    private final FacturaClient facturaClient;
    private final LocalizationService localizationService;

    @GetMapping("/providers")
    public ResponseData<ProviderResultDTO> getProviders() {
        return ResponseData.ok(null);
    }

    @PostMapping("/providers")
    public ResponseData<?> saveProviders(@RequestBody @Valid SignVM signVM) {
        invoiceService.saveProviders(signVM);
        return ResponseData.ok();
    }

    @GetMapping("/statuses")
    public List<SelectItemDTO> getStatuses() {
        return Status.getAll();
    }

    @GetMapping("/product-groups")
    public List<SelectItemDTO> getProductGroups() {
        return ProductGroup.getAll();
    }

    @GetMapping("/types")
    public List<EnumDTO> getTypes() {
        return FacturaType.getAll();
    }

    @GetMapping("/single-sided-types")
    public List<EnumDTO> getSingleSidedTypes() {
        return SingleSidedType.getAll();
    }

    @GetMapping("/guid")
    public ResponseEntity<String> getGuid() {
        return ResponseEntity.ok(facturaClient.getGuid());
    }

    @GetMapping("/customer")
    public ResponseData<FacturaCustomer> getInvoiceCustomer(@RequestParam(required = false) String tin) {
        log.debug("REST request to get Customer for invoice");
        FacturaCustomer result = invoiceService.getCustomer(tin);
        return ResponseData.ok(result);
    }

    @GetMapping("/customer/catalog")
    public ResponseEntity<List<CompanyProductDTO>> getCompanyCatalog(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(invoiceService.getCompanyCatalog(search));
    }

    @GetMapping("/lgota")
    public ResponseData<List<LgotaDTO>> getLgotas(@RequestParam(required = false) String mxikCode) {
        List<LgotaDTO> result = invoiceService.getLgotas(mxikCode);
        return ResponseData.ok(result);
    }

    @PostMapping
    public ResponseData<Long> create(@Valid @RequestBody FacturaDTO facturaDTO) {
        log.debug("REST request to create Invoice, Factura ID : {}", facturaDTO.getFacturaId());
        if ((facturaDTO.getSingleSidedType() == null || facturaDTO.getSingleSidedType() == 0) && StringUtils.isBlank(facturaDTO.getBuyerTin())) {
            throw new BadRequestException(localizationService.getMessage("supplier.required"));
        }
        Invoice invoice = invoiceService.create(facturaDTO);
        return ResponseData.ok(invoice.getId());
    }

    @PutMapping
    public ResponseData<Long> update(@Valid @RequestBody FacturaDTO facturaDTO) {
        log.debug("REST request to update Invoice, Factura ID : {}", facturaDTO.getFacturaId());
        if (StringUtils.isBlank(facturaDTO.getFacturaId())) {
            throw new BadRequestException(localizationService.getMessage("factura.id.required"));
        }
        if (StringUtils.isBlank(facturaDTO.getBuyerTin())) {
            throw new BadRequestException(localizationService.getMessage("supplier.required"));
        }
        Long invoiceId = invoiceService.update(facturaDTO);
        return ResponseData.ok(invoiceId);
    }

    @PostMapping("/send")
    public ResponseData<?> send(@Valid @RequestBody FacturaSendDTO facturaSendDTO) {
        invoiceService.send(facturaSendDTO);
        return ResponseData.ok();
    }

    @PostMapping("/cancel")
    public ResponseData<?> cancel(@Valid @RequestBody FacturaSendDTO facturaSendDTO) {
        invoiceService.changeStatus(facturaSendDTO, Status.CANCELLED);
        return ResponseData.ok();
    }

    @PostMapping("/accept")
    public ResponseData<?> accept(@Valid @RequestBody FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setAction("accept");
        invoiceService.changeStatus(facturaSendDTO, Status.ACCEPTED);
        return ResponseData.ok();
    }

    @PostMapping("/reject")
    public ResponseData<?> reject(@Valid @RequestBody FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setAction("reject");
        invoiceService.changeStatus(facturaSendDTO, Status.REJECTED);
        return ResponseData.ok();
    }

    @GetMapping("/dashboard")
    public ResponseData<Page<DashboardDTO>> getDashboard(@ParameterObject InvoiceFilter filter) {
        Page<DashboardDTO> result = invoiceService.getDashboard(filter);
        return ResponseData.ok(result);
    }

    @Deprecated
    @GetMapping("/search")
    public ResponseData<List<CommonDTO>> search(@ParameterObject InvoiceFilter filter) {
        List<CommonDTO> result = invoiceService.search(filter);
        return ResponseData.ok(result);
    }

    @Deprecated
    @GetMapping("/incomings")
    public ResponseData<Page<DashboardDTO>> getIncomings(@ParameterObject InvoiceFilter filter) {
        Page<DashboardDTO> result = invoiceService.getIncomings(filter);
        return ResponseData.ok(result);
    }

    @Deprecated
    @GetMapping("/outgoings")
    public ResponseData<Page<DashboardDTO>> getOutgoings(@ParameterObject InvoiceFilter filter) {
        Page<DashboardDTO> result = invoiceService.getOutgoings(filter);
        return ResponseData.ok(result);
    }

    @Deprecated
    @GetMapping("/drafts")
    public ResponseData<Page<DashboardDTO>> getDrafts(@ParameterObject InvoiceFilter filter) {
        Page<DashboardDTO> result = invoiceService.getDrafts(filter);
        return ResponseData.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseData<InvoiceDTO> get(@PathVariable Long id) {
        log.debug("REST request to get Invoice: {}", id);
        InvoiceDTO result = invoiceService.findOne(id);
        return ResponseData.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Long id) {
        log.debug("REST request to delete Invoice: {}", id);
        invoiceService.delete(id);
        return ResponseData.ok();
    }

    @GetMapping("/{id}/converted")
    public ResponseData<InvoiceConvertedDTO> getConverted(@PathVariable Long id) {
        log.debug("REST request to get converted items: {}", id);
        InvoiceConvertedDTO result = invoiceService.getConvertedInvoice(id);
        return ResponseData.ok(result);
    }

    @GetMapping("/{id}/used-for-income")
    public boolean usedForIncome(@PathVariable Long id) {
        return invoiceService.usedForIncome(id);
    }

    @PutMapping("/sync/{id}")
    public void sync(@PathVariable Long id) {
        invoiceService.sync(id);
    }

    @GetMapping("/sync-by-facturaId")
    public void syncByFacturaId(@RequestParam String facturaId,
                                @RequestParam(required = false) String sellerTin,
                                @RequestParam(required = false) String buyerTin) {
        invoiceService.syncByFacturaId(facturaId, sellerTin, buyerTin);
    }

    @PostMapping("/webhook/updated")
    public void onUpdate(@Valid @RequestBody InvoiceCallback dto) {
        invoiceService.onUpdate(dto);
    }
}
