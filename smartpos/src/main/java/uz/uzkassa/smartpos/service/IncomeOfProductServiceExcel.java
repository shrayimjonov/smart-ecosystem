package uz.uzkassa.smartpos.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.dto.ValidationField;
import uz.uzkassa.smartpos.dto.warehouse.IncomeFromExcelDto;
import uz.uzkassa.smartpos.dto.warehouse.v2.IncomeOfProductCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.v2.IncomeOfProductDetailCreateDto;
import uz.uzkassa.smartpos.enums.FieldValidation;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.common.UnitRepository;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class IncomeOfProductServiceExcel extends BaseService {
    private final UnitRepository unitRepository;
    private final BranchRepository branchRepository;
    private final ProductBranchRepository productBranchRepository;
    private final IncomeOfProductServiceV2 incomeOfProductServiceV2;

    @Transactional
    public ByteArrayResource importFromExcel(Long branchId, MultipartFile file) throws IOException {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(notFound(localizationService.getMessage("branch")));
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Map<String, Integer> headers = new HashMap<>();
        Iterator<Row> rowIterator = sheet.iterator();
        Row header = rowIterator.next();
        DataFormatter dataFormatter = new DataFormatter();
        header.forEach(cell -> {
            if (IncomeOfProductHeader.getHeaders().contains(dataFormatter.formatCellValue(cell))) {
                headers.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            } else {
                throw badRequest(localizationService.getMessage("header.incorrect", cell.getStringCellValue())).get();
            }
        });
        if (headers.size() != IncomeOfProductHeader.getHeaders().size()) {
            throw badRequest(localizationService.getMessage("file.must.contains.all.headers", String.join(",", IncomeOfProductHeader.getHeaders()))).get();
        }
        Map<String, Unit> unitMap = unitRepository.findAll().stream().collect(Collectors.toMap(Unit::getDescription, Function.identity()));
        List<IncomeFromExcelDto> dtos = new ArrayList<>();
        rowIterator.forEachRemaining(row -> dtos.add(IncomeFromExcelDto.builder()
                .productName(dataFormatter.formatCellValue(row.getCell(headers.get(IncomeOfProductHeader.PRODUCT_NAME))).trim())
                .qty(dataFormatter.formatCellValue(row.getCell(headers.get(IncomeOfProductHeader.QTY))).trim())
                .unit(dataFormatter.formatCellValue(row.getCell(headers.get(IncomeOfProductHeader.UNIT))).trim())
                .costPrice(dataFormatter.formatCellValue(row.getCell(headers.get(IncomeOfProductHeader.COST_PRICE))).trim())
                .salesPrice(dataFormatter.formatCellValue(row.getCell(headers.get(IncomeOfProductHeader.SALES_PRICE))).trim())
                .build()));
        List<String> productNames = dtos.stream().map(IncomeFromExcelDto::getProductName).toList();
        List<ProductBranch> products = productBranchRepository.findAllByBranchIdAndCityIdAndNameInAndDeletedIsFalse(branchId, branch.getCityKeyId(), productNames);
        Map<String, Pair<Integer, ProductBranch>> productBranchMap = products.stream()
                .collect(Collectors.toMap(ProductBranch::getName,
                        productBranch -> Pair.of(1, productBranch),
                        (pair, pair2) -> Pair.of(2, pair.getSecond())));
        List<IncomeValidationDto> list = dtos.stream().map(incomeFromExcelDto -> {
            IncomeValidationDto validationDto = new IncomeValidationDto();
            setProduct(validationDto, incomeFromExcelDto.getProductName(), productBranchMap);
            setQty(validationDto, incomeFromExcelDto.getQty());
            setUnit(validationDto, incomeFromExcelDto.getUnit(), unitMap);
            setCostPrice(validationDto, incomeFromExcelDto.getCostPrice());
            setSalesPrice(validationDto, incomeFromExcelDto.getSalesPrice());
            return validationDto;
        }).toList();
        if (list.stream().allMatch(IncomeValidationDto::isValid)) {
            IncomeOfProductCreateDto<IncomeOfProductDetailCreateDto> incomeDto = new IncomeOfProductCreateDto<>();
            incomeDto.setIncomeDate(LocalDateTime.now());
            incomeDto.setDocumentNo(file.getOriginalFilename());
            incomeDto.setDescription(file.getOriginalFilename());
            incomeDto.setToBranchId(branchId);
            incomeDto.setIncomeOfProductDetails(list.stream().map(validationDto -> {
                var detail = new IncomeOfProductDetailCreateDto();
                detail.setProductId(validationDto.getProductId().getValue());
                detail.setQty(validationDto.getQty().getValue());
                detail.setUnitId(validationDto.getUnit().getValue());
                detail.setCostPrice(validationDto.getCostPrice().getValue());
                detail.setSalesPrice(validationDto.getSalesPrice().getValue());
                detail.setMarkup(detail.getSalesPrice().subtract(detail.getCostPrice()).divide(detail.getCostPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
                return detail;
            }).toList());
            incomeOfProductServiceV2.create(incomeDto, null);
            return null;
        } else {
            try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
                XSSFSheet reportSheet = xssfWorkbook.createSheet();
                var drawing = reportSheet.createDrawingPatriarch();
                var factory = xssfWorkbook.getCreationHelper();
                var anchor = factory.createClientAnchor();
                var marker = xssfWorkbook.createCellStyle();
                Row row = reportSheet.createRow(0);
                createCell(row, 0, IncomeOfProductHeader.PRODUCT_NAME);
                createCell(row, 1, IncomeOfProductHeader.QTY);
                createCell(row, 2, IncomeOfProductHeader.UNIT);
                createCell(row, 3, IncomeOfProductHeader.COST_PRICE);
                createCell(row, 4, IncomeOfProductHeader.SALES_PRICE);

                AtomicInteger rowCount = new AtomicInteger(1);
                list.stream().filter(incomeValidationDto -> !incomeValidationDto.isValid()).forEach(validationDto -> {
                    Row row1 = reportSheet.createRow(rowCount.get());
                    createCell(row1, 0, validationDto.getProductId(), marker, drawing, anchor, factory);
                    createCell(row1, 1, validationDto.getQty(), marker, drawing, anchor, factory);
                    createCell(row1, 2, validationDto.getUnit(), marker, drawing, anchor, factory);
                    createCell(row1, 3, validationDto.getCostPrice(), marker, drawing, anchor, factory);
                    createCell(row1, 4, validationDto.getSalesPrice(), marker, drawing, anchor, factory);
                    rowCount.getAndIncrement();
                });
                for (int i = 0; i < 4; i++) {
                    reportSheet.autoSizeColumn(i);
                }
                xssfWorkbook.write(bos);
                return new ByteArrayResource(bos.toByteArray());
            }
        }
    }


    private static void createCell(Row row, int column, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
    }

    private static void createCell(Row row, int column, ValidationField<?> value, CellStyle marker, XSSFDrawing drawing, ClientAnchor anchor, CreationHelper factory) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value.getWrongStringValue());
        if (!value.isValid()) {
            cell.setCellStyle(marker);
            var comment = drawing.createCellComment(anchor);
            comment.setString(factory.createRichTextString(value.getValidation().getNameRu()));
            cell.setCellComment(comment);
        }
    }

    private void setSalesPrice(IncomeValidationDto validationDto, String salesPrice) {
        if (StringUtils.isBlank(salesPrice)) {
            validationDto.setSalesPrice(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            try {
                BigDecimal price = new BigDecimal(salesPrice);
                if (price.compareTo(BigDecimal.ZERO) >= 0) {
                    validationDto.setSalesPrice(new ValidationField<>(price, salesPrice, FieldValidation.VALID));
                } else {
                    validationDto.setSalesPrice(new ValidationField<>(null, salesPrice, FieldValidation.NOT_VALID_FORMAT_ERROR));
                }
            } catch (Exception e) {
                validationDto.setSalesPrice(new ValidationField<>(null, salesPrice, FieldValidation.NOT_VALID_FORMAT_ERROR));
            }
        }

    }

    private void setCostPrice(IncomeValidationDto validationDto, String costPrice) {
        if (StringUtils.isBlank(costPrice)) {
            validationDto.setCostPrice(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            try {
                BigDecimal price = new BigDecimal(costPrice);
                if (price.compareTo(BigDecimal.ZERO) > 0) {
                    validationDto.setCostPrice(new ValidationField<>(price, costPrice, FieldValidation.VALID));
                } else {
                    validationDto.setCostPrice(new ValidationField<>(null, costPrice, FieldValidation.NOT_VALID_FORMAT_ERROR));
                }
            } catch (Exception e) {
                validationDto.setCostPrice(new ValidationField<>(null, costPrice, FieldValidation.NOT_VALID_FORMAT_ERROR));
            }
        }
    }

    private void setUnit(IncomeValidationDto validationDto, String unit, Map<String, Unit> unitMap) {
        if (StringUtils.isBlank(unit)) {
            validationDto.setUnit(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            String unitLowerCase = unit.toLowerCase();
            if (!unitMap.containsKey(unitLowerCase)) {
                validationDto.setUnit(new ValidationField<>(null, unit, FieldValidation.UNIT_ERROR));
            } else {
                validationDto.setUnit(new ValidationField<>(unitMap.get(unitLowerCase).getId(), unit, FieldValidation.VALID));
            }
        }
    }

    private void setQty(IncomeValidationDto validationDto, String qty) {
        if (StringUtils.isBlank(qty)) {
            validationDto.setQty(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            try {
                BigDecimal qtyBigDecimal = new BigDecimal(qty);
                if (qtyBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                    validationDto.setQty(new ValidationField<>(qtyBigDecimal, qty, FieldValidation.VALID));
                } else {
                    validationDto.setQty(new ValidationField<>(null, null, FieldValidation.NOT_VALID_FORMAT_ERROR));
                }
            } catch (Exception e) {
                validationDto.setQty(new ValidationField<>(null, qty, FieldValidation.NOT_VALID_FORMAT_ERROR));
            }
        }
    }

    private void setProduct(IncomeValidationDto validationDto, String productName, Map<String, Pair<Integer, ProductBranch>> productBranchMap) {
        if (StringUtils.isBlank(productName)) {
            validationDto.setProductId(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            if (productBranchMap.containsKey(productName)) {
                if (productBranchMap.get(productName).getFirst() != 1) {
                    validationDto.setProductId(new ValidationField<>(null, productName, FieldValidation.PRODUCT_DUPLICATE));
                } else {
                    validationDto.setProductId(new ValidationField<>(productBranchMap.get(productName).getSecond().getProductId(), productName, FieldValidation.VALID));
                }
            } else {
                validationDto.setProductId(new ValidationField<>(null, productName, FieldValidation.PRODUCT_NOT_FOUND));
            }
        }
    }

    public ByteArrayResource getTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); var outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Income_of_Product");
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue(IncomeOfProductHeader.PRODUCT_NAME);
            sheet.autoSizeColumn(0, true);
            CellStyle cellStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            cellStyle.setDataFormat(dataFormat.getFormat("@"));
            sheet.setDefaultColumnStyle(0, cellStyle);

            cell = row.createCell(1);
            cell.setCellValue(IncomeOfProductHeader.QTY);
            sheet.autoSizeColumn(1, true);
            sheet.setDefaultColumnStyle(1, cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(IncomeOfProductHeader.UNIT);
            sheet.autoSizeColumn(2, true);
            sheet.setDefaultColumnStyle(2, cellStyle);
            List<Unit> units = unitRepository.findAll();
            insertUnits(workbook, units);


            cell = row.createCell(3);
            cell.setCellValue(IncomeOfProductHeader.COST_PRICE);
            sheet.autoSizeColumn(3, true);
            sheet.setDefaultColumnStyle(3, cellStyle);

            cell = row.createCell(4);
            cell.setCellValue(IncomeOfProductHeader.SALES_PRICE);
            sheet.autoSizeColumn(4, true);
            sheet.setDefaultColumnStyle(4, cellStyle);


            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertUnits(XSSFWorkbook workbook, List<Unit> units) {
        XSSFSheet sheet = workbook.createSheet("Unit");
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";

        int r = 0;
        for (Unit key : units) {
            row = sheet.getRow(r);
            if (row == null)
                row = sheet.createRow(r);
            row.createCell(0).setCellValue(key.getDescription());
            if (r == 0) {
                prefilledValue = key.getDescription();
            }
            r++;
        }

        namedRange = workbook.createName();
        namedRange.setNameName("Units");

        String colLetter = CellReference.convertNumToColString(0);

        reference = "Unit!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        sheet.setSelected(false);

        sheet = workbook.getSheet("Income_of_Product");
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(2);
        if (cell == null)
            cell = row.createCell(2);
        cell.setCellValue(prefilledValue);

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint("Units");
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 2, 2);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        workbook.setActiveSheet(0);
    }

    private static class IncomeOfProductHeader {
        private static final String PRODUCT_NAME = "Название товара";
        private static final String QTY = "Количество";
        private static final String UNIT = "Ед. изм-я";
        private static final String COST_PRICE = "Себестоимость";
        private static final String SALES_PRICE = "Продажная цена";

        private static List<String> getHeaders() {
            return List.of(PRODUCT_NAME, QTY, UNIT, COST_PRICE, SALES_PRICE);
        }
    }

    @Getter
    @Setter
    public static class IncomeValidationDto {
        private ValidationField<Long> productId;
        private ValidationField<BigDecimal> qty;
        private ValidationField<Long> unit;
        private ValidationField<BigDecimal> costPrice;
        private ValidationField<BigDecimal> salesPrice;

        public boolean isValid() {
            return productId.isValid() && qty.isValid() && unit.isValid() && costPrice.isValid() && salesPrice.isValid();
        }
    }
}
