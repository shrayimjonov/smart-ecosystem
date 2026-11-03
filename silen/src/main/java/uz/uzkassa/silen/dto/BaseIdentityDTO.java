package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BaseIdentityDTO extends BaseAuditDTO implements Serializable {
    protected Long id;
}
