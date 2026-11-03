package uz.uzkassa.cts.dto.kapitalbank.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;


public class PayRequestDto implements Serializable {

    @NotNull
    private Integer transactionId;

    @NotNull
    @NotEmpty
    @NotBlank
    private String sms;

    private String cashId;

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getCashId() {
        return cashId;
    }

    public void setCashId(String cashId) {
        this.cashId = cashId;
    }

    @Override
    public String toString() {
        return "PayRequestDto{" +
                "transactionId=" + transactionId +
                ", sms='" + sms + '\'' +
                ", cashId='" + cashId + '\'' +
                '}';
    }
}
