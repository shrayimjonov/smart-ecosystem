package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.integration.enums.PhotoType;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.08.2023
 */
@Getter
@Setter
@NoArgsConstructor
public class SingleCatalogPhotoDTO implements Serializable {

    Long id;

    String url;

    PhotoType photoType;

    Long originalPhotoId;

    boolean confirmed;
}
