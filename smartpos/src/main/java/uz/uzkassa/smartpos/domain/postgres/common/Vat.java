package uz.uzkassa.smartpos.domain.postgres.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.dto.VatDTO;

import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table(name = "vat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "percent"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vat extends AbstractAuditingEntity implements Serializable {
    @Id
    @GenericGenerator(
            name = "sequenceGenerator",
            type = SequenceStyleGenerator.class,
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "optimizer",
                            value = "pooled-lo"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "initial_value",
                            value = "1"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "increment_size",
                            value = "1"
                    )
            }
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequenceGenerator"
    )
    private Long id;

    private String name;
    private BigDecimal percent;

    public VatDTO toDTO() {
        VatDTO dto = setAuditValues(new VatDTO());
        dto.setId(getId());
        dto.setName(getName());
        dto.setPercent(getPercent());
        return dto;
    }
}
