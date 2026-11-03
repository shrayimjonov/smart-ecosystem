package uz.uzkassa.silen.integration.turon;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@Setter
public class CreateAggregationResponse implements Serializable {
    private String reportId;
    private String errorMessage;

    public CreateAggregationResponse(String reportId,String errorMessage) {
        this.errorMessage = errorMessage;
        this.reportId = reportId;
    }

    public CreateAggregationResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    CreateAggregationResponse(){}


}
