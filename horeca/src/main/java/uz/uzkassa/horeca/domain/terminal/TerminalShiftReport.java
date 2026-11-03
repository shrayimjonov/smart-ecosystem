package uz.uzkassa.horeca.domain.terminal;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import uz.uzkassa.horeca.domain.Terminal;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.domain.branch.Branch;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "terminal_shift_report")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalShiftReport extends SimpleEntity implements Serializable {

    @Column(name = "number")
    Long number;

    @Column(name = "start_date_time")
    LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    LocalDateTime endDateTime;

    @Column(name = "receipts")
    Long receipts;

    @Column(name = "receipts_returned")
    Long receiptsReturned;

    @Column(name = "card")
    BigDecimal card;

    @Column(name = "card_returned")
    BigDecimal cardReturned;

    @Column(name = "cash")
    BigDecimal cash;

    @Column(name = "cash_returned")
    BigDecimal cashReturned;

    @Column(name = "nds")
    BigDecimal NDS;

    @Column(name = "nds_returned")
    BigDecimal ndsReturned;

    @Column(name = "user_id")
    Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "user_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    User user;

    @Column(name = "terminal_id")
    Long terminalId;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "terminal_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Terminal terminal;

    @Column(name = "branch_id")
    Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Branch branch;
}

