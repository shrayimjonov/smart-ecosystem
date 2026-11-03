package uz.uzkassa.cts.dto.orderspecification;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.contract.ContractShortDTO;
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
public class ServiceOrderSpecificationListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1349582345234L;

    Long id;

    String number;

    LocalDateTime createdDate;

    BigDecimal totalPrice;

    BigDecimal totalVat;

    BigDecimal amount;

    ContractStatus status;

    String specFileUri;

    String specFileName;

    String specContentType;

    ContractShortDTO contract;

    CustomerShortDTO customer;
}
