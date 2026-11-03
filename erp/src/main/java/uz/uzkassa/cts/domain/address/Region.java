package uz.uzkassa.cts.domain.address;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import uz.uzkassa.cts.dto.customer.admin.RegionItemDTO;

import java.io.Serial;

@Entity
@Table(name = "region",
        indexes = {
                @Index(columnList = "code", name = "region_code_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class Region extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 5548812L;

    @NotNull
    @Column(name = "code", unique = true)
    Long code;

    @Column(name = "short_name", unique = true)
    String shortName;

    public RegionItemDTO toItemDTO() {
        return new RegionItemDTO(getId(), getName(), getShortName());
    }
}
