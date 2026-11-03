package uz.uzkassa.smartpos.domain.postgres.terminal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.TerminalDTO;
import uz.uzkassa.smartpos.enums.DeviceType;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Device extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "serial_number")
    private String serialNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DeviceType type;

    @NotNull
    @Column(name = "branch_id")
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    private Branch branch;

    @NotNull
    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    public TerminalDTO getDTO() {
        TerminalDTO dto = new TerminalDTO();
        dto.setId(getId());
        dto.setSerialNumber(getSerialNumber());
        dto.setBranchId(getBranchId());
        if (getBranch() != null) {
            dto.setBranchName(getBranch().getName());
            dto.setBranchAddress(getBranch().getAddress());
        }
        if (getCompany() != null) {
            dto.setCompanyId(getCompany().getId());
            dto.setCompanyName(getCompany().getName());
            dto.setCompanyInn(getCompany().getInn());
            dto.setCompanyAddress(getCompany().getAddress());
        }
        dto.setDeviceType(getType());
        return dto;
    }

    @Override
    public String getName() {
        return getSerialNumber();
    }
}
