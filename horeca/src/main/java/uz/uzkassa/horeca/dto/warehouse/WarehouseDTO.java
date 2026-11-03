package uz.uzkassa.horeca.dto.warehouse;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 98329843L;

    Long id;

    String name;

    Long managerId;

    Long departmentId;

    Long branchId;
}
