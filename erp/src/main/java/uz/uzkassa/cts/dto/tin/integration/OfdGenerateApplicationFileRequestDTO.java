package uz.uzkassa.cts.dto.tin.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class OfdGenerateApplicationFileRequestDTO implements Serializable {

    private Long officeId;
    private String ccmSerialNumber;
    private Long ccmCategoryId;
    private String terminalId;
    private String phone;

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public String getCcmSerialNumber() {
        return ccmSerialNumber;
    }

    public void setCcmSerialNumber(String ccmSerialNumber) {
        this.ccmSerialNumber = ccmSerialNumber;
    }

    public Long getCcmCategoryId() {
        return ccmCategoryId;
    }

    public void setCcmCategoryId(Long ccmCategoryId) {
        this.ccmCategoryId = ccmCategoryId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
