package uz.uzkassa.smartpossupply.dto.integration.turon.mark;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TuronMarkOrderResponseDTO implements Serializable {

    private String orderId;
    private Integer expectedCompleteTimestamp;
}
