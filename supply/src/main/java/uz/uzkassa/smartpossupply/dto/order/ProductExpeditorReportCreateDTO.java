package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ProductListType;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.11.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductExpeditorReportCreateDTO extends SimpleFilter {

    List<Long> orderIds = new ArrayList<>();

    ProductListType productListType;

    String agentName;

    String expeditorName;
}
