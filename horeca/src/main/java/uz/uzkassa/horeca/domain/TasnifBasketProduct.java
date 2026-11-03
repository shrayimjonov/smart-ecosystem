package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.soliq.MxikDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tasnif_basket_product",
        indexes = {
                @Index(columnList = "company_id", name = "tasnif_basket_product_company_id_idx")
        })
public class TasnifBasketProduct extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 9839485L;

    @Column(name = "products")
    @JdbcTypeCode(SqlTypes.JSON)
    List<MxikDTO> products;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TasnifBasketProduct)) {
            return false;
        }
        return id != null && id.equals(((TasnifBasketProduct) o).id);
    }
}
