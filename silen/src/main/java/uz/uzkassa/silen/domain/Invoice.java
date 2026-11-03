package uz.uzkassa.silen.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.invoice.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "invoice")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@SQLDelete(sql = "UPDATE invoice SET deleted = true WHERE id = ?")
@DynamicUpdate
public class Invoice extends BaseIdentityEntity implements Serializable {
    @Column(name = "version", columnDefinition = "int default 1")
    private Integer version = 1;
    @Column(name = "type")
    private Integer type;
    @Column(name = "single_sided_type")
    private Integer singleSidedType;

    @Column(name = "old_factura_id", length = 24)
    private String oldFacturaId;
    @Column(name = "old_factura_no")
    private String oldFacturaNo;
    private LocalDate oldFacturaDate;

    @Column(name = "number")
    private String number;
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "contract_number")
    private String contractNumber;
    @Column(name = "contract_date")
    private LocalDate contractDate;

    @Column(name = "invest_id", length = 20)
    private String investId;
    @Column(name = "invest_name", length = 500)
    private String investName;

    @Column(name = "agent_factura_id", length = 24)
    private String agentFacturaId;
    @Column(name = "empowerment_no")
    private String empowermentNo;
    @Column(name = "empowerment_date_of_issue")
    private LocalDate empowermentDateOfIssue;
    @Column(name = "empowerment_date_of_expire")
    private LocalDate empowermentDateOfExpire;
    @Column(name = "agent_tin")
    private String agentTin;
    @Column(name = "agent_pinfl")
    private String agentPinfl;
    @Column(name = "agent_fio")
    private String agentFio;

    @Column(name = "item_released_tin")
    private String itemReleasedTin;
    @Column(name = "item_released_fio")
    private String itemReleasedFio;
    @Column(name = "item_released_pinfl")
    private String itemReleasedPinfl;

    @Column(name = "country_id")
    private String countryId;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "bank")
    private String bank;
    @Column(name = "account")
    private String account;

    @Column(name = "factura_product_id", length = 24)
    private String facturaProductId;
    @Column(name = "tin")
    private String tin;
    @Column(name = "has_vat")
    private Boolean hasVat;
    @Column(name = "has_excise")
    private Boolean hasExcise;
    @Column(name = "has_comittent")
    private Boolean hasComittent;
    @Column(name = "has_lgota")
    private Boolean hasLgota;
    @Column(name = "has_medical")
    private Boolean hasMedical;
    @Column(name = "has_marking")
    private Boolean hasMarking;

    @Column(name = "lot_id")
    private String lotId;

    @OneToMany(mappedBy = "invoice",/* fetch = FetchType.EAGER, */cascade = {CascadeType.ALL}, orphanRemoval = true)
    @OrderBy("id")
    @JsonIgnore
    @BatchSize(size = 10)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<InvoiceItem> items = new HashSet<>();

    public Set<InvoiceItem> getItems() {
        if (items == null) {
            items = new HashSet<>();
        }
        return items;
    }

    public void addItem(InvoiceItem item) {
        item.setInvoice(this);
        getItems().add(item);
    }

    public FacturaDTO toFacturaDTO() {
        FacturaDTO dto = new FacturaDTO();
        dto.setFacturaType(getType());
        dto.setSingleSidedType(getSingleSidedType());
        dto.setFacturaDoc(new FacturaDoc(getNumber(), getInvoiceDate()));
        if (StringUtils.isNotEmpty(getOldFacturaId())) {
            dto.setOldFacturaDoc(new OldFacturaDoc(getOldFacturaId(), getOldFacturaNo(), getOldFacturaDate()));
        }
        if (StringUtils.isNotEmpty(getContractNumber())) {
            dto.setContractDoc(new FacturaContract(getContractNumber(), getContractDate()));
        }
        if (StringUtils.isNotEmpty(getAgentFacturaId()) || StringUtils.isNotEmpty(getEmpowermentNo())) {
            dto.setFacturaEmpowermentDoc(toFacturaEmpowermentDTO());
        }
        if (StringUtils.isNoneEmpty(getItemReleasedFio(), getItemReleasedPinfl())) {
            dto.setItemReleasedDoc(new ItemReleasedDoc(getItemReleasedTin(), getItemReleasedFio(), getItemReleasedPinfl()));
        }
        if (StringUtils.isNotEmpty(getCountryId())) {
            dto.setForeignCompany(getForeignCompany());
        }
        if (getInvestId() != null) {
            dto.setFacturaInvestmentObjectDoc(new FacturaInvestmentObjectDoc(getInvestId(), getInvestName()));
        }
        dto.setLotId(getLotId());
        dto.setProductList(getProductList());
        dto.setHasMarking(getHasMarking());
        return dto;
    }

    private FacturaEmpowerment toFacturaEmpowermentDTO() {
        FacturaEmpowerment facturaEmpowermentDTO = new FacturaEmpowerment();
        facturaEmpowermentDTO.setAgentFacturaId(getAgentFacturaId());
        facturaEmpowermentDTO.setEmpowermentNo(getEmpowermentNo());
        facturaEmpowermentDTO.setEmpowermentDateOfIssue(getEmpowermentDateOfIssue());
        facturaEmpowermentDTO.setAgentTin(getAgentTin());
        facturaEmpowermentDTO.setAgentPinfl(getAgentPinfl());
        facturaEmpowermentDTO.setAgentFio(getAgentFio());
        return facturaEmpowermentDTO;
    }

    private FacturaForeignCompany getForeignCompany() {
        FacturaForeignCompany dto = new FacturaForeignCompany();
        dto.setCountryId(getCountryId());
        dto.setName(getName());
        dto.setAddress(getAddress());
        dto.setBank(getBank());
        dto.setAccount(getAccount());
        return dto;
    }

    private FacturaProductListDTO getProductList() {
        FacturaProductListDTO listDTO = new FacturaProductListDTO();
        listDTO.setFacturaProductId(getFacturaProductId());
        listDTO.setTin(getTin());
        listDTO.setHasVat(getHasVat());
        listDTO.setHasExcise(getHasExcise());
        listDTO.setHasComittent(getHasComittent());
        listDTO.setHasLgota(getHasLgota());
        listDTO.setHasMedical(getHasMedical());
        if (CollectionUtils.isNotEmpty(getItems())) {
            listDTO.setProducts(getItems().stream().map(InvoiceItem::toProductDTO).collect(Collectors.toList()));
        }
        return listDTO;
    }
}
