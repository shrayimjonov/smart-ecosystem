package uz.uzkassa.smartpos.domain.postgres.edocs.invoice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.dto.factura.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "draft_invoice")
@Getter
@Setter
@DynamicUpdate
public class DraftInvoice extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "seller_tin")
    private String sellerTin;
    @Column(name = "buyer_tin")
    private String buyerTin;
    @Column(name = "lot_id")
    private String lotId;

    @Column(name = "seller_name")
    private String sellerName;
    @Column(name = "seller_account")
    private String sellerAccount;
    @Column(name = "seller_bank_id")
    private String sellerBankId;
    @Column(name = "seller_address")
    private String sellerAddress;
    @Column(name = "seller_mobile")
    private String sellerMobile;
    @Column(name = "seller_work_phone")
    private String sellerWorkPhone;
    @Column(name = "seller_oked")
    private String sellerOked;
    @Column(name = "seller_district_id")
    private String sellerDistrictId;
    @Column(name = "seller_director")
    private String sellerDirector;
    @Column(name = "seller_accountant")
    private String sellerAccountant;
    @Column(name = "seller_vat_reg_code")
    private String sellerVatRegCode;
    @Column(name = "seller_vat_reg_status")
    private Integer sellerVatRegStatus;
    @Column(name = "seller_branch_code")
    private String sellerBranchCode;
    @Column(name = "seller_branch_name")
    private String sellerBranchName;
    @Column(name = "seller_tax_gap", precision = 25, scale = 2)
    private BigDecimal sellerTaxGap;

    @Column(name = "buyer_name")
    private String buyerName;
    @Column(name = "buyer_account")
    private String buyerAccount;
    @Column(name = "buyer_bank_id")
    private String buyerBankId;
    @Column(name = "buyer_address")
    private String buyerAddress;
    @Column(name = "buyer_mobile")
    private String buyerMobile;
    @Column(name = "buyer_work_phone")
    private String buyerWorkPhone;
    @Column(name = "buyer_oked")
    private String buyerOked;
    @Column(name = "buyer_district_id")
    private String buyerDistrictId;
    @Column(name = "buyer_director")
    private String buyerDirector;
    @Column(name = "buyer_accountant")
    private String buyerAccountant;
    @Column(name = "buyer_vat_reg_code")
    private String buyerVatRegCode;
    @Column(name = "buyer_vat_reg_status")
    private Integer buyerVatRegStatus;
    @Column(name = "buyer_branch_code")
    private String buyerBranchCode;
    @Column(name = "buyer_branch_name")
    private String buyerBranchName;
    @Column(name = "buyer_tax_gap", precision = 25, scale = 2)
    private BigDecimal buyerTaxGap;

    @OneToMany(mappedBy = "invoice",/* fetch = FetchType.EAGER, */cascade = {CascadeType.ALL}, orphanRemoval = true)
    @OrderBy("id")
    @JsonIgnore
    @BatchSize(size = 10)
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
        dto.setLotId(getLotId());
        dto.setSellerTin(getSellerTin());
        dto.setSeller(toSellerDTO());
        dto.setBuyerTin(getBuyerTin());
        dto.setBuyer(toBuyerDTO());
        dto.setProductList(getProductList());
        dto.setHasMarking(getHasMarking());
        return dto;
    }

    private FacturaCustomer toSellerDTO() {
        FacturaCustomer seller = new FacturaCustomer();
        seller.setName(getSellerName());
        seller.setTin(getSellerTin());
        seller.setAccount(getSellerAccount());
        seller.setBankId(getSellerBankId());
        seller.setAddress(getSellerAddress());
        seller.setMobile(getSellerMobile());
        seller.setWorkPhone(getSellerWorkPhone());
        seller.setOked(getSellerOked());
        seller.setDistrictId(getSellerDistrictId());
        seller.setDirector(getSellerDirector());
        seller.setAccountant(getSellerAccountant());
        seller.setVatRegCode(getSellerVatRegCode());
        seller.setVatRegStatus(getSellerVatRegStatus());
        seller.setBranchCode(getSellerBranchCode());
        seller.setBranchName(getSellerBranchName());
        seller.setTaxGap(getSellerTaxGap());
        return seller;
    }

    public void fromSellerDTO(FacturaCustomer seller) {
        if (StringUtils.isNotEmpty(seller.getTin())) {
            this.setSellerTin(seller.getTin());
        }
        this.setSellerName(seller.getName());
        this.setSellerAccount(seller.getAccount());
        this.setSellerBankId(seller.getBankId());
        this.setSellerAddress(seller.getAddress());
        this.setSellerMobile(seller.getMobile());
        this.setSellerWorkPhone(seller.getWorkPhone());
        this.setSellerOked(seller.getOked());
        this.setSellerDistrictId(seller.getDistrictId());
        if (StringUtils.isNotEmpty(seller.getDirector())) {
            this.setSellerDirector(seller.getDirector().replaceAll(" ", ""));
        }
        if (StringUtils.isNotEmpty(seller.getAccountant())) {
            this.setSellerAccountant(seller.getAccountant().replaceAll(" ", ""));
        }
        this.setSellerVatRegCode(seller.getVatRegCode());
        this.setSellerVatRegStatus(seller.getVatRegStatus());
        this.setSellerBranchCode(seller.getBranchCode());
        this.setSellerBranchName(seller.getBranchName());
        this.setSellerTaxGap(seller.getTaxGap());
    }

    private FacturaCustomer toBuyerDTO() {
        FacturaCustomer buyer = new FacturaCustomer();
        buyer.setName(getBuyerName());
        buyer.setTin(getBuyerTin());
        buyer.setAccount(getBuyerAccount());
        buyer.setBankId(getBuyerBankId());
        buyer.setAddress(getBuyerAddress());
        buyer.setMobile(getBuyerMobile());
        buyer.setWorkPhone(getBuyerWorkPhone());
        buyer.setOked(getBuyerOked());
        buyer.setDistrictId(getBuyerDistrictId());
        buyer.setDirector(getBuyerDirector());
        buyer.setAccountant(getBuyerAccountant());
        buyer.setVatRegCode(getBuyerVatRegCode());
        buyer.setVatRegStatus(getBuyerVatRegStatus());
        buyer.setBranchCode(getBuyerBranchCode());
        buyer.setBranchName(getBuyerBranchName());
        buyer.setTaxGap(getBuyerTaxGap());
        return buyer;
    }

    public void fromBuyerDTO(FacturaCustomer buyer) {
        if (StringUtils.isNotEmpty(buyer.getTin())) {
            this.setBuyerTin(buyer.getTin());
        }
        this.setBuyerName(buyer.getName());
        this.setBuyerAccount(buyer.getAccount());
        this.setBuyerBankId(buyer.getBankId());
        this.setBuyerAddress(buyer.getAddress());
        this.setBuyerMobile(buyer.getMobile());
        this.setBuyerWorkPhone(buyer.getWorkPhone());
        this.setBuyerOked(buyer.getOked());
        this.setBuyerDistrictId(buyer.getDistrictId());
        if (StringUtils.isNotEmpty(buyer.getDirector())) {
            this.setBuyerDirector(buyer.getDirector().replaceAll(" ", ""));
        }
        if (StringUtils.isNotEmpty(buyer.getAccountant())) {
            this.setBuyerAccountant(buyer.getAccountant().replaceAll(" ", ""));
        }
        this.setBuyerVatRegCode(buyer.getVatRegCode());
        this.setBuyerVatRegStatus(buyer.getVatRegStatus());
        this.setBuyerBranchCode(buyer.getBranchCode());
        this.setBuyerBranchName(buyer.getBranchName());
        this.setBuyerTaxGap(buyer.getTaxGap());
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
