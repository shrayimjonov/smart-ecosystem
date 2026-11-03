package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.08.2023
 */
@Getter
@Setter
@NoArgsConstructor
public class MarketplacePhotoDTO implements Serializable {

    Long id;
    SingleCatalogPhotoDTO original;
    SingleCatalogPhotoDTO low;
    SingleCatalogPhotoDTO medium;
    SingleCatalogPhotoDTO high;
}
