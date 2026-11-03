package uz.uzkassa.cts.dto.apay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties
public class APayErrorResponseDto implements Serializable {
    private String type;
    private String title;
    private Long status;
    private String detail;
    private String path;
    private String message;

    @Override
    public String toString() {
        return "APayErrorResponseDto{" +
            "type='" + type + '\'' +
            ", title='" + title + '\'' +
            ", status=" + status +
            ", detail='" + detail + '\'' +
            ", path='" + path + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}
