package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.base.BaseEntity;

import java.io.Serial;
import java.io.Serializable;

import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "terminal",
        indexes = {
                @Index(columnList = "serial_number", name = "terminal_serial_number_idx"),
                @Index(columnList = "fiscal_module_serial_number", name = "terminal_fm_serial_number_idx"),
                @Index(columnList = "branch_id", name = "terminal_branch_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE ternimal set deleted='false' WHERE id=?")
public class Terminal extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 45654645L;

    @NotNull
    @Column(name = "serial_number", nullable = false)
    String serialNumber;

    @Column(name = "fiscal_module_serial_number")
    String fiscalModuleSerialNumber;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    public CommonCodeDTO toTerminalDTO() {
        return new CommonCodeDTO(getId(), getSerialNumber(), getFiscalModuleSerialNumber());
    }

    @Override
    public String getName() {
        return getSerialNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Terminal)) {
            return false;
        }
        return id != null && id.equals(((Terminal) o).id);
    }
}
