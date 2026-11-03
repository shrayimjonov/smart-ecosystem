package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.importreport.OrderImportReportDTO;
import uz.uzkassa.smartpossupply.dto.order.AgentTrackingOrderListDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderExpeditorReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderExpeditorTotalQtyReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderListDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderListForTransferDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderStatusUpdateDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderUpdateStatusDTO;
import uz.uzkassa.smartpossupply.dto.order.ProductExpeditorReportCreateDTO;
import uz.uzkassa.smartpossupply.dto.order.ProductExpeditorResponseDTO;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerReportDTO;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerSummaryDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpWithWarehouseQtyDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.trip.OrderTripDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.OrderRejectReason;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.filters.OrderReportFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.service.OrderService;
import uz.uzkassa.smartpossupply.service.handlers.excel.OrderExpeditorReportStrategy;
import uz.uzkassa.smartpossupply.service.handlers.excel.OrderListStrategy;
import uz.uzkassa.smartpossupply.service.handlers.excel.OrderProductExpeditorStrategy;
import uz.uzkassa.smartpossupply.service.handlers.excel.SalesAndCustomerStrategy;
import uz.uzkassa.smartpossupply.service.handlers.excel.importreport.OrderImportReportExcelHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.template.OrderBatchImportTemplateExcelHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.template.OrderImportTemplateExcelHandler;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetOrderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetOrderResource {

    OrderService orderService;
    OrderListStrategy orderListStrategy;
    OrderImportReportExcelHandler orderImportReportExcelHandler;
    OrderImportTemplateExcelHandler orderImportTemplateExcelHandler;
    OrderBatchImportTemplateExcelHandler orderBatchImportTemplateExcelHandler;
    OrderProductExpeditorStrategy orderProductExpeditorStrategy;
    OrderExpeditorReportStrategy orderExpeditorReportStrategy;
    SalesAndCustomerStrategy salesAndCustomerStrategy;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.create(orderDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<OrderDetailDTO> get(@PathVariable Long id, @RequestParam(required = false) Boolean fetchQtyInWarehouse) {
        return ResponseEntity.ok(orderService.get(id, fetchQtyInWarehouse));
    }

    @GetMapping("/report")
    public ResponseEntity<Page<OrderReportDTO>> getReportByOrder(@ParameterObject OrderReportFilter filter) {
        return ResponseEntity.ok(orderService.getReport(filter));
    }

    @Operation(summary = ApiConstants.Copy)
    @PutMapping(ApiConstants.copy)
    public ResponseEntity<Long> copy(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.copy(id));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.update(id, orderDTO));
    }

    @Operation(summary = ApiConstants.UpdateNumber)
    @PatchMapping(ApiConstants.updateNumber)
    public ResponseEntity<Void> updateNumber(@PathVariable Long id, @RequestParam("number") String number) {
        orderService.updateNumber(id, number.trim());
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateDeliveryDate)
    public ResponseEntity<Void> updateDeliveryDate(@PathVariable Long id, @RequestParam("deliveryDate") LocalDateTime deliveryDate) {
        orderService.updateDeliveryDate(id, deliveryDate);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam(value = "lastModifiedDate", required = false) LocalDateTime lastModifiedDate, @RequestParam(value = "rejectReason", required = false) OrderRejectReason rejectReason) {
        return ResponseEntity.ok(orderService.updateStatus(id, new OrderStatusUpdateDTO(OrderStatus.valueOf(status), lastModifiedDate, rejectReason)));
    }

    @Operation(summary = "Multiple order update status")
    @PatchMapping("/status/multiple")
    public ResponseEntity<Void> updateStatusMultiple(@RequestBody List<OrderUpdateStatusDTO> orders, @RequestParam("status") String status) {
        orderService.updateStatus(orders, OrderStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<OrderListDTO>> getList(@ParameterObject OrderFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(orderService.getList(filter));
    }

    @GetMapping("/agent-tracking")
    public ResponseEntity<List<AgentTrackingOrderListDTO>> getListForAgentTracking(@RequestParam List<Long> orderIds) {
        return ResponseEntity.ok(orderService.getListForAgentTracking(orderIds));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject OrderFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(orderService.lookUp(filter));
    }

    @Operation(summary = "Product Lookup")
    @GetMapping("/product/lookup")
    public ResponseEntity<List<ProductLookUpWithWarehouseQtyDTO>> getProductLookUp(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setEnvironmentType(EnvironmentType.WEB.getCode());
        return ResponseEntity.ok(orderService.getProductLookUp(filter));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<OrderStatisticsDTO> getStatistics(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getStatistics(filter));
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> exportCsv(@ParameterObject OrderFilter filter) {
        orderListStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> exportExcel(@ParameterObject OrderFilter filter) {
        orderListStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.pdf)
    public ResponseEntity<?> getPDF(@ParameterObject OrderFilter filter) {
        return orderService.getPDF(filter);
    }

    @Operation(summary = ApiConstants.Import)
    @PostMapping(value = ApiConstants.importStr, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importOrders(@RequestPart("file") MultipartFile file, @RequestPart(name = "importReportDTO", required = false) OrderImportReportDTO importReportDTO, @RequestParam(name = "jsonString", required = false) String jsonString) {
        orderImportReportExcelHandler.importReport(file, EntityTypeEnum.ORDER, FileType.EXCEL, jsonString, importReportDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Template)
    @GetMapping("/download-template")
    public ResponseEntity<Resource> getTemplate() {
        return orderImportTemplateExcelHandler.download();
    }

    @Operation(summary = ApiConstants.Template)
    @GetMapping("/download-template/batch")
    public ResponseEntity<Resource> getTemplateForBatch() {
        return orderBatchImportTemplateExcelHandler.download();
    }

    @Operation(summary = "Assign expeditor")
    @PatchMapping("/assign/expeditor/{id}")
    public ResponseEntity<Long> assignExpeditor(@PathVariable Long id, @RequestParam(name = "expeditorId") Long expeditorId) {
        orderService.assignExpeditor(id, expeditorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product-qty-details")
    public ResponseEntity<Page<OrderProductQtyDetailListDTO>> getProductQtyDetails(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getProductQtyDetails(filter));
    }

    @PostMapping("/product-expeditor/report")
    public ResponseEntity<ProductExpeditorResponseDTO> generateProductExpeditorReport(@RequestBody ProductExpeditorReportCreateDTO productExpeditorReportCreateDTO) {
        return ResponseEntity.ok(orderService.generateProductExpeditorReport(productExpeditorReportCreateDTO));
    }

    @GetMapping("/transfer")
    public ResponseEntity<List<OrderListForTransferDTO>> getListForTransfer(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getListForTransfer(filter));
    }

    @GetMapping("/sync")
    public ResponseEntity<Void> sync() {
        orderService.syncPlanRoutes();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/product-expeditor" + ApiConstants.exportExcel)
    public ResponseEntity<Resource> getProductExpeditorReportExcel(@RequestBody ProductExpeditorReportCreateDTO productExpeditorReportCreateDTO) {
        orderProductExpeditorStrategy.generate(productExpeditorReportCreateDTO, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expeditor-report")
    public ResponseEntity<Page<OrderExpeditorReportDTO>> getOrderExpeditorReport(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getOrderExpeditorReport(filter));
    }

    @GetMapping("/expeditor-report/total-qty")
    public ResponseEntity<OrderExpeditorTotalQtyReportDTO> getOrderExpeditorTotalQtyReport(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getOrderExpeditorTotalQtyReport(filter));
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping("/expeditor-report" + ApiConstants.exportExcel)
    public ResponseEntity<Resource> orderExpeditorReportExportToExcel(@ParameterObject OrderFilter filter) {
        orderExpeditorReportStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sales-report")
    public ResponseEntity<Page<SalesAndCustomerReportDTO>> getSalesAndCustomerReport(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getSalesAndCustomerReport(filter));
    }

    @GetMapping("/sales-report/summary")
    public ResponseEntity<SalesAndCustomerSummaryDTO> getSalesAndCustomerSummary(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getSalesAndCustomerSummary(filter));
    }

    @GetMapping("/sales-customer" + ApiConstants.exportExcel)
    public ResponseEntity<Resource> salesAndCustomerReportExportToExcel(@ParameterObject OrderFilter filter) {
        salesAndCustomerStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign-trips")
    public ResponseEntity<Void> assignTripToOrders(@Valid @RequestBody OrderTripDTO orderTripDTO) {
        orderService.assignTripToOrders(orderTripDTO);
        return ResponseEntity.ok().build();
    }
}