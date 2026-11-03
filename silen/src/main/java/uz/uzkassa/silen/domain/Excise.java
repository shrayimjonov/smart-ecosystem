package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.ExciseDTO;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;

/**
 * A Excise.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "excise")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Excise extends BaseIdentityEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", unique = true)
    private ProductType type;

    @Column(nullable = false)
    private BigDecimal amount;

    public ExciseDTO toDto() {
        ExciseDTO dto = new ExciseDTO();
        dto.setId(getId());
        dto.setType(getType());
        if (getType() != null) {
            dto.setTypeName(getType().getNameRu());
        }
        dto.setAmount(getAmount());
        return dto;
    }

    public BigDecimal calculateRate(final Double capacity, final Double titleAlcohol) {
        if (EnumSet.of(ProductType.Vodka, ProductType.Cognac, ProductType.Liquor, ProductType.LiquorVodka, ProductType.Tincture).contains(getType())) {
            final BigDecimal volume = BigDecimal.valueOf(titleAlcohol).divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);
            return this.getAmount().multiply(volume).multiply(BigDecimal.valueOf(capacity));
        }
        return this.getAmount().divide(new BigDecimal(10), 3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(capacity));
    }
}
