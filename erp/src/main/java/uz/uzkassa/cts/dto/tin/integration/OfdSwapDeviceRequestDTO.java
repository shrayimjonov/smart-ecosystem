package uz.uzkassa.cts.dto.tin.integration;


import lombok.ToString;

import java.io.Serializable;

@ToString
public class OfdSwapDeviceRequestDTO implements Serializable {

    private Long applicationId;
    private Long ccmCategoryId;
    private String ccmSerialNumber;
    private String terminalStatusChangeId;
    private Integer reasonId;

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

    public Integer getReasonId() {
        return reasonId;
    }

    public void setReasonId(Integer reasonId) {
        this.reasonId = reasonId;
    }
}
