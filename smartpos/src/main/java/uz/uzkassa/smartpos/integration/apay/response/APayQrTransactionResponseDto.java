package uz.uzkassa.smartpos.integration.apay.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class APayQrTransactionResponseDto extends APayAddTransactionResponseDto implements Serializable {
    private String qrCode;
}
