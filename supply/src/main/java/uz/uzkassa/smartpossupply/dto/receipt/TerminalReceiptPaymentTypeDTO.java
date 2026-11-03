package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TerminalReceiptPaymentTypeDTO implements Serializable {


    BigDecimal amount;

    boolean valid = true;

    public TerminalReceiptPaymentTypeDTO(BigDecimal amount) {
        this.amount = amount;
    }
}