package uz.uzkassa.cts.dto.kapitalbank.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KapitalBankResponseDTO implements Serializable {

    private String errorMessage;
    private String status;
    private Long timestamp;

    @Override
    public String toString() {
        return "KapitalBankResponseDTO{" +
                ", errorMessage='" + errorMessage + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
