package uz.uzkassa.cts.dto.tin.integration;


import lombok.ToString;

import java.io.Serializable;

@ToString
public class OfdSwapFiscalModuleRequestDTO implements Serializable {

    private Long applicationId;
    private Long ccmCategoryId;
    private String ccmSerialNumber;
    private int reasonId;
    private String reason;
    private String terminalId;
    private String terminalStatusChangeId;

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getCcmCategoryId() {
        return ccmCategoryId;
    }

    public void setCcmCategoryId(Long ccmCategoryId) {
        this.ccmCategoryId = ccmCategoryId;
    }

    public int getReasonId() {
        return reasonId;
    }

    public void setReasonId(int reasonId) {
        this.reasonId = reasonId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCcmSerialNumber() {
        return ccmSerialNumber;
    }

    public void setCcmSerialNumber(String ccmSerialNumber) {
        this.ccmSerialNumber = ccmSerialNumber;
    }

    public String getTerminalStatusChangeId() {
        return terminalStatusChangeId;
    }

    public void setTerminalStatusChangeId(String terminalStatusChangeId) {
        this.terminalStatusChangeId = terminalStatusChangeId;
    }
}
