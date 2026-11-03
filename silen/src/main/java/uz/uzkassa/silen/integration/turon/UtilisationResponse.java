package uz.uzkassa.silen.integration.turon;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@Setter
public class UtilisationResponse implements Serializable {

    private String omsId;
    private String reportId;
    private String errorMessage;

    public UtilisationResponse() {
    }

    public UtilisationResponse(String reportId, String omsId) {
        this.omsId = omsId;
        this.reportId = reportId;
    }

    public UtilisationResponse(String omsId, String reportId, String errorMessage) {
        this.omsId = omsId;
        this.reportId = reportId;
        this.errorMessage = errorMessage;
    }

    public UtilisationResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
