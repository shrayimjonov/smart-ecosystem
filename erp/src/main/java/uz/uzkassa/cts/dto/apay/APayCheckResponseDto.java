package uz.uzkassa.cts.dto.apay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class APayCheckResponseDto implements Serializable {
    private String message;
    @JsonProperty("status")
    private APayStatus status;
    private LocalDateTime time;

    @Override
    public String toString() {
        return "APayCheckResponseDto{" +
            "message='" + message + '\'' +
            ", status=" + status +
            ", time=" + time +
            '}';
    }
}
