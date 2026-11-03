package uz.uzkassa.cts.dto.apay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class APayPayResponseDto implements Serializable {
    private String id;
    @JsonProperty("status")
    private APayStatus status;
    private LocalDateTime time;
}
