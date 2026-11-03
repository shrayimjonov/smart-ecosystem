package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.base.BaseEntity;

import java.io.Serial;

@Entity
@Table(name = "product_photo",
        indexes = {
                @Index(columnList = "photo_id", name = "product_photo_photo_id_idx"),
                @Index(columnList = "product_id", name = "product_photo_product_id_idx"),
                @Index(columnList = "main", name = "product_photo_main_idx")
        })
@Getter
@Setter
@SQLDelete(sql = "UPDATE product_photo SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPhoto extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 7845692589L;

    @NotNull
    @Column(name = "photo_id", nullable = false)
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false, insertable = false)
    FileUpload photo;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "main", nullable = false, columnDefinition = "boolean default false")
    boolean main;
}
