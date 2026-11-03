package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.AggregationStatus;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CodeDTO {

    String id;
    String code;
    String printCode;
    AggregationStatus status;

    public CodeDTO(String id, String code, String printCode, AggregationStatus status) {
        this.id = id;
        this.code = code;
        this.printCode = printCode;
        this.status = status;
    }
}
