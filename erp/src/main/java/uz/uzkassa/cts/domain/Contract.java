package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.dto.contract.ContractShortDTO;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "customer_contract", indexes = {
    @Index(columnList = "customer_id", name = "customer_contract_customer_id_idx")
})
@Entity
@SQLDelete(sql = "UPDATE customer_contract SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class Contract extends BaseDocument {

    @Serial
    private static final long serialVersionUID = 1009930705L;

    @Column(name = "file_uri", length = 500)
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "branch_id")
    private Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    private Branch branch;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CtsServiceOrder> serviceOrders = new ArrayList<>();

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceOrderInvoice> serviceOrderInvoices = new ArrayList<>();

    public String getNumber() {
        return PrefixEnum.CS.name() + this.getCustomer().getId() + "/" + DateTimeFormatter.ofPattern("yy").format(this.getDocDate()) + "/" + String.format("%07d", this.getDocNumber());
    }

    public ContractShortDTO toContractShortDTO() {
        return new ContractShortDTO(this.getId(), this.getNumber(), this.getCreatedDate());
    }
}
