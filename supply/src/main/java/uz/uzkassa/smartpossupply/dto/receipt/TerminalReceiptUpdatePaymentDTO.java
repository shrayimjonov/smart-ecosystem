package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpossupply.enums.ReceiptPaymentType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class TerminalReceiptUpdatePaymentDTO implements Serializable {

    Map<ReceiptPaymentType, BigDecimal> payments = new LinkedHashMap<>();
}