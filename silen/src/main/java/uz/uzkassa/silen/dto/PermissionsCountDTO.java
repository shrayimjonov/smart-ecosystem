package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionsCountDTO {

    String orgId;

    int qty;

    public PermissionsCountDTO(String orgId, int qty) {
        this.orgId = orgId;
        this.qty = qty;
    }
}
