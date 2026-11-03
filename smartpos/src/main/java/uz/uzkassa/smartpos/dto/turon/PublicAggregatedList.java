package uz.uzkassa.smartpos.dto.turon;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PublicAggregatedList {
    private String requestedCis;
    private String status;
    private Map<String, Object> codes;
    private String errorCode;
    private String errorMessage;
}
