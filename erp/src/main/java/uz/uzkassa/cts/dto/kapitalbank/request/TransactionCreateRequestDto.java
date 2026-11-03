package uz.uzkassa.cts.dto.kapitalbank.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class TransactionCreateRequestDto implements Serializable {

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 16, max = 16)
    private String pan;

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 4, max = 4)
    private String expireDate;

    @NotNull
    private Long amount;

    private String cashId;

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCashId() {
        return cashId;
    }

    public void setCashId(String cashId) {
        this.cashId = cashId;
    }

    @Override
    public String toString() {
        return "TransactionCreateRequestDto{" +
                "pan='" + pan + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", amount=" + amount +
                ", cashId='" + cashId + '\'' +
                '}';
    }
}
