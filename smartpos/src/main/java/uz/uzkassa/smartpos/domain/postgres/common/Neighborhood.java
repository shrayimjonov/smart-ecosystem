package uz.uzkassa.smartpos.domain.postgres.common;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "neighborhood")
@Getter
@Setter
@DynamicUpdate
public class Neighborhood implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private Long code;

    private String name;

    @Column(name = "city_code", nullable = false)
    private Long cityCode;
    @Column(name = "region_code")
    private Long regionCode;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(
            name = "city_code",
            referencedColumnName = "code", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
        @JoinColumn(
            name = "region_code",
            referencedColumnName = "region_code", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    })
    private City city;
}
