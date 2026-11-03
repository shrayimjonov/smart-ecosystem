package uz.uzkassa.smartpossupply.domain.waybill;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillExecutorDTO;
import uz.uzkassa.smartpossupply.enums.WaybillStatus;

import java.io.Serial;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "waybill_executors", indexes = {
        @Index(columnList = "waybill_id", name = "waybill_executors_waybill_id_idx"),
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WaybillExecutor extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 547931648L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    WaybillStatus status;

    @Column(name = "operation_number")
    String operationNumber;

    @Column(name = "operation_by")
    String operationBy;

    @Column(name = "operation_date")
    LocalDateTime operationDate;

    @Column(name = "waybill_id")
    Long waybillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waybill_id", updatable = false, insertable = false)
    Waybill waybill;

    public WaybillExecutorDTO toDTO() {
        WaybillExecutorDTO dto = new WaybillExecutorDTO();
        dto.setStatus(getStatus());
        dto.setOperationNumber(getOperationNumber());
        dto.setOperationBy(getOperationBy());
        dto.setOperationDate(getOperationDate());
        return dto;
    }
}
