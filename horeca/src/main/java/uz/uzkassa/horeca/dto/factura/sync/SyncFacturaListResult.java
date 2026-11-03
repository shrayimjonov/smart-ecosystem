package uz.uzkassa.horeca.dto.factura.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyncFacturaListResult implements Serializable {

    private List<SyncFacturaDTO> rows;
    private Integer totalCount;
}
