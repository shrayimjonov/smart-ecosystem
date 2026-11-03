package uz.uzkassa.smartpos.domain.postgres.receipt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.shift.ShiftDTO;
import uz.uzkassa.smartpos.enums.ShiftStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "shift",
        indexes = {
                @Index(columnList = "startDateTime,endDateTime,status", name = "shift_date_status_idx"),
                @Index(columnList = "status,user_id", name = "shift_status_user_idx")
        }
)
@Getter
@Setter
@DynamicUpdate
public class Shift {

    @Id
    @GenericGenerator(
            name = "sequenceGenerator",
            type = SequenceStyleGenerator.class,
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "optimizer",
                            value = "pooled-lo"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "initial_value",
                            value = "1"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "increment_size",
                            value = "1"
                    )
            }
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequenceGenerator"
    )
    private Long id;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long number;
    private Long fiscalNumber;

    @Enumerated(EnumType.STRING)
    private ShiftStatus status;

    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "terminal_id")
    private Long terminalId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Device terminal;

    @Column(name = "branch_id")
    private Long branchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;

    public ShiftDTO getDTO() {
        ShiftDTO shiftDTO = new ShiftDTO();
        shiftDTO.setId(getId());
        shiftDTO.setStartDateTime(getStartDateTime());
        shiftDTO.setEndDateTime(getEndDateTime());
        shiftDTO.setNumber(getNumber());
        shiftDTO.setFiscalNumber(getFiscalNumber());
        shiftDTO.setStatus(getStatus());
        return shiftDTO;
    }
}
