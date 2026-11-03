package uz.uzkassa.cts.dto.bankclient.response;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BankClientDetailsDto {

    private Long id;

    @NotNull
    private String documentNo;

    @NotNull
    @Size(min = 20, max = 20)
    private String payerAccountNumber;

    @NotNull
    @Size(min = 9, max = 14)
    private String payerIdentityNumber;

    @NotNull
    private String payerName;

    @NotNull
    @Size(min = 5, max = 5)
    private String payerBankCode;

    @NotNull
    private String payerBankName;


    @NotNull
    @Size(min = 20, max = 20)
    private String receiverAccountNumber;

    @NotNull
    @Size(min = 9, max = 14)
    private String receiverIdentityNumber;

    @NotNull
    private String receiverName;

    @NotNull
    @Size(min = 5, max = 5)
    private String receiverBankCode;

    @NotNull
    private String receiverBankName;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    private String description;

    @NotNull
    private boolean approved = false;

    @NotNull
    private boolean alreadyExists = false;

    private String rejectionDescription;

    @Override
    public String toString() {
        return "BankClientDetailsDto{" +
                "id=" + id +
                ", documentNo='" + documentNo + '\'' +
                ", payerAccountNumber='" + payerAccountNumber + '\'' +
                ", payerIdentityNumber='" + payerIdentityNumber + '\'' +
                ", payerName='" + payerName + '\'' +
                ", payerBankCode='" + payerBankCode + '\'' +
                ", payerBankName='" + payerBankName + '\'' +
                ", receiverAccountNumber='" + receiverAccountNumber + '\'' +
                ", receiverIdentityNumber='" + receiverIdentityNumber + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", receiverBankCode='" + receiverBankCode + '\'' +
                ", receiverBankName='" + receiverBankName + '\'' +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", description='" + description + '\'' +
                ", approved=" + approved +
                ", alreadyExists=" + alreadyExists +
                ", rejectionDescription='" + rejectionDescription + '\'' +
                '}';
    }
}
