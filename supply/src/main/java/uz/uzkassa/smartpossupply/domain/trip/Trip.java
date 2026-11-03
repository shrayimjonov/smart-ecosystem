package uz.uzkassa.smartpossupply.domain.trip;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.enums.TripStatus;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "trip")
@SQLDelete(sql = "UPDATE trip SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class Trip extends AbstractAuditingEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expeditor_id", insertable = false, updatable = false)
    User expeditor;

    @Column(name = "expeditor_id", nullable = false)
    Long expeditorId;

    @Column(name = "trip_number", nullable = false)
    String tripNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    TripStatus status = TripStatus.NEW;

    @Column(name = "trip_date")
    LocalDate tripDate;

    @Override
    public String getName() {
        return this.getTripNumber();
    }
}
