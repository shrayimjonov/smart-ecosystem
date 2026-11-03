package uz.uzkassa.smartpos.dto.sales;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.dto.clickhouse.SalesClickhouseInfoDto;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.sales.SalesStatus;
import uz.uzkassa.smartpos.enums.sales.SalesType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class SalesCabinetListDto {
    private Long id;
    private String uid;
    private String originUid;
    private LocalDateTime createdDateTime;
    private Map<PaymentType, BigDecimal> payments;
    private SalesType type;
    private SalesStatus status;
    private BigDecimal totalCost;
    private BigDecimal totalPaid;
    private String fiscalUrl;
    private Long shiftNumber;
    private String terminalSN;
    private CommonDTO branch;
    private CompanyBaseDTO company;
    private String customerName;
    private Integer positions;
    private String facturaId;

    public SalesCabinetListDto(SalesClickhouseInfoDto info) {
        this.id = info.getId();
        this.uid = info.getUid();
        this.originUid = info.getOriginUid();
        this.createdDateTime = info.getCreatedDateTime();
        this.payments = info.getPayments();
        this.type = info.getType();
        this.status = info.getStatus();
        this.totalCost = info.getTotalCost();
        this.totalPaid = info.getTotalPaid();
        this.shiftNumber = info.getShiftNumber();
        this.terminalSN = info.getTerminalSN();
        this.company = info.getCompany();
        this.branch = info.getBranch();
        this.customerName = info.getCustomerName();
        this.positions = CollectionUtils.size(info.getSalesDetailIds());
    }

    public SalesCabinetListDto(Sales sales) {
        this.id = sales.getId();
        this.uid = sales.getUid();
        this.originUid = sales.getOriginUid();
        this.createdDateTime = sales.getCreatedDateTime();
        this.payments = sales.getPayments();
        this.type = sales.getType();
        this.status = sales.getStatus();
        this.totalCost = sales.getTotalCost();
        this.totalPaid = sales.getTotalPaid();
        this.shiftNumber = sales.getShiftNumber();
        this.terminalSN = sales.getTerminalSN();
        if (sales.getCompany() != null) {
            this.company = sales.getCompany().toCompanyBaseDTO();
        }
        if (sales.getBranch() != null) {
            this.branch = sales.getBranch().toCommonDTO();
        }
        if (sales.getAdditional() != null) {
            this.customerName = sales.getAdditional().getCustomerName();
        }
        this.positions = CollectionUtils.size(sales.getSalesDetails());
        this.facturaId = sales.getFacturaId();
    }
}
