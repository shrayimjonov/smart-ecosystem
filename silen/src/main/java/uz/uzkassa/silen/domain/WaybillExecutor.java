package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.waybill.WaybillExecutorDTO;
import uz.uzkassa.silen.enumeration.WaybillStatus;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "waybill_executors")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE waybill_executors SET deleted = 'true' WHERE id=?")
public class WaybillExecutor extends BaseOrganizationEntity implements Serializable {

    @Column(name = "status")
    private WaybillStatus status;

    @Column(name = "operation_number")
    private String operationNumber;

    @Column(name = "operation_by")
    private String operationBy;

    @Column(name = "operation_date")
    private LocalDateTime operationDate;

    @Column(name = "waybill_id", updatable = false, insertable = false)
    String waybillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waybill_id")
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
