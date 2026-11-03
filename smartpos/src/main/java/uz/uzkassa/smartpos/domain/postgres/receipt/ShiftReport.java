package uz.uzkassa.smartpos.domain.postgres.receipt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.shift.ShiftReportDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shift_report",
    indexes = {
        @Index(columnList = "terminal_id,branch_id", name = "shift_report_terminal_branch_idx"),
        @Index(columnList = "branch_id", name = "shift_report_branch_idx")
    }
)
@Getter
@Setter
public class ShiftReport extends SimpleEntity implements Serializable {
    @Column(name = "number")
    private Long number;
    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;
    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @Column(name = "receipts")
    private Long receipts;
    @Column(name = "receipts_returned")
    private Long receiptsReturned;

    @Column(name = "card")
    private BigDecimal card;
    @Column(name = "card_returned")
    private BigDecimal cardReturned;

    @Column(name = "cash")
    private BigDecimal cash;
    @Column(name = "cash_returned")
    private BigDecimal cashReturned;

    @Column(name = "nds")
    private BigDecimal NDS;
    @Column(name = "nds_returned")
    private BigDecimal ndsReturned;

    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "user_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "terminal_id")
    private Long terminalId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "terminal_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Device terminal;

    @Column(name = "branch_id")
    private Long branchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;

    public ShiftReportDTO getDTO() {
        ShiftReportDTO dto = new ShiftReportDTO();
        dto.setFiscalNumber(getNumber());
        dto.setBranchId(getBranchId());
        if (getBranch() != null) {
            dto.setBranchName(getBranch().getName());
            dto.setBranchAddress(getBranch().getAddress());
            if (getBranch().getCompany() != null) {
                final Company company = getBranch().getCompany();
                dto.setCompanyName(company.getName());
                dto.setCompanyINN(company.getInn());
                dto.setCompanyDateTime(company.getCreatedDate());
                dto.setBusinessType(company.getBusinessType());
                dto.setFmNumber(company.getFiscalNumber());
            }
        }
        if (getTerminal() != null) {
            dto.setTerminalSN(getTerminal().getSerialNumber());
        }
        dto.setReceipts(getReceipts());
        dto.setReceiptsReturned(getReceiptsReturned());
        dto.setTotalCash(getCash());
        dto.setTotalCashReturned(getCashReturned());
        dto.setTotalCard(getCard());
        dto.setTotalCardReturned(getCardReturned());
        dto.setNDS(getNDS());
        dto.setNdsReturned(getNdsReturned());
        dto.setUserId(getUserId());
        if (getUser() != null) {
            dto.setUserName(getUser().getName());
        }
        dto.setStartDateTime(getStartDateTime());
        dto.setEndDateTime(getEndDateTime());
        return dto;
    }
}

