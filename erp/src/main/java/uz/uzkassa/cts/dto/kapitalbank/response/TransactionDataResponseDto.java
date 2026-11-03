package uz.uzkassa.cts.dto.kapitalbank.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TransactionDataResponseDto implements Serializable {

    private Integer id;
    private String maskedPhoneNumber;

    @Override
    public String toString() {
        return "TransactionDataResponseDto{" +
                "id=" + id +
                ", maskedPhoneNumber='" + maskedPhoneNumber + '\'' +
                '}';
    }
}
