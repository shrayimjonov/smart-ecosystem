package uz.uzkassa.smartpossupply.web.rest.integration;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.aop.onec.OneCValidate;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.factura.FacturaDTO;
import uz.uzkassa.smartpossupply.dto.receipt.TerminalReceiptDTO;
import uz.uzkassa.smartpossupply.dto.warehouseproduct.WarehouseProductQtyDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.FacturaFilter;
import uz.uzkassa.smartpossupply.filters.ReceiptFilter;
import uz.uzkassa.smartpossupply.filters.WaybillFilter;
import uz.uzkassa.smartpossupply.service.FacturaService;
import uz.uzkassa.smartpossupply.service.ReceiptService;
import uz.uzkassa.smartpossupply.service.WarehouseProductService;
import uz.uzkassa.smartpossupply.service.WaybillService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.oneCIntegrationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@OneCValidate
public class OneCIntegrationResource {

    FacturaService facturaService;
    WaybillService waybillService;
    ReceiptService receiptService;
    WarehouseProductService warehouseProductService;

    @Operation(summary = "Incoming Factura List")
    @GetMapping("/factura/incoming")
    public ResponseEntity<Page<FacturaDTO>> get1CIncomingList(FacturaFilter filter) {
        filter.setIncoming(true);
        return this.getList(filter);
    }

    @Operation(summary = "Outgoing Factura List")
    @GetMapping("/factura/outgoing")
    public ResponseEntity<Page<FacturaDTO>> get1COutgoingList(FacturaFilter filter) {
        filter.setIncoming(false);
        return this.getList(filter);
    }

    private ResponseEntity<Page<FacturaDTO>> getList(FacturaFilter filter) {
        if (StringUtils.isEmpty(SecurityUtils.getCurrentCompanyTin())) {
            throw new BadRequestException("Company TIN is required!");
        }
        return ResponseEntity.ok(facturaService.getListTo1C(filter));
    }

    @Operation(summary = "Incoming Waybill List")
    @GetMapping("/waybill/incoming")
    public ResponseEntity<Page<WaybillDTO>> get1CIncomingList(WaybillFilter filter) {
        if (StringUtils.isEmpty(filter.getConsigneeTin())) {
            throw new BadRequestException("Consignee TIN is required!");
        }
        filter.setOutgoing(false);
        return this.getList(filter);
    }

    @Operation(summary = "Outgoing Waybill List")
    @GetMapping("/waybill/outgoing")
    public ResponseEntity<Page<WaybillDTO>> get1COutgoingList(WaybillFilter filter) {
        if (StringUtils.isEmpty(filter.getConsignorTin())) {
            throw new BadRequestException("Consignor TIN is required!");
        }
        filter.setOutgoing(true);
        return this.getList(filter);
    }

    private ResponseEntity<Page<WaybillDTO>> getList(WaybillFilter filter) {
        return ResponseEntity.ok(waybillService.getListTo1C(filter));
    }

    @Operation(summary = "Receipt List")
    @GetMapping("/receipt")
    public ResponseEntity<Page<TerminalReceiptDTO>> getListTo1C(ReceiptFilter filter) {
        if (StringUtils.isEmpty(filter.getTin())) {
            throw new BadRequestException("Tin is required!");
        }
        return ResponseEntity.ok(receiptService.getListTo1C(filter));
    }

    @GetMapping("/product-qty")
    public ResponseEntity<WarehouseProductQtyDTO> getProductQty(BaseFilter filter) {
        return ResponseEntity.ok(warehouseProductService.getProductQty(filter));
    }
}