package uz.uzkassa.cts.dto.ofd;

import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
public class OfdCustomerFiscalDTO implements Serializable {

    private Long ccmCategoryId;
    private String ccmSerialNumber;
    private Long officeId;
    private String phone;
    private String terminalId;
    private String tin;
    private String pinfl;
    private List<OfdFileDTO> contractFile;
    private List<OfdFileDTO> applicationFile;
    private OfdCustomerBranchScannerDTO scanner;


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

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getPinfl() {
        return pinfl;
    }

    public void setPinfl(String pinfl) {
        this.pinfl = pinfl;
    }

    public List<OfdFileDTO> getContractFile() {
        return contractFile;
    }

    public void setContractFile(List<OfdFileDTO> contractFile) {
        this.contractFile = contractFile;
    }

    public List<OfdFileDTO> getApplicationFile() {
        return applicationFile;
    }

    public void setApplicationFile(List<OfdFileDTO> applicationFile) {
        this.applicationFile = applicationFile;
    }

    public OfdCustomerBranchScannerDTO getScanner() {
        return scanner;
    }

    public void setScanner(OfdCustomerBranchScannerDTO scanner) {
        this.scanner = scanner;
    }
}
