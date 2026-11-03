package uz.uzkassa.smartpos.dto.nic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OfdResponseDataDTO<T> extends OfdResponseDTO {
    private T data;
}
