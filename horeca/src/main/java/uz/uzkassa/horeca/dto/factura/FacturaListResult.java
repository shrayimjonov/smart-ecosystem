package uz.uzkassa.horeca.dto.factura;

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
public class FacturaListResult<T> implements Serializable {

    private List<T> rows;
    private Integer totalCount;
}
