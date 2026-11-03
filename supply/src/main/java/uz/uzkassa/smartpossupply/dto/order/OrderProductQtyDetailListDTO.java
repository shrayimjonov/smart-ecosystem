package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.10.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductQtyDetailListDTO implements Serializable {

    Long id;

    String orderNumber;

    LocalDateTime orderDate;

    OrderStatus status;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal priceWithoutVat;

    BigDecimal vatPrice;

    BigDecimal totalPriceWithoutVat;

    BigDecimal totalPrice;

    CompanyBaseDTO customer;

    CommonDTO agent;

    LocalDateTime lastModifiedDate;


    @SuppressWarnings("unused")
    public OrderProductQtyDetailListDTO(Long id, String orderNumber, LocalDateTime orderDate, String orderStatus, BigDecimal qty, BigDecimal price, BigDecimal priceWithoutVat, BigDecimal vatPrice,
                                        BigDecimal totalPriceWithoutVat, BigDecimal totalPrice,
                                        Long companyId, String companyName, String companyTin, String companyBrand, Long agentId, String agentFirstName, String agentLastName, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = OrderStatus.valueOf(orderStatus);
        this.orderDate = orderDate;
        this.qty = qty;
        this.price = price;
        this.vatPrice = vatPrice;
        this.totalPriceWithoutVat = totalPriceWithoutVat;
        this.totalPrice = totalPrice;
        this.priceWithoutVat = priceWithoutVat;
        if (companyId != null && StringUtils.isNotEmpty(companyTin)) {
            this.customer = new CompanyBaseDTO(companyId, companyName, companyTin, companyBrand);
        }
        if (agentId != null) {
            this.agent = new CommonDTO(agentId, Optional.ofNullable(agentFirstName).orElse("").concat(" ").concat(Optional.ofNullable(agentLastName).orElse("")));
        }
        this.lastModifiedDate = lastModifiedDate;
    }
}
