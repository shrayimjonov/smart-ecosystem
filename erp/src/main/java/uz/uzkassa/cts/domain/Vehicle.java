package uz.uzkassa.cts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serial;

@Entity
@Table(name = "vehicle", indexes = {
        @Index(columnList = "employee_id", name = "vehicle_employee_idx"),
        @Index(columnList = "number_plate", name = "vehicle_number_plate_idx"),
        @Index(columnList = "model", name = "vehicle_model_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE vehicle SET deleted = 'true' WHERE id=?")
public class Vehicle extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 9584364500927L;

    @Column(name = "employee_id", nullable = false)
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;

    @Column(name = "model", nullable = false)
    String model;

    @Column(name = "color")
    String color;

    @Column(name = "number_plate", nullable = false)
    String numberPlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;
}
