package uz.uzkassa.cts.dto.apay;

import lombok.Data;

import java.io.Serializable;

@Data
public class APayCheckRequestDto implements Serializable {
    private String billId;

    public APayCheckRequestDto(String billId) {
        this.billId = billId;
    }
}
