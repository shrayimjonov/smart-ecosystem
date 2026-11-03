package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BaseUuidDTO extends BaseAuditDTO implements Serializable {
    protected String id;
}
