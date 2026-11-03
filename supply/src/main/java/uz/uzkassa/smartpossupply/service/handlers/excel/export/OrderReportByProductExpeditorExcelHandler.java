package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.order.ProductExpeditorReportCreateDTO;
import uz.uzkassa.smartpossupply.dto.order.ProductExpeditorResponseDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductExpeditorListDTO;
import uz.uzkassa.smartpossupply.service.OrderService;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE)
@RequiredArgsConstructor
public class OrderReportByProductExpeditorExcelHandler extends AbstractExcelHandler<ProductExpeditorReportCreateDTO> {

    final OrderService orderService;

    private ProductExpeditorResponseDTO response;

    @Override
    public String getFileName(ProductExpeditorReportCreateDTO productExpeditorReportCreateDTO) {
        return "ProductExpeditorReport.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> headers = new LinkedList<>();
        headers.add(Optional.ofNullable(response.getExpeditorName()).orElse("")
            + " / "
            + Optional.ofNullable(response.getAgentName()).orElse(""));

        response.getProducts().stream()
            .map(p -> p.getProduct().getShortName())
            .distinct()
            .forEach(headers::add);

        headers.add("СУММА");
        headers.add("ТИП");

        return headers;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(ProductExpeditorReportCreateDTO productExpeditorReportCreateDTO) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(productExpeditorReportCreateDTO));
        response = orderService.generateProductExpeditorReport(productExpeditorReportCreateDTO);
        LinkedList<String> headers = getHeaders();
        createHeader(headers, sheet);
        List<ProductExpeditorListDTO> validProducts = response.getProducts().stream().filter(product -> product.getCustomerBranch() != null).toList();

        Map<String, Integer> colIndex = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            colIndex.put(headers.get(i), i);
        }
        Map<String, List<ProductExpeditorListDTO>> grouped = validProducts.stream()
                .collect(Collectors.groupingBy(
                        product -> product.getCustomerBranch().getName(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        int rowIndex = 0;
        for (Map.Entry<String, List<ProductExpeditorListDTO>> entry : grouped.entrySet()) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(entry.getKey());
            Map<String, BigDecimal> productQtyMap = new LinkedHashMap<>();
            Map<String, BigDecimal> productQtyInPackageMap = new LinkedHashMap<>();
            BigDecimal branchTotalPrice = BigDecimal.ZERO;
            String paymentType = null;
            for (ProductExpeditorListDTO productDTO : entry.getValue()) {
                String shortName = productDTO.getProduct().getShortName();
                String productName = (shortName == null || shortName.trim().isEmpty())
                        ? productDTO.getProduct().getName()
                        : shortName;
                if (productDTO.getQty() != null) {
                    productQtyMap.merge(productName, productDTO.getQty(), BigDecimal::add);
                }
                if (!productQtyInPackageMap.containsKey(productName) && productDTO.getProduct().getQtyInPackage() != null) {
                    productQtyInPackageMap.put(productName, productDTO.getProduct().getQtyInPackage());
                }
                if (productDTO.getTotalPrice() != null) {
                    branchTotalPrice = branchTotalPrice.add(productDTO.getTotalPrice());
                }
                if (paymentType == null && productDTO.getPaymentType() != null) {
                    paymentType = productDTO.getPaymentType().getName();
                }
            }
            for (Map.Entry<String, BigDecimal> prodEntry : productQtyMap.entrySet()) {
                Integer col = colIndex.get(prodEntry.getKey());
                if (col != null) {
                    BigDecimal qty = prodEntry.getValue();
                    BigDecimal qtyInPackage = productQtyInPackageMap.get(prodEntry.getKey());
                    if (qty.signum() == 0) {
                        row.createCell(col).setCellValue("-");
                    } else if (qtyInPackage != null && qtyInPackage.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal[] divisionResult = qty.divideAndRemainder(qtyInPackage);
                        BigDecimal quotient = divisionResult[0];
                        BigDecimal remainder = divisionResult[1];
                        if (remainder.compareTo(BigDecimal.ZERO) == 0) {
                            row.createCell(col).setCellValue(quotient.doubleValue());
                        } else {
                            String cellValue = quotient.stripTrailingZeros().toPlainString()
                                    + "-"
                                    + remainder.stripTrailingZeros().toPlainString();
                            row.createCell(col).setCellValue(cellValue);
                        }
                    } else {
                        row.createCell(col).setCellValue(qty.doubleValue());
                    }
                }
            }

            row.createCell(headers.size() - 2).setCellValue(branchTotalPrice.doubleValue());
            row.createCell(headers.size() - 1).setCellValue(paymentType != null ? paymentType : "-");
        }

        Row totalRow = sheet.createRow(++rowIndex);
        totalRow.createCell(0).setCellValue("ИТОГО");

        for (int col = 1; col < headers.size() - 2; col++) {
            double colSum = 0;
            boolean hasNumericValue = false;

            for (int r = 1; r < rowIndex; r++) {
                Row dataRow = sheet.getRow(r);
                if (dataRow != null && dataRow.getCell(col) != null) {
                    if (dataRow.getCell(col).getCellType() == CellType.NUMERIC) {
                        colSum += dataRow.getCell(col).getNumericCellValue();
                        hasNumericValue = true;
                    } else if (dataRow.getCell(col).getCellType() == CellType.STRING) {
                        String cellValue = dataRow.getCell(col).getStringCellValue();
                        if (cellValue.contains("-")) {
                            String[] parts = cellValue.split("-");
                            if (parts.length == 2) {
                                try {
                                    double quotient = Double.parseDouble(parts[0].trim());
                                    colSum += quotient;
                                    hasNumericValue = true;
                                } catch (NumberFormatException e) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                }
            }
            if (hasNumericValue && colSum > 0) {
                totalRow.createCell(col).setCellValue(colSum);
            } else {
                totalRow.createCell(col).setCellValue("-");
            }
        }

        double totalPriceSum = 0;
        for (int r = 1; r < rowIndex; r++) {
            Row dataRow = sheet.getRow(r);
            if (dataRow != null && dataRow.getCell(headers.size() - 2) != null) {
                if (dataRow.getCell(headers.size() - 2).getCellType() == CellType.NUMERIC) {
                    totalPriceSum += dataRow.getCell(headers.size() - 2).getNumericCellValue();
                }
            }
        }
        totalRow.createCell(headers.size() - 2).setCellValue(totalPriceSum);
        totalRow.createCell(headers.size() - 1).setCellValue("-");

        return workbook;
    }
}
