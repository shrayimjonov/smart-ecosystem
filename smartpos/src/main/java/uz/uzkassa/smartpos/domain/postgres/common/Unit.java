package uz.uzkassa.smartpos.domain.postgres.common;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.dto.UnitDTO;
import uz.uzkassa.smartpos.enums.CompanyType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "unit",
    indexes = {
        @Index(columnList = "name", name = "unit_name_idx")
    }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@DynamicUpdate
public class Unit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_uz", nullable = false)
    private String nameUz;

    @Column(name = "description")
    private String description;

    @Column(name = "code", unique = true)
    private Long code;

    @Column(columnDefinition = "boolean default false")
    private boolean countable;

    @Setter
    @ElementCollection(targetClass = CompanyType.class)
    @CollectionTable(name = "unit_types", joinColumns = @JoinColumn(name = "unit_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)))
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    Set<CompanyType> types = new HashSet<>();

    public UnitDTO toDTO() {
        UnitDTO dto = new UnitDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setNameRu(getName());
        dto.setNameUz(getNameUz());
        dto.setDescription(getDescription());
        dto.setCode(getCode());
        dto.setCountable(isCountable());
        return dto;
    }

    public SelectItem getShortDTO() {
        return new SelectItem(getId(), getName(), getDescription());
    }

}
