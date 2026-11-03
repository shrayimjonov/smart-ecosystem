package uz.uzkassa.cts.dto.bankclientdetailrejected.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BankClientDetailsRejectRequestDto implements Serializable {
    private List<Long> ids;
}
