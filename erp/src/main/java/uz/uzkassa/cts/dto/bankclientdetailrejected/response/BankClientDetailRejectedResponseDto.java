package uz.uzkassa.cts.dto.bankclientdetailrejected.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.cts.enums.BankClientRejectedStatus;
import uz.uzkassa.cts.enums.ServiceType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BankClientDetailRejectedResponseDto implements Serializable {
    private Long id;
    private String documentNo;
    private String journalName;
    private String transactionNumber;
    @Size(min = 20, max = 20)
    private String payerAccountNumber;
    @NotNull
    @NotEmpty
    @Size(min = 9, max = 14)
    private String payerIdentityNumber;
    private String payerName;
    @Size(min = 5, max = 5)
    private String payerBankCode;
    private String payerBankName;
    @Size(min = 20, max = 20)
    private String receiverAccountNumber;
    @Size(min = 9, max = 14)
    private String receiverIdentityNumber;
    private String receiverName;
    @Size(min = 5, max = 5)
    private String receiverBankCode;
    private String receiverBankName;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String description;
    private String contractNumber;
    private String specificationNumber;
    private boolean approved;
    private Long bankClientJournalId;
    @Enumerated(value = EnumType.STRING)
    private ServiceType serviceType;
    private BankClientRejectedStatus status;
    private String oldTin;
}
