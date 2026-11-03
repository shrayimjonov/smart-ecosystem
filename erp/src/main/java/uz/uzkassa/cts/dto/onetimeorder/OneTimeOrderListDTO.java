package uz.uzkassa.cts.dto.onetimeorder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OneTimeOrderListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1847487624323452L;

    Long id;

    CommonDTO contract;

    CustomerShortDTO customer;

    CommonDTO service;

    CommonDTO creator;

    CommonDTO lastModifier;

    String fileUri;

    String fileName;

    String contentType;

    Integer totalProductCount;

    BigDecimal totalSellPrice;

    BigDecimal totalPrice;

    BigDecimal totalVatPrice;

    String totalVatPriceInWord;

    String totalSellPriceInWord;

    String totalPriceInWord;

    ContractStatus status;

    LocalDateTime createdDate;

    String number;

    String serialNumberKKM;
}
