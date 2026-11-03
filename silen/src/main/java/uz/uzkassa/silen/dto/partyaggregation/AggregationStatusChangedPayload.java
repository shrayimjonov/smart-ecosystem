package uz.uzkassa.silen.dto.partyaggregation;

import lombok.Data;

import java.io.Serializable;

@Data
public class AggregationStatusChangedPayload implements Serializable {
    String id;
    String partyAggregationId;
    String code;
    String status;
    String statusName;

}
