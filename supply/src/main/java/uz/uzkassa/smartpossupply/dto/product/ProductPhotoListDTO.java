package uz.uzkassa.smartpossupply.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.enums.FileSizeType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPhotoListDTO extends FileDTO {

    boolean main;

    FileSizeType fileSize;

    Long productId;

    @Schema
    List<ProductPhotoListDTO> children = new ArrayList<>();


    public ProductPhotoListDTO(Long id, String url, boolean main, FileSizeType fileSize) {
        super(id, null, url);
        this.main = main;
        this.fileSize = fileSize;
    }

    public ProductPhotoListDTO(Long productId, Long id, String url, Boolean main) {
        super(id, null, url);
        this.productId = productId;
        this.main = main != null && main;
    }

    public void addChild(ProductPhotoListDTO productPhotoListDTO) {
        children.add(productPhotoListDTO);
    }
}
