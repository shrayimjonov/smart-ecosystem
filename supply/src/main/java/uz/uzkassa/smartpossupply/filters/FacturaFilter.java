package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FacturaFilter extends BaseFilter {

    List<Long> ids;

    Integer facturaType;

    List<Integer> facturaTypes;

    boolean incoming;

    Long buyerId;

    Long sellerId;

    Long facturaId;

    Long facturaNo;

    Long branchId;

    Long stockId;

    String includeStatuses;

    @JsonIgnore //prevents Jackson from serializing/deserializing
    @Schema(hidden = true) // hides from Swagger
    private boolean isAdmin;
}
