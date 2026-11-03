package uz.uzkassa.horeca.filters;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FacturaFilter extends BaseFilter {

    Integer facturaType;

    List<Integer> facturaTypes;

    boolean incoming;

    Long buyerId;

    Long sellerId;

    Long facturaId;

    Long facturaNo;

    String excludeStatus;
}
