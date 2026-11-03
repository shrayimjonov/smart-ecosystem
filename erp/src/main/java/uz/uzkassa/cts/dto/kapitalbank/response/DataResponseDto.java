package uz.uzkassa.cts.dto.kapitalbank.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DataResponseDto implements Serializable {

    private String status;
    private Long balance;
}
