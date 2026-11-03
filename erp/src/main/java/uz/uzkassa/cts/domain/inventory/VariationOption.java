package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.LocalizedEntity;

import java.io.Serial;

@Entity
@Table(name = "variation_option",
        indexes = {
                @Index(columnList = "variation_type_id", name = "variation_option_variation_type_id_idx")
        })
@Getter
@Setter
@SQLDelete(sql = "UPDATE variation_option SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariationOption extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 35896568974L;

    @NotNull
    @Column(name = "variation_type_id", nullable = false)
    Long variationTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variation_type_id", updatable = false, insertable = false)
    VariationType variationType;


}
