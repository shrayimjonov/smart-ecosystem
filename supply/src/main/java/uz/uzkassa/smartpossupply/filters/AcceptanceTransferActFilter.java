package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptanceTransferActFilter extends BaseFilter {

    Long sellerId;

    Long buyerId;

    boolean incoming;
}
