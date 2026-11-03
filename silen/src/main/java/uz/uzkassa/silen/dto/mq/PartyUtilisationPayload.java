package uz.uzkassa.silen.dto.mq;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyUtilisationPayload implements Serializable {

    ProductType productType;

    String partyId;

    int quantity;

    public PartyUtilisationPayload(String partyId) {
        this.partyId = partyId;
    }
}
