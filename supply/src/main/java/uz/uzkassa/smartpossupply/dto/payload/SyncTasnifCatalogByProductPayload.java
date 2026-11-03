package uz.uzkassa.smartpossupply.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SyncTasnifCatalogByProductPayload implements Serializable {

    String tin;

    Long userId;

    Long productId;

    Long facturaProductId;

    public SyncTasnifCatalogByProductPayload(String tin, Long userId, Long productId) {
        this.tin = tin;
        this.userId = userId;
        this.productId = productId;
    }
}
