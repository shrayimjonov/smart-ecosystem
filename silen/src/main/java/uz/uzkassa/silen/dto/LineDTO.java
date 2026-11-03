package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.Mode;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Line} entity.
 */
@Getter
@Setter
@ToString
public class LineDTO implements Serializable {
    private String id;
    private String code;
    private String name;
    private String description;
    private Integer number;
    private String organizationId;
    private String organizationTin;
    private String deviceId;
    private String deviceCode;
    private Set<Mode> modes;
    private Set<String> modeNames;
    private Set<SelectItem> products;
    private Set<String> productIds;
}
