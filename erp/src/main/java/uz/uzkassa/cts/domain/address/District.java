package uz.uzkassa.cts.domain.address;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.cts.domain.base.LocalizedEntity;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "district",
        indexes = {
                @Index(columnList = "code", name = "district_code_idx"),
                @Index(columnList = "region_id", name = "district_region_id_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class District extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 343423L;

    @Column(name = "code")
    Long code;

    @Column(name = "region_id")
    Long regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    Region region;
}
