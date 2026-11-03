package uz.uzkassa.smartpossupply.dto.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.05.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ProductPhotoSyncPayload implements Serializable {

    List<Long> branchIds;

    Long productId;

    public ProductPhotoSyncPayload(List<Long> branchIds, Long productId) {
        this.branchIds = branchIds;
        this.productId = productId;
    }
}
