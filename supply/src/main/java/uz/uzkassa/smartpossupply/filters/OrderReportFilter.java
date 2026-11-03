package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderReportFilter extends BaseFilter {

    Long agentId;

    List<Long> agentIds;

    Integer orderCount;

    Integer productCount;

    Integer packageCount;

    Integer productCountInPackage;

    BigDecimal totalPrice;

    String paymentType;
}
