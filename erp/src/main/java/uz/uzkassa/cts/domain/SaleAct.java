package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "sale_act")
@Entity
@SQLDelete(sql = "UPDATE sale_act SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class SaleAct extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 7603468774L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ContractStatus status = ContractStatus.CREATED;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_contract_id")
    private SaleContract saleContract;

    @OneToOne(mappedBy = "saleAct", cascade = CascadeType.ALL, orphanRemoval = true)
    private SaleInvoice saleInvoice;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "power_attorney_file_uri")
    private String powerAttorneyFileUri;

    @Column(name = "power_attorney_file_name")
    private String powerAttorneyFileName;

    @Column(name = "power_attorney_content_type")
    private String powerAttorneyContentType;

    @Column(name = "accept_date")
    private LocalDateTime acceptDate;

    public String getNumber() {
        return PrefixEnum.AAS + "/" + this.getId();
    }

    public String decimalToWord(BigDecimal amount) {
        return NumberToWordUtil.digits2Text(amount);
    }
}
