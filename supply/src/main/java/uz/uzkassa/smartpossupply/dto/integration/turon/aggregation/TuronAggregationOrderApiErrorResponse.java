package uz.uzkassa.smartpossupply.dto.integration.turon.aggregation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TuronAggregationOrderApiErrorResponse {

    private String error_message;

    @Override
    public String toString() {
        return "Ошибка от TrueApi!\n" + error_message;
    }
}
