package uz.uzkassa.cts.domain.address;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.io.Serial;


@Entity
@Table(name = "neighborhood",
        indexes = {
                @Index(columnList = "code", name = "neighborhood_code_idx", unique = true),
                @Index(columnList = "district_id", name = "neighborhood_district_id_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Neighborhood extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 945839485L;

    @NotNull
    @Column(name = "code", unique = true)
    Integer code;

    @NotNull
    @Column(name = "district_id", nullable = false)
    Long districtId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", updatable = false, insertable = false)
    District district;
}
