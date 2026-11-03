package uz.uzkassa.horeca.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalAdditionalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 99948359L;

    String samModuleSerialNumber;
    String receiptSeq;
    LocalDateTime fiscalCreatedDate;
    Long providerId;
    String transactionId;
    Integer numberInShift;
    String epaymentId;
    String voucherId;
    String carPlateNumber;
    String columnNumber;
    String cardType;
    String rrn;
    String terminalVersion;
    boolean forCredit;
    Integer month;
    BigDecimal percent;
}
