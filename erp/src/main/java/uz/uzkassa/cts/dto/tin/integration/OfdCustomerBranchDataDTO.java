package uz.uzkassa.cts.dto.tin.integration;

import lombok.ToString;

@ToString
public class OfdCustomerBranchDataDTO {

    private Long id;
    private Long status;
    private String statusInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }
}
