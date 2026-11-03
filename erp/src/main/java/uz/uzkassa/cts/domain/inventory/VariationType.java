package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import uz.uzkassa.cts.dto.variationtype.VariationTypeLookUpDTO;
import uz.uzkassa.cts.enums.WidgetType;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "variation_type")
@Getter
@Setter
@SQLDelete(sql = "UPDATE variation_type SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariationType extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 325698743369L;

    @NotNull
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    WidgetType type;

    @Column(name = "code")
    String code;

    @OneToMany(mappedBy = "variationType", cascade = CascadeType.ALL, orphanRemoval = true)
    List<VariationOption> options = new ArrayList<>();

    public VariationTypeLookUpDTO getLookUpDTO() {
        return new VariationTypeLookUpDTO(getId(), getName(), getCode(), getType());
    }
}
