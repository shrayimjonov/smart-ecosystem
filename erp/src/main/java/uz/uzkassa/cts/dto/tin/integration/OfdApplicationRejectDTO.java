package uz.uzkassa.cts.dto.tin.integration;

import uz.uzkassa.cts.dto.ofd.OfdFileDTO;

import java.io.Serializable;
import java.util.List;

public class OfdApplicationRejectDTO implements Serializable {

    private List<OfdFileDTO> applicationFile;
    private Long applicationId;
    private int reasonId;

    public List<OfdFileDTO> getApplicationFile() {
        return applicationFile;
    }

    public void setApplicationFile(List<OfdFileDTO> applicationFile) {
        this.applicationFile = applicationFile;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public int getReasonId() {
        return reasonId;
    }

    public void setReasonId(int reasonId) {
        this.reasonId = reasonId;
    }
}
