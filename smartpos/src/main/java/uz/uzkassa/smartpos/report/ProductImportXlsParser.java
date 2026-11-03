package uz.uzkassa.smartpos.report;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranchId;
import uz.uzkassa.smartpos.domain.postgres.catalog.Mxik;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.common.Vat;
import uz.uzkassa.smartpos.dto.ValidationField;
import uz.uzkassa.smartpos.dto.catalog.ProductValidationDto;
import uz.uzkassa.smartpos.dto.filter.ImportProductsFilter;
import uz.uzkassa.smartpos.enums.FieldValidation;
import uz.uzkassa.smartpos.enums.ProductFileHeader;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.MxikRepository;
import uz.uzkassa.smartpos.repository.postgres.common.UnitRepository;
import uz.uzkassa.smartpos.repository.postgres.common.VatRepository;
import uz.uzkassa.smartpos.service.SmartposService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
@Lazy
@Slf4j
public class ProductImportXlsParser extends SmartposService {
    private final CategoryBranchRepository categoryBranchRepository;
    private final UnitRepository unitRepository;
    private final VatRepository vatRepository;
    private final MxikRepository mxikRepository;
    private final RabbitMqProducer rabbitMqProducer;

    public ByteArrayResource importProducts(MultipartFile file, ImportProductsFilter filter) throws IOException {
        final var dataFormatter = new DataFormatter();
        var workbook = WorkbookFactory.create(file.getInputStream());
        var sheet = workbook.getSheetAt(0);
        var iterator = sheet.iterator();
        var headersMap = parseHeader(iterator.next());
        EnumSet<ProductFileHeader> requiredHeaders;
        if (filter.getCategoryId() != null) {
            requiredHeaders = EnumSet.complementOf(EnumSet.of(ProductFileHeader.CATEGORY, ProductFileHeader.BALANCE));
        } else {
            requiredHeaders = EnumSet.complementOf(EnumSet.of(ProductFileHeader.BALANCE));
        }
        validateHeaders(headersMap, requiredHeaders);
        final var branchId = filter.getBranchId() != null ? filter.getBranchId() : getCurrentBranchId();
        Map<String, CategoryBranch> categoryMap = new HashMap<>();
        CategoryBranch categoryBranch = null;
        if (filter.getCategoryId() != null) {
            categoryBranch = categoryBranchRepository.findById(new CategoryBranchId(filter.getCategoryId(), branchId))
                    .filter(CategoryBranch::isEnabled)
                    .orElseThrow(notFound(localizationService.getMessage("category")));
        } else {
            categoryMap = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(branchId)
                    .stream().collect(Collectors.toMap(category -> category.getName().toLowerCase(), Function.identity()));
        }
        var unitMap = unitRepository.findAll().stream().collect(Collectors.toMap(unit -> unit.getDescription().toLowerCase(), Function.identity()));
        var vatSet = vatRepository.findAll().stream().map(Vat::getPercent).collect(Collectors.toSet());
        var productValidations = new ArrayList<ProductValidationDto>();
        while (iterator.hasNext()) {
            var row = iterator.next();
            var validationDto = new ProductValidationDto();
            if (filter.getCategoryId() != null) {
                validationDto.setCategory(new ValidationField<>(filter.getCategoryId(), categoryBranch != null ? categoryBranch.getName() : "", FieldValidation.VALID));
            } else {
                String category = dataFormatter.formatCellValue(row.getCell(headersMap.get(ProductFileHeader.CATEGORY))).trim();
                setCategory(category, validationDto, categoryMap);
            }
            String name = dataFormatter.formatCellValue(row.getCell(headersMap.get(ProductFileHeader.PRODUCT_NAME))).trim();
            setName(name, validationDto);
            String barcode = dataFormatter.formatCellValue(row.getCell(headersMap.get(ProductFileHeader.BARCODE))).trim();
            setBarcode(barcode, validationDto);
            String unitName = dataFormatter.formatCellValue(row.getCell(headersMap.get(ProductFileHeader.UNIT))).trim();
            setUnit(unitName, validationDto, unitMap);
            String price = dataFormatter.formatCellValue(row.getCell(headersMap.get(ProductFileHeader.PRICE))).trim();
            setPrice(price, validationDto);
            String vat = dataFormatter.formatCellValue(row.getCell(headersMap.get(ProductFileHeader.VAT))).trim();
            setVat(vat, validationDto, vatSet);
            String favorite = dataFormatter.formatCellValue(row.getCell(headersMap.get(ProductFileHeader.FAVORITE))).trim();
            setFavorite(favorite, validationDto);
            String hasMark = dataFormatter.formatCellValue(row.getCell(headersMap.get(ProductFileHeader.MARK))).trim();
            setHasMark(hasMark, validationDto);
            String vatBarcode = dataFormatter.formatCellValue(row.getCell(headersMap.get(ProductFileHeader.VAT_BARCODE))).trim();
            setVatBarcode(vatBarcode, validationDto);
            productValidations.add(validationDto);
        }
        validateVatBarcodes(productValidations);
        var wrongProducts = new ArrayList<ProductValidationDto>();
        AtomicInteger i = new AtomicInteger();
        productValidations.forEach(validationDto -> {
            if (validationDto.isValid()) {
                rabbitMqProducer.send(validationDto.toCreateDto(branchId), MqConstants.PRODUCT_CREATE_QUEUE);
                i.incrementAndGet();
            } else {
                wrongProducts.add(validationDto);
            }
        });
        if (CollectionUtils.isEmpty(wrongProducts)) {
            return null;
        }
        return backReportWithWrongProducts(headersMap, wrongProducts);

    }

    private @NotNull ByteArrayResource backReportWithWrongProducts(Map<ProductFileHeader, Integer> headersMap, ArrayList<ProductValidationDto> wrongProducts) throws IOException {
        try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(); var outputStream = new ByteArrayOutputStream()) {
            var reportSheet = xssfWorkbook.createSheet();
            var drawing = reportSheet.createDrawingPatriarch();
            var factory = xssfWorkbook.getCreationHelper();
            var anchor = factory.createClientAnchor();
            var marker = xssfWorkbook.createCellStyle();
            marker.setFillForegroundColor(IndexedColors.CORAL.getIndex());
            marker.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            AtomicInteger i = new AtomicInteger();
            Row headerRow = reportSheet.createRow(i.getAndIncrement());
            headersMap.forEach((key, value) -> headerRow.createCell(value).setCellValue(key != null ? key.getNameRu() : null));
            wrongProducts.forEach(validationDto -> {
                var row = reportSheet.createRow(i.getAndIncrement());
                var cell = row.createCell(headersMap.get(ProductFileHeader.PRODUCT_NAME));
                cell.setCellValue(validationDto.getName().getWrongStringValue());
                createComment(validationDto.getName(), cell, marker, drawing, anchor, factory);

                cell = row.createCell(headersMap.get(ProductFileHeader.BARCODE));
                cell.setCellValue(validationDto.getBarcode().getWrongStringValue());
                createComment(validationDto.getBarcode(), cell, marker, drawing, anchor, factory);

                cell = row.createCell(headersMap.get(ProductFileHeader.VAT_BARCODE));
                cell.setCellValue(validationDto.getVatBarcode().getWrongStringValue());
                createComment(validationDto.getVatBarcode(), cell, marker, drawing, anchor, factory);

                if (validationDto.getCategory().getValidation() != FieldValidation.VALID) {
                    cell = row.createCell(headersMap.get(ProductFileHeader.CATEGORY));
                    cell.setCellValue(validationDto.getCategory().getWrongStringValue());
                    createComment(validationDto.getCategory(), cell, marker, drawing, anchor, factory);
                }

                cell = row.createCell(headersMap.get(ProductFileHeader.UNIT));
                cell.setCellValue(validationDto.getUnit().getWrongStringValue());
                createComment(validationDto.getUnit(), cell, marker, drawing, anchor, factory);

                cell = row.createCell(headersMap.get(ProductFileHeader.PRICE));
                cell.setCellValue(validationDto.getSalesPrice().getWrongStringValue());
                createComment(validationDto.getSalesPrice(), cell, marker, drawing, anchor, factory);

                cell = row.createCell(headersMap.get(ProductFileHeader.VAT));
                cell.setCellValue(validationDto.getVat().getWrongStringValue());
                createComment(validationDto.getVat(), cell, marker, drawing, anchor, factory);

                cell = row.createCell(headersMap.get(ProductFileHeader.FAVORITE));
                cell.setCellValue(validationDto.getFavorite().getWrongStringValue());
                createComment(validationDto.getFavorite(), cell, marker, drawing, anchor, factory);

                cell = row.createCell(headersMap.get(ProductFileHeader.MARK));
                cell.setCellValue(validationDto.getHasMark().getWrongStringValue());
                createComment(validationDto.getHasMark(), cell, marker, drawing, anchor, factory);
            });
            xssfWorkbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    private static void createComment(ValidationField<?> validationField, XSSFCell cell, CellStyle marker, XSSFDrawing drawing, ClientAnchor anchor, CreationHelper factory) {
        if (!validationField.isValid()) {
            cell.setCellStyle(marker);
            var comment = drawing.createCellComment(anchor);
            comment.setString(factory.createRichTextString(validationField.getValidation().getNameRu()));
            cell.setCellComment(comment);
        }
    }

    private void validateVatBarcodes(ArrayList<ProductValidationDto> productValidations) {
        List<String> vatBarcodes = productValidations.stream().map(validationDto -> validationDto.getVatBarcode().getValue()).toList();
        var mxikSet = new HashSet<String>();
        Lists.partition(vatBarcodes, 5000).forEach(codes -> mxikSet.addAll(mxikRepository.findAllByMxikIn(codes).stream().map(Mxik::getMxik).toList()));
        productValidations.forEach(validationDto -> {
            if (!mxikSet.contains(validationDto.getVatBarcode().getValue())) {
                validationDto.getVatBarcode().setValidation(FieldValidation.MXIK_ERROR);
            }
        });
    }

    private void setName(String name, ProductValidationDto validationDto) {
        if (StringUtils.isBlank(name)) {
            validationDto.setName(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            validationDto.setName(new ValidationField<>(name, name, FieldValidation.VALID));
        }
    }

    private void setBarcode(String barcode, ProductValidationDto validationDto) {
        if (StringUtils.isBlank(barcode)) {
            validationDto.setBarcode(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            validationDto.setBarcode(new ValidationField<>(barcode, barcode, FieldValidation.VALID));
        }
    }

    private void setUnit(String unit, ProductValidationDto validationDto, Map<String, Unit> unitMap) {
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


    private void setPrice(String price, ProductValidationDto validationDto) {
        if (StringUtils.isBlank(price)) {
            validationDto.setSalesPrice(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            try {
                BigDecimal salesPrice = new BigDecimal(price);
                if (salesPrice.compareTo(BigDecimal.ZERO) >= 0) {
                    validationDto.setSalesPrice(new ValidationField<>(salesPrice, price, FieldValidation.VALID));
                } else {
                    validationDto.setSalesPrice(new ValidationField<>(null, price, FieldValidation.NOT_VALID_FORMAT_ERROR));
                }
            } catch (Exception e) {
                validationDto.setSalesPrice(new ValidationField<>(null, price, FieldValidation.NOT_VALID_FORMAT_ERROR));
            }
        }
    }

    private void setVat(String vat, ProductValidationDto validationDto, Set<BigDecimal> vatSet) {
        if (StringUtils.isBlank(vat)) {
            validationDto.setVat(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            if (vat.equalsIgnoreCase("Без НДС")) {
                validationDto.setVat(new ValidationField<>(null, vat, FieldValidation.VALID));
            } else {
                try {
                    BigDecimal vatPercent = new BigDecimal(vat);
                    if (vatSet.contains(vatPercent)) {
                        validationDto.setVat(new ValidationField<>(vatPercent, vatPercent, FieldValidation.VALID));
                    } else {
                        validationDto.setVat(new ValidationField<>(null, vat, FieldValidation.NOT_VALID_FORMAT_ERROR));
                    }
                } catch (Exception e) {
                    validationDto.setVat(new ValidationField<>(null, vat, FieldValidation.NOT_VALID_FORMAT_ERROR));
                }
            }
        }
    }

    private void setFavorite(String favorite, ProductValidationDto validationDto) {
        if (favorite.equalsIgnoreCase("да")) {
            validationDto.setFavorite(new ValidationField<>(true, favorite, FieldValidation.VALID));
        } else {
            validationDto.setFavorite(new ValidationField<>(false, favorite, FieldValidation.VALID));
        }
    }

    private void setHasMark(String hasMark, ProductValidationDto validationDto) {
        if (hasMark.equalsIgnoreCase("да")) {
            validationDto.setHasMark(new ValidationField<>(true, hasMark, FieldValidation.VALID));
        } else {
            validationDto.setHasMark(new ValidationField<>(false, hasMark, FieldValidation.VALID));
        }
    }

    private void setVatBarcode(String vatBarcode, ProductValidationDto validationDto) {
        if (StringUtils.isEmpty(vatBarcode)) {
            validationDto.setVatBarcode(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            validationDto.setVatBarcode(new ValidationField<>(vatBarcode, vatBarcode, FieldValidation.VALID));
        }
    }

    private void setCategory(String category, ProductValidationDto validationDto, Map<String, CategoryBranch> categoryMap) {
        if (StringUtils.isBlank(category)) {
            validationDto.setCategory(new ValidationField<>(null, null, FieldValidation.REQUIRED_FIELD_ERROR));
        } else {
            String categoryLowerCase = category.toLowerCase();
            if (!categoryMap.containsKey(categoryLowerCase)) {
                validationDto.setCategory(new ValidationField<>(null, category, FieldValidation.CATEGORY_NOT_FOUND_ERROR));
            } else {
                validationDto.setCategory(new ValidationField<>(categoryMap.get(categoryLowerCase).getCategoryId(), category, FieldValidation.VALID));
            }
        }
    }

    private Map<ProductFileHeader, Integer> parseHeader(Row row) {
        var result = new HashMap<ProductFileHeader, Integer>();
        AtomicInteger i = new AtomicInteger(0);
        row.forEach(cell -> result.put(ProductFileHeader.getValue(cell.getStringCellValue()), i.getAndIncrement()));
        return result;
    }

    private void validateHeaders(Map<ProductFileHeader, Integer> headersMap, EnumSet<ProductFileHeader> requiredHeaders) {
        requiredHeaders.forEach(header -> {
            if (!headersMap.containsKey(header)) {
                throw new BadRequestException(localizationService.getMessage("required.header", header.getNameRu()));
            }
        });
    }
}
