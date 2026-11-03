package uz.uzkassa.silen.dto.marking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.dto.BaseUuidDTO;
import uz.uzkassa.silen.enumeration.*;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.OrderProduct} entity.
 */
@Getter
@Setter
@ToString
public class OrderProductDTO extends BaseUuidDTO implements Serializable {
    private String orderNumber;
    private String productId;
    private String productName;
    private String gtin;
    private Integer quantity;
    private int received;
    private int remaining;
    private SerialNumberType serialNumberType;
    private RateType rateType;
    private Integer templateId;
    private CisType cisType;
    private String cisTypeNme;
    private BufferStatus bufferStatus;
    private String bufferStatusName;
    private String organizationId;
    private String organizationName;
    private String organizationTin;
    private String productType;
    private String productGroup;
    private String turonId;
    private ReleaseMethodType releaseMethodType;
    private String releaseMethodTypeName;
    private Set<PartyDTO> parties;
}
