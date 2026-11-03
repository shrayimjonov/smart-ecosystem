package uz.uzkassa.smartpos.dto.factura;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LgotaResponseDTO implements Serializable {
    private List<LgotaDTO> rows;
    private Long totalCount;

    public List<LgotaDTO> getRows() {
        if (rows == null) {
            rows = new ArrayList<>();
        }
        return rows;
    }
}
