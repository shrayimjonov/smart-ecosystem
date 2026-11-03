package uz.uzkassa.silen.excel;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.Attributes;
import uz.uzkassa.silen.dto.filter.AttributesFilter;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductType;
import uz.uzkassa.silen.enumeration.SortTypeEnum;
import uz.uzkassa.silen.repository.pg.AttributesRepository;
import uz.uzkassa.silen.repository.pg.OrganizationRepository;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductExcelScheme extends AbstractProductExcel {
    private final AttributesRepository attributesRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public void writeBody() {
    }

    public void getScheme(HttpServletResponse response) throws IOException {
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
            columnWidth[i] = headers.get(i).length()+100;
        }
        setHeaderTitles(titles);
        setWorkingSheetName("Products");
//        setColumnWidth(90, 200, 200, 130, 130, 90, 90, 130, 150, 200, 90, 90);
        setColumnWidth(columnWidth);
        execute(response.getOutputStream());
    }

    private List<String> getHeadersWithAttributes() {
        final String organizationId = SecurityUtils.getCurrentOrganizationId();
        AttributesFilter filter = new AttributesFilter();
        filter.setOrderBy("sorter");
        filter.setSortOrder(SortTypeEnum.asc);

        if (StringUtils.isNotEmpty(organizationId)) {
            List<String> organizationTypes = organizationRepository.getReferenceById(organizationId).getTypes()
                .stream().map(OrganizationType::name)
                .toList();
            ProductType productType = ProductType.valueOf(organizationTypes.getFirst());
            filter.setProductType(productType);
        }

        return attributesRepository.findAllByFilter(filter)
            .map(Attributes::getName)
            .stream().collect(Collectors.toList());
    }
}
