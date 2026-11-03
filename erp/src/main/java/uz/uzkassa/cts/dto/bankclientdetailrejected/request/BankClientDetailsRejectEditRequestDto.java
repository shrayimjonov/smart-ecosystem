package uz.uzkassa.cts.dto.bankclientdetailrejected.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BankClientDetailsRejectEditRequestDto implements Serializable {
    private Long id;
    private String tin;
}
