package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranchId;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesDynamicsFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.report.*;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepository;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportResource {
    private final ReceiptExcelReport receiptExcelReport;
    private final ShiftExcelReport shiftExcelReport;
    private final ProductSalesExcelReport productSalesExcelReport;
    private final ProductSalesDynamicExcelReport productSalesDynamicExcelReport;
    private final CategorySalesExcelReport categorySalesExcelReport;
    private final ProductImportTemplateXls productImportTemplateXls;
    private final CategoryBranchRepository categoryBranchRepository;

    @GetMapping(value = "/report/receipts/excel")
    public ResponseEntity<Resource> getReceiptReport(@ParameterObject ReceiptFilter filter) throws IOException {
        return ServerUtils.getResponseEntity(receiptExcelReport.getExcelReport(filter, false), "Чеки_Smartpos");
    }

    @GetMapping(value = "/report/shift/excel")
    public ResponseEntity<Resource> getShiftReport(@ParameterObject BaseFilter filter) throws IOException {
        return ServerUtils.getResponseEntity(shiftExcelReport.getExcelReport(filter), "Отчет_по_сменам_Smartpos");
    }

    @GetMapping(value = "/report/sales-stats/products/excel")
    public ResponseEntity<Resource> getProductSalesReport(@ParameterObject SalesStatsFilter filter) throws IOException {
        return ServerUtils.getResponseEntity(productSalesExcelReport.getExcelReport(filter), "Отчет_по_продаже_товаров_Smartpos");
    }

    @GetMapping(value = "/report/sales-dynamics/product/excel")
    public ResponseEntity<Resource> getProductSalesDynamicReport(@ParameterObject SalesDynamicsFilter filter) throws IOException {
        if (filter.getFrom() == null || filter.getTo() == null || filter.getProductId() == null || filter.getGranularity() == null) {
            throw new ValidationException("Please specify from, to, granularity and productId");
        }
        return ServerUtils.getResponseEntity(productSalesDynamicExcelReport.getExcelReport(filter), "Отчет_по_товару_Smartpos");
    }

    @GetMapping(value = "/report/sales-stats/categories/excel")
    public ResponseEntity<Resource> getCategorySalesReport(@ParameterObject SalesStatsFilter filter) throws IOException {
        return ServerUtils.getResponseEntity(categorySalesExcelReport.getExcelReport(filter), "Отчет_по_категориям_Smartpos");
    }

    @GetMapping(value = "/product_excel_template")
    public ResponseEntity<Resource> getProductTemplate(@RequestParam(required = false) Long branchId,
                                                       @RequestParam(required = false) Long categoryId) throws IOException {
        String fileName = "Шаблон_" + Optional.ofNullable(categoryId).flatMap(id -> categoryBranchRepository.findById(new CategoryBranchId(id, branchId)))
                .map(CategoryBranch::getName).orElse("продуктов");
        return ServerUtils.getResponseEntity(productImportTemplateXls.getBaos(branchId, categoryId), fileName);
    }

}
