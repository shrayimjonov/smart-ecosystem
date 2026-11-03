package uz.uzkassa.cts.dto.kapitalbank.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CheckStatusRequestDto implements Serializable {
    @NotNull
    private Integer transactionId;
    private String cashId;

    @Override
    public String toString() {
        return "CheckStatusRequestDto{" +
                "transactionId=" + transactionId +
                ", cashId='" + cashId + '\'' +
                '}';
    }
}
