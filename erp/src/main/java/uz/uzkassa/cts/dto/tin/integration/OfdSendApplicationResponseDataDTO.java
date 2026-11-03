package uz.uzkassa.cts.dto.tin.integration;

import java.io.Serializable;

public class OfdSendApplicationResponseDataDTO implements Serializable {

    private Long id;
    private Integer status;
    private String fileUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
