package uz.uzkassa.cts.dto.tin.integration;


import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

@ToString
public class OfdApplicationDTO implements Serializable {

    private Long id;
    private String tin;
    private String pinfl;
    private String name;
    private Integer status;
    private String statusName;
    private String serialNumber;
    private String terminalId;
    private Long officeId;
    private Long officeFileId;
    private Long contractFileId;
    private Long applicationFileId;
    private String createdDate;
    private String answerDescription;
    private String registrationCardUrl;
    private Integer applicationTypeCode;
    private String applicationTypeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getAnswerDescription() {
        return answerDescription;
    }

    public void setAnswerDescription(String answerDescription) {
        this.answerDescription = answerDescription;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getRegistrationCardUrl() {
        return registrationCardUrl;
    }

    public void setRegistrationCardUrl(String registrationCardUrl) {
        this.registrationCardUrl = registrationCardUrl;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public Long getOfficeFileId() {
        return officeFileId;
    }

    public void setOfficeFileId(Long officeFileId) {
        this.officeFileId = officeFileId;
    }

    public Long getContractFileId() {
        return contractFileId;
    }

    public void setContractFileId(Long contractFileId) {
        this.contractFileId = contractFileId;
    }

    public Long getApplicationFileId() {
        return applicationFileId;
    }

    public void setApplicationFileId(Long applicationFileId) {
        this.applicationFileId = applicationFileId;
    }

    public Integer getApplicationTypeCode() {
        return applicationTypeCode;
    }

    public void setApplicationTypeCode(Integer applicationTypeCode) {
        this.applicationTypeCode = applicationTypeCode;
    }

    public String getApplicationTypeName() {
        return applicationTypeName;
    }

    public void setApplicationTypeName(String applicationTypeName) {
        this.applicationTypeName = applicationTypeName;
    }

    public String getPinfl() {
        return pinfl;
    }

    public void setPinfl(String pinfl) {
        this.pinfl = pinfl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OfdApplicationDTO that = (OfdApplicationDTO) o;
        return serialNumber.equals(that.serialNumber) &&
                terminalId.equals(that.terminalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber, terminalId);
    }
}
