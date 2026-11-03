package uz.uzkassa.cts.dto.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class OfdApplicationPayload implements Serializable {

    private Long customerId;
    private String tin;
    private String apiUrl;
}
