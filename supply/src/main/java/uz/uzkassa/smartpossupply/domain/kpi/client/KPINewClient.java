package uz.uzkassa.smartpossupply.domain.kpi.client;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "kpi_new_client")
@SQLDelete(sql = "UPDATE kpi SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPINewClient extends KPI {

    @Column(name = "client_limit", nullable = false)
    Integer clientLimit;

    @Column(name = "bonus_amount", nullable = false, precision = 25, scale = 2)
    BigDecimal bonusAmount;

    @OneToMany(mappedBy = "kpi", cascade = CascadeType.ALL, orphanRemoval = true)
    List<KPIItem> kpiItems = new ArrayList<>();


    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.KPI_NEW_CLIENT;
    }
}
