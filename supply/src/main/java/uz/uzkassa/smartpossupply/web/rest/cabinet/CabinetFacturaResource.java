package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.auth.SignDTO;
import uz.uzkassa.smartpossupply.dto.factura.CheckFacturaCodeDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaCompanyCommittentDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaLookUpDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaProductListDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaSendDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaStatisticsByStatusDTO;
import uz.uzkassa.smartpossupply.dto.factura.InvestmentObjectDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.factura.supply.SupplyFacturaDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.SupplyFacturaListDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.SupplyFacturaUpdateDTO;
import uz.uzkassa.smartpossupply.dto.factura.sync.FacturaSyncDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.soliq.TaxCompanyBranchDTO;
import uz.uzkassa.smartpossupply.dto.soliq.VATCompanyDateDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.FacturaType;
import uz.uzkassa.smartpossupply.enums.GuidType;
import uz.uzkassa.smartpossupply.enums.InvoiceStatus;
import uz.uzkassa.smartpossupply.filters.FacturaFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.integration.SoliqClient;
import uz.uzkassa.smartpossupply.service.FacturaService;
import uz.uzkassa.smartpossupply.service.ProductFinancialDiscountService;
import uz.uzkassa.smartpossupply.service.ProductService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetFacturaRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CabinetFacturaResource {

    FacturaService facturaService;
    ProductService productService;
    FacturaRestClient facturaRestClient;
    SoliqClient soliqClient;
    ProductFinancialDiscountService productFinancialDiscountService;

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<SupplyFacturaListDTO>> getList(@ParameterObject FacturaFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(facturaService.getList(filter));
    }

    @Operation(summary = "Drafts Ids")
    @GetMapping("/drafts-ids")
    public ResponseEntity<List<Long>> getDraftIds() {
        return ResponseEntity.ok(facturaService.getDraftIds());
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<SupplyFacturaDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.get(id));
    }

    @Operation(summary = "Get Factura Products")
    @GetMapping("/products/{facturaId}")
    public ResponseEntity<FacturaProductListDTO> getProducts(@PathVariable Long facturaId) {
        return ResponseEntity.ok(facturaService.getProducts(facturaId));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id + "/with-product-type")
    public ResponseEntity<SupplyFacturaDTO> getWithProductType(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.getWithProductType(id));
    }

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody FacturaDTO facturaDTO) {
        return ResponseEntity.ok(facturaService.create(facturaDTO));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody FacturaDTO facturaDTO) {
        return ResponseEntity.ok(facturaService.update(id, facturaDTO));
    }

    @Operation(summary = "Convert Stock to Factura")
    @PostMapping("/convert/stock/{stockId}")
    public ResponseEntity<Long> convert(@PathVariable Long stockId,
                                        @RequestParam(value = "oldFacturaId", required = false) Long oldFacturaId,
                                        @RequestParam(value = "facturaType", required = false) Integer facturaType
    ) {
        return ResponseEntity.ok(facturaService.convertFromStockV2(stockId, oldFacturaId, facturaType));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<FacturaLookUpDTO>> lookUp(@ParameterObject FacturaFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setStatus(Status.ACCEPTED.name());
        return ResponseEntity.ok(facturaService.lookUp(filter));
    }

    @Operation(summary = "Products Look Up")
    @GetMapping("/products/lookup")
    public ResponseEntity<List<ProductLookUpDTO>> getProductLookUp(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return ResponseEntity.ok(productService.lookUp(filter));
    }

    @Operation(summary = "Financial Discount Product")
    @GetMapping("/financial-discount-product")
    public ResponseEntity<List<ProductLookUpDTO>> getFinancialDiscountProduct() {
        return ResponseEntity.ok(productFinancialDiscountService.getFinancialDiscountProduct());
    }

    @Operation(summary = ApiConstants.Check)
    @PostMapping(ApiConstants.check)
    public ResponseEntity<List<CisInfoResponse>> check(@RequestBody @Valid BatchValidationDTO validationDTO) {
        return ResponseEntity.ok(facturaService.check(validationDTO));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<InvoiceStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(InvoiceStatus.class));
    }

    @Operation(summary = ApiConstants.Types)
    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<FacturaType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(FacturaType.class));
    }

    @Operation(summary = ApiConstants.Sync)
    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Void> sync(@RequestBody FacturaSyncDTO facturaSyncDTO) {
        facturaService.sync(facturaSyncDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Sync)
    @PutMapping("/sync/products/{id}")
    public ResponseEntity<Void> syncProducts(@PathVariable Long id) {
        facturaService.syncProducts(id);
        return ResponseEntity.ok().build();
    }

    @Deprecated
    @Operation(summary = ApiConstants.Sync)
    @GetMapping("/sync/{id}")
    public ResponseEntity<Void> sync(@PathVariable Long id) {
        facturaService.syncById(id, false);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Sync)
    @GetMapping("/sync/incoming/{id}")
    public ResponseEntity<Void> syncIncoming(@PathVariable Long id) {
        facturaService.syncById(id, true);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Sync)
    @GetMapping("/sync/outgoing/{id}")
    public ResponseEntity<Void> syncOutgoing(@PathVariable Long id) {
        facturaService.syncById(id, false);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Send)
    @PostMapping(ApiConstants.send)
    public ResponseEntity<Long> send(@RequestBody FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setClientIp(SecurityUtils.getCurrentUserIP());
        return ResponseEntity.ok(facturaService.send(facturaSendDTO));
    }

    @Operation(summary = ApiConstants.Cancel)
    @PostMapping(ApiConstants.cancel)
    public ResponseEntity<Long> cancel(@RequestBody FacturaSendDTO facturaSendDTO) {
        return ResponseEntity.ok(facturaService.changeStatus(facturaSendDTO, Status.CANCELLED));
    }

    @Operation(summary = "Cancel Draft")
    @PostMapping(ApiConstants.cancelDraft)
    public ResponseEntity<Long> cancelDraft(@RequestBody FacturaSendDTO facturaSendDTO) {
        return ResponseEntity.ok(facturaService.cancelDraft(facturaSendDTO));
    }

    @Operation(summary = ApiConstants.Accept)
    @PostMapping(ApiConstants.accept)
    public ResponseEntity<Long> accept(@Valid @RequestBody FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setAction("accept");
        return ResponseEntity.ok(facturaService.changeStatus(facturaSendDTO, Status.ACCEPTED));
    }

    @Operation(summary = ApiConstants.Reject)
    @PostMapping(ApiConstants.reject)
    public ResponseEntity<Long> reject(@Valid @RequestBody FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setAction("reject");
        return ResponseEntity.ok(facturaService.changeStatus(facturaSendDTO, Status.REJECTED));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facturaService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Sign By Factura Client")
    @GetMapping("/{tin}/{facturaId}/{status}")
    public ResponseEntity<String> getSignByFacturaClient(
        @PathVariable String tin,
        @PathVariable String facturaId,
        @PathVariable String status,
        @RequestParam("isBuyer") boolean isBuyer
    ) {
        return ResponseEntity.ok(facturaRestClient.getSign(facturaId, tin, Status.valueOf(status), isBuyer));
    }

    @Operation(summary = "Decode Sign")
    @PostMapping("/decode-sign")
    public ResponseEntity<String> decodeSign(@RequestBody SignDTO signDTO) {
        return ResponseEntity.ok(facturaService.decodeSign(signDTO));
    }

    @GetMapping("/seller-factura")
    public ResponseEntity<String> getSellerFactura(@RequestParam("tin") String tin,
                                                   @RequestParam("facturaId") String facturaId) {
        return ResponseEntity.ok(facturaRestClient.getSellerFactura(tin, facturaId));
    }

    @GetMapping("/seller-factura-sign")
    public ResponseEntity<String> getSellerFactura(@RequestParam("tin") String tin,
                                                   @RequestParam("facturaId") String facturaId,
                                                   @RequestParam("status") String status) {
        return ResponseEntity.ok(facturaRestClient.getSellerFacturaSign(tin, facturaId, status));
    }

    @GetMapping("/buyer-factura")
    public ResponseEntity<String> getBuyerFactura(@RequestParam("tin") String tin,
                                                  @RequestParam("facturaId") String facturaId) {
        return ResponseEntity.ok(facturaRestClient.getBuyerFactura(tin, facturaId));
    }

    @GetMapping("/buyer-factura-sign")
    public ResponseEntity<String> getBuyerFactura(@RequestParam("tin") String tin,
                                                  @RequestParam("facturaId") String facturaId,
                                                  @RequestParam("status") String status) {
        return ResponseEntity.ok(facturaRestClient.getBuyerFacturaSign(tin, facturaId, status));
    }

    @Operation(summary = ApiConstants.Check)
    @PostMapping(ApiConstants.check + "/codes")
    public ResponseEntity<CheckFacturaCodeDTO> checkCode(@RequestBody CheckFacturaCodeDTO checkFacturaCodeDTO) {
        return ResponseEntity.ok(facturaService.checkCode(checkFacturaCodeDTO));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<FacturaStatisticsByStatusDTO> getStatistics(@ParameterObject FacturaFilter filter) {
        return ResponseEntity.ok(facturaService.getStatistics(filter));
    }

    @GetMapping("/generate-guid")
    public ResponseEntity<String> generateGUID() {
        return ResponseEntity.ok(facturaService.getGuid(GuidType.FACTURA));
    }

    @GetMapping("/product-list/{tin}/{facturaProductId}")
    public ResponseEntity<FacturaProductListDTO> getProductList(@PathVariable String tin, @PathVariable String facturaProductId) {
        return ResponseEntity.ok(facturaService.getFacturaProductList(tin, facturaProductId));
    }

    @PatchMapping("/data/{id}")
    public ResponseEntity<SupplyFacturaUpdateDTO> updateData(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.updateData(id));
    }

    @PatchMapping("/company-vat-registration/{id}")
    public ResponseEntity<VATCompanyDateDTO> updateVatCompanyRegistration(@PathVariable Long id,
                                                                          @RequestParam(value = "sellerId", required = false) Long sellerId,
                                                                          @RequestParam(value = "buyerId", required = false) Long buyerId) {
        return ResponseEntity.ok(facturaService.updateCompanyVatRegistration(id, sellerId, buyerId));
    }

    @Operation(summary = "Committent")
    @GetMapping("/committent/{tin}")
    public ResponseEntity<FacturaCompanyCommittentDTO> getCommittent(@PathVariable String tin) {
        return ResponseEntity.ok(facturaService.getCommittent(tin));
    }

    @Operation(summary = "Branches")
    @GetMapping("/branches/{tin}")
    public ResponseEntity<TaxCompanyBranchDTO[]> getBranches(@PathVariable String tin) {
        return ResponseEntity.ok(soliqClient.getBranchesByCompany(tin, true));
    }

    @GetMapping("/investment-object/{tin}")
    public ResponseEntity<InvestmentObjectDTO> getInvestmentObject(@PathVariable String tin) {
        return ResponseEntity.ok(facturaService.getInvestmentObject(tin));
    }

}
