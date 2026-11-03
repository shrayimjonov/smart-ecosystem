package uz.uzkassa.silen.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.Attributes;
import uz.uzkassa.silen.domain.Product;
import uz.uzkassa.silen.domain.ProductAttributes;
import uz.uzkassa.silen.dto.filter.AttributesFilter;
import uz.uzkassa.silen.dto.filter.ProductFilter;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductType;
import uz.uzkassa.silen.enumeration.SortTypeEnum;
import uz.uzkassa.silen.repository.pg.AttributesRepository;
import uz.uzkassa.silen.repository.pg.OrganizationRepository;
import uz.uzkassa.silen.repository.pg.ProductRepository;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExportProductExcel extends AbstractProductExcel {
    private final ProductRepository productRepository;
    private final AttributesRepository attributesRepository;
    private final OrganizationRepository organizationRepository;
    private ProductFilter filter;


    public void exportProducts(ProductFilter filter, OutputStream outputStream) throws NoSuchMethodException, IOException {
        this.filter = filter;
        List<String> headers = new LinkedList<>(Arrays.asList("ИНН", "НАЗВАНИЕ ПРОДУКТА",
            "КОРОТКОЕ НАЗВАНИЕ ПРОДУКТА",
            "ИКПУ", "ЕД. ИЗМЕРЕНИЯ",
            "НДС (СТАВКА %)", "ЦЕНА",
            "GTIN", "ТИП ПРОДУКЦИИ"));
        headers.addAll(getHeadersWithAttributes());

        String[] titles = new String[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            titles[i] = headers.get(i);
        }
        int[] columnWidth = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            columnWidth[i] = headers.get(i).length() + 100;
        }
        setColumnWidth(columnWidth);
        setHeaderTitles(titles);
        setWorkingSheetName("Products");
        execute(outputStream);
    }

    @Override
    public void writeBody() {
        filter.setPage(0);
        filter.setSize(10000);
        Page<Product> productList = productRepository.findAllByFilter(filter);
        int rowNum = 1;
        for (Product product : productList) {
            Row row = workingSheet.createRow(rowNum++);
            generateRow(row, product);
        }
    }

    private void generateRow(Row row, Product product) {
        int colNum = 0;
        //inn
        Cell cell1 = row.createCell(colNum++);
        cell1.setCellStyle(getNumberStyle());
        cell1.setCellValue(product.getOrganization().getTin());

        // name
        Cell cell2 = row.createCell(colNum++);
        cell2.setCellStyle(getStringStyle());
        cell2.setCellValue(product.getName());

        //shortName
        Cell cell3 = row.createCell(colNum++);
        cell3.setCellStyle(getStringStyle());
        cell3.setCellValue(product.getShortName());

        //getCatalogCode
        Cell cell4 = row.createCell(colNum++);
        cell4.setCellStyle(getStringStyle());
        cell4.setCellValue(product.getCatalogCode());

        //getPackageName
        Cell cell5 = row.createCell(colNum++);
        cell5.setCellStyle(getStringStyle());
        cell5.setCellValue(product.getPackageName());

        //getVatRate
        Cell cell6 = row.createCell(colNum++);
        cell6.setCellValue(product.getVatRate().doubleValue());

        //getPrice
        Cell cell7 = row.createCell(colNum++);
        cell7.setCellValue(Optional.ofNullable(product.getPrice()).orElse(BigDecimal.ZERO).doubleValue());

        //getBarcode
        Cell cell8 = row.createCell(colNum++);
        cell8.setCellStyle(getStringStyle());
        cell8.setCellValue(product.getBarcode());

        // getProductType
        Cell cell9 = row.createCell(colNum++);
        cell9.setCellStyle(getStringStyle());
        log.debug("Product type is {}", product.getType());
        cell9.setCellValue(product.getType().getNameRu());


        int sortOrder = 1;
        //attributes
        for (ProductAttributes attr : product.getAttributes()) {
            if (attr.getAttribute().getSorter() == sortOrder) {
                Cell attribute = row.createCell(colNum++);
                attribute.setCellStyle(getStringStyle());
                attribute.setCellValue(attr.getAttributeValue());
            } else {
                Cell attribute = row.createCell(colNum++);
                attribute.setCellStyle(getStringStyle());
                attribute.setCellValue("");
            }
            sortOrder++;
            log.debug("attr {}", attr);

        }
    }

    private List<String> getHeadersWithAttributes() {
        final String organizationId = SecurityUtils.getCurrentOrganizationId();
        AttributesFilter filter = new AttributesFilter();
        filter.setOrderBy("sorter");
        filter.setSortOrder(SortTypeEnum.asc);

        if (StringUtils.isNotEmpty(organizationId)) {
            List<String> organizationTypes = organizationRepository.getReferenceById(organizationId).getTypes()
                .stream().map(OrganizationType::name)
                .collect(Collectors.toList());
            ProductType productType = ProductType.valueOf(organizationTypes.get(0));
            filter.setProductType(productType);
        }

        return attributesRepository.findAllByFilter(filter)
            .map(Attributes::getName)
            .stream().collect(Collectors.toList());
    }
}
