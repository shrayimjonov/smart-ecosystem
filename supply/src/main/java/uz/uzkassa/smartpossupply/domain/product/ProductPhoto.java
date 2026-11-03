package uz.uzkassa.smartpossupply.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.dto.product.ProductPhotoListDTO;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 01.03.2023 14:47
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SqlResultSetMapping(
        name = "ProductPhotoListMapper",
        classes = @ConstructorResult(
                targetClass = ProductPhotoListDTO.class,
                columns = {
                        @ColumnResult(name = "product_id", type = Long.class),
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "url", type = String.class),
                        @ColumnResult(name = "main", type = Boolean.class)
                }
        )
)
@Entity
@Table(name = "product_photo")
public class ProductPhoto extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 54856786L;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false, insertable = false)
    FileUpload photo;

    @Column(name = "is_main", columnDefinition = "boolean default false")
    boolean isMain;

    public ProductPhotoListDTO toDTO() {
        return new ProductPhotoListDTO(getPhotoId(), getPhoto().getUrl(), isMain, getPhoto().getFileSizeType());
    }

}
