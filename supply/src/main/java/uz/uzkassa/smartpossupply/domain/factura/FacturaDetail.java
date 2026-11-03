package uz.uzkassa.smartpossupply.domain.factura;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.dto.factura.FacturaCompanyDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaContractDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaDocDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaEmpowermentDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaForeignCompanyDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaInvestmentObjectDoc;
import uz.uzkassa.smartpossupply.dto.factura.FacturaItemReleasedDocDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaProductListDTO;
import uz.uzkassa.smartpossupply.dto.factura.OldFacturaDoc;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;
import uz.uzkassa.smartpossupply.utils.DateUtils;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "factura_detail",
        indexes = {
                @Index(columnList = "factura_id", name = "factura_detail_factura_id_idx")
        })
@Slf4j
public class FacturaDetail extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 4444400834843L;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id")
    Factura factura;

    @Column(name = "version", columnDefinition = "int default 1")
    Integer version = 1;

    //SellerDoc
    @Column(name = "seller_account")
    String sellerAccount;

    @Column(name = "seller_bank_id")
    String sellerBankId;

    @Column(name = "seller_bank_name")
    String sellerBankName;

    @Column(name = "seller_address")
    String sellerAddress;

    @Column(name = "seller_mobile")
    String sellerMobile;

    @Column(name = "seller_work_phone")
    String sellerWorkPhone;

    @Column(name = "seller_oked")
    String sellerOked;

    @Column(name = "seller_director")
    String sellerDirector;

    @Column(name = "seller_accountant")
    String sellerAccountant;

    @Column(name = "seller_vat_reg_code")
    String sellerVatRegCode;

    @Column(name = "seller_vat_reg_status")
    Integer sellerVatRegStatus;

    @Column(name = "seller_branch_code")
    String sellerBranchCode;

    @Column(name = "seller_branch_name")
    String sellerBranchName;

    //Buyer
    @Column(name = "buyer_account")
    String buyerAccount;

    @Column(name = "buyer_bank_id")
    String buyerBankId;

    @Column(name = "buyer_bank_name")
    String buyerBankName;

    @Column(name = "buyer_address")
    String buyerAddress;

    @Column(name = "buyer_mobile")
    String buyerMobile;

    @Column(name = "buyer_work_phone")
    String buyerWorkPhone;

    @Column(name = "buyer_oked")
    String buyerOked;

    @Column(name = "buyer_director")
    String buyerDirector;

    @Column(name = "buyer_accountant")
    String buyerAccountant;

    @Column(name = "buyer_vat_reg_code")
    String buyerVatRegCode;

    @Column(name = "buyer_vat_reg_status")
    Integer buyerVatRegStatus;

    @Column(name = "buyer_branch_code")
    String buyerBranchCode;

    @Column(name = "buyer_branch_name")
    String buyerBranchName;


    //Old Factura
    @Column(name = "old_factura_id")
    String oldFacturaId;

    @Column(name = "old_factura_no")
    String oldFacturaNo;

    @Column(name = "old_factura_date")
    LocalDate oldFacturaDate;

    //FacturaDoc
    @Column(name = "nic_factura_id")
    String nicFacturaId;

    @Column(name = "factura_no")
    String facturaNo;

    @Column(name = "factura_date")
    LocalDate facturaDate;

    //FacturaContract
    @Column(name = "contract_no")
    String contractNo;

    @Column(name = "contract_date")
    LocalDate contractDate;

    //FacturaEmpowermentDoc
    @Column(name = "empowerment_id")
    String empowermentId;

    @Column(name = "agent_factura_id")
    String agentFacturaId;

    @Column(name = "empowerment_no")
    String empowermentNo;

    @Column(name = "empowerment_date_of_issue")
    LocalDate empowermentDateOfIssue;

    @Column(name = "agent_pinfl")
    String agentPinfl;

    @Column(name = "agent_fio")
    String agentFio;

    //ItemReleasedDoc
    @Column(name = "item_released_tin")
    String itemReleasedTin;

    @Column(name = "item_released_pinfl")
    String itemReleasedPinfl;

    @Column(name = "item_released_fio")
    String itemReleasedFio;

    //FacturaForeignCompany
    @Column(name = "foreign_country_id")
    String foreignCountryId;

    @Column(name = "foreign_company_name")
    String foreignCompanyName;

    @Column(name = "foreign_company_address")
    String foreignCompanyAddress;

    @Column(name = "foreign_company_bank")
    String foreignCompanyBank;

    @Column(name = "foreign_company_account")
    String foreignCompanyAccount;

    //FacturaProductListDTO
    @Deprecated
    @Column(name = "factura_product_id")
    String facturaProductId;

    @Column(name = "empowerment_product_Id")
    String empowermentProductId;

    @Column(name = "tin")
    String tin;

    @Column(name = "has_vat", columnDefinition = "boolean default false")
    Boolean hasVat;

    @Column(name = "has_excise", columnDefinition = "boolean default false")
    Boolean hasExcise;

    @Column(name = "has_committent", columnDefinition = "boolean default false")
    Boolean hasCommittent;

    @Column(name = "has_lgota", columnDefinition = "boolean default false")
    Boolean hasLgota;

    @Column(name = "has_medical", columnDefinition = "boolean default false")
    Boolean hasMedical;

    @Column(name = "hide_report_committent", columnDefinition = "boolean default false")
    Boolean hideReportCommittent;

    @Column(name = "delivery_sum_with_vat", precision = 25, scale = 2)
    BigDecimal deliverySumWithVat;

    @Column(name = "has_marking", columnDefinition = "boolean default false")
    Boolean hasMarking;

    @Column(name = "lot_id")
    String lotId;

    @Column(name = "delivery_branch_id")
    Long deliveryBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_branch_id", updatable = false, insertable = false)
    CustomerBranch deliveryBranch;

    @OneToMany(mappedBy = "facturaDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("sOrder")
    @JsonIgnore
    //@BatchSize(size = 50)
    Set<FacturaProduct> products = new HashSet<>();

    @Column(name = "invest_id")
    String investId;

    @Column(name = "invest_name")
    String investName;

    public void addProduct(FacturaProduct facturaProduct) {
        facturaProduct.setFacturaDetail(this);
        products.add(facturaProduct);
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FACTURA_DETAIL;
    }

    public FacturaDTO toFacturaDTO(boolean withProducts) {
        FacturaDTO facturaDTO = new FacturaDTO();
        facturaDTO.setFacturaDoc(new FacturaDocDTO(getFacturaNo(), getFacturaDate()));
        if (StringUtils.isNotEmpty(getOldFacturaId())) {
            facturaDTO.setOldFacturaDoc(toOldFacturaDocDTO());
        }
        if (StringUtils.isNotEmpty(getContractNo()) && getContractDate() != null) {
            facturaDTO.setContractDoc(toContractDTO());
        } else if (getFactura() != null && StringUtils.isNotEmpty(getFactura().getContractNo()) && getFactura().getContractDate() != null) {
            facturaDTO.setContractDoc(toContractDTO());
        }
        if (StringUtils.isNotEmpty(getAgentFacturaId()) || StringUtils.isNotEmpty(getEmpowermentNo())) {
            facturaDTO.setFacturaEmpowermentDoc(toEmpowermentDTO());
        }
        if (StringUtils.isNoneEmpty(getItemReleasedFio(), getItemReleasedPinfl())) {
            facturaDTO.setItemReleasedDoc(toFacturaItemReleasedDocDTO());
        }
        if (StringUtils.isNotEmpty(getForeignCountryId())) {
            facturaDTO.setForeignCompany(getForeignCompanyDTO());
        }
        facturaDTO.setLotId(getLotId());
        if (withProducts) {
            facturaDTO.setProductList(getProductListDTO());
        }
        facturaDTO.setHasMarking(getHasMarking());

        if (StringUtils.isNoneEmpty(getInvestId(), getInvestName())) {
            facturaDTO.setFacturaInvestmentObjectDoc(toFacturaInvestmentObjectDocDTO());
        }
        return facturaDTO;
    }

    public void fromFacturaDocDTO(FacturaDocDTO facturaDocDTO) {
        setFacturaNo(facturaDocDTO.getFacturaNo());
        setFacturaDate(facturaDocDTO.getFacturaDate());
    }

    public void fromSellerDTO(FacturaCompanyDTO sellerDTO) {
        setSellerAccount(sellerDTO.getAccount());
        setSellerBankId(sellerDTO.getBankId());
        setSellerBankName(sellerDTO.getBankName());
        setSellerAddress(sellerDTO.getAddress());
        setSellerMobile(sellerDTO.getMobile());
        setSellerWorkPhone(sellerDTO.getWorkPhone());
        setSellerOked(sellerDTO.getOked());
        setSellerDirector(sellerDTO.getDirector());
        setSellerAccountant(sellerDTO.getAccountant());
        setSellerVatRegCode(sellerDTO.getVatRegCode());
        setSellerVatRegStatus(sellerDTO.getVatRegStatus());
        setSellerBranchCode(sellerDTO.getBranchCode());
        setSellerBranchName(sellerDTO.getBranchName());
    }

    public void fromBuyerDTO(FacturaCompanyDTO buyerDTO) {
        setBuyerAccount(buyerDTO.getAccount());
        setBuyerBankId(buyerDTO.getBankId());
        setBuyerBankName(buyerDTO.getBankName());
        setBuyerAddress(buyerDTO.getAddress());
        setBuyerMobile(buyerDTO.getMobile());
        setBuyerWorkPhone(buyerDTO.getWorkPhone());
        setBuyerOked(buyerDTO.getOked());
        setBuyerDirector(buyerDTO.getDirector());
        setBuyerAccountant(buyerDTO.getAccountant());
        setBuyerVatRegCode(buyerDTO.getVatRegCode());
        setBuyerVatRegStatus(buyerDTO.getVatRegStatus());
        setBuyerBranchCode(buyerDTO.getBranchCode());
        setBuyerBranchName(buyerDTO.getBranchName());
    }

    public FacturaEmpowermentDTO toEmpowermentDTO() {
        FacturaEmpowermentDTO facturaEmpowermentDTO = new FacturaEmpowermentDTO();
        facturaEmpowermentDTO.setAgentFacturaId(getAgentFacturaId());
        facturaEmpowermentDTO.setEmpowermentNo(getEmpowermentNo());
        if (getEmpowermentDateOfIssue() != null) {
            facturaEmpowermentDTO.setEmpowermentDateOfIssue(getEmpowermentDateOfIssue().toString());
        }
        facturaEmpowermentDTO.setAgentPinfl(getAgentPinfl());
        facturaEmpowermentDTO.setAgentFio(getAgentFio());
        return facturaEmpowermentDTO;
    }

    public OldFacturaDoc toOldFacturaDocDTO() {
        return new OldFacturaDoc(getOldFacturaId(), getOldFacturaNo(), getOldFacturaDate());
    }

    public FacturaContractDTO toContractDTO() {
        return new FacturaContractDTO(Optional.ofNullable(getContractNo()).orElse(getFactura().getContractNo()), Optional.ofNullable(getContractDate()).orElse(getFactura().getContractDate()).toString());
    }

    public FacturaItemReleasedDocDTO toFacturaItemReleasedDocDTO() {
        return new FacturaItemReleasedDocDTO(
                getItemReleasedTin(),
                getItemReleasedFio(),
                getItemReleasedPinfl()
        );
    }

    public FacturaForeignCompanyDTO getForeignCompanyDTO() {
        FacturaForeignCompanyDTO facturaForeignCompanyDTO = new FacturaForeignCompanyDTO();
        facturaForeignCompanyDTO.setCountryId(getForeignCountryId());
        facturaForeignCompanyDTO.setName(getForeignCompanyName());
        facturaForeignCompanyDTO.setAddress(getForeignCompanyAddress());
        facturaForeignCompanyDTO.setBank(getForeignCompanyBank());
        facturaForeignCompanyDTO.setAccount(getForeignCompanyAccount());
        return facturaForeignCompanyDTO;
    }

    public FacturaProductListDTO getProductListDTO() {
        FacturaProductListDTO facturaProductListDTO = new FacturaProductListDTO();
        if (getFactura() != null && getFactura().getFacturaProductId() != null) {
            facturaProductListDTO.setFacturaProductId(getFactura().getFacturaProductId());
        } else {
            facturaProductListDTO.setFacturaProductId(getFacturaProductId());
        }
        facturaProductListDTO.setTin(getTin());
        facturaProductListDTO.setHasVat(getHasVat());
        facturaProductListDTO.setHasExcise(getHasExcise());
        facturaProductListDTO.setHasCommittent(getHasCommittent());
        facturaProductListDTO.setHasLgota(getHasLgota());
        facturaProductListDTO.setHasMedical(getHasMedical());
        facturaProductListDTO.setHideReportCommittent(getHideReportCommittent());
        if (!CollectionUtils.isEmpty(getProducts())) {
            facturaProductListDTO.setProducts(getProducts().stream().map(FacturaProduct::toProductDTO).toList());
        }
        return facturaProductListDTO;
    }

    public FacturaCompanyDTO toSellerDTO(Company seller) {
        FacturaCompanyDTO facturaSellerDTO = new FacturaCompanyDTO();
        facturaSellerDTO.setTin(seller.getTin());
        facturaSellerDTO.setName(seller.getName());
        facturaSellerDTO.setAccount(getSellerAccount());
        facturaSellerDTO.setBankId(getSellerBankId());
        if (StringUtils.isNotEmpty(getSellerBankName())) {
            facturaSellerDTO.setBankName(getSellerBankName());
        } else if (seller.getCompanyBank() != null) {
            facturaSellerDTO.setBankName(seller.getCompanyBank().getBank().getName());
        }
        facturaSellerDTO.setAddress(getSellerAddress());
        facturaSellerDTO.setMobile(getSellerMobile());
        facturaSellerDTO.setWorkPhone(getSellerWorkPhone());
        facturaSellerDTO.setOked(getSellerOked());
        if (seller.getAddress() != null) {
            if (seller.getAddress().getDistrict() != null) {
                facturaSellerDTO.setDistrictName(seller.getAddress().getDistrict().getName());
            }
            if (seller.getAddress().getRegion() != null) {
                facturaSellerDTO.setRegionName(seller.getAddress().getRegion().getName());
            }
        }

        facturaSellerDTO.setDirector(getSellerDirector());
        facturaSellerDTO.setAccountant(getSellerAccountant());
        facturaSellerDTO.setVatRegCode(getSellerVatRegCode());
        facturaSellerDTO.setVatRegStatus(getSellerVatRegStatus());
        facturaSellerDTO.setBranchName(getSellerBranchName());
        facturaSellerDTO.setBranchCode(getSellerBranchCode());
        return facturaSellerDTO;
    }

    public FacturaCompanyDTO toBuyerDTO(Company buyer) {
        FacturaCompanyDTO facturaBuyerDTO = new FacturaCompanyDTO();
        facturaBuyerDTO.setTin(buyer.getTin());
        facturaBuyerDTO.setName(buyer.getName());
        facturaBuyerDTO.setAccount(getBuyerAccount());
        facturaBuyerDTO.setBankId(getBuyerBankId());
        facturaBuyerDTO.setBranchCode(getBuyerBranchCode());
        facturaBuyerDTO.setBranchName(getBuyerBranchName());
        if (StringUtils.isNotEmpty(getBuyerBankName())) {
            facturaBuyerDTO.setBankName(getBuyerBankName());
        } else if (buyer.getCompanyBank() != null) {
            facturaBuyerDTO.setBankName(buyer.getCompanyBank().getBank().getName());
        }
        facturaBuyerDTO.setAddress(getBuyerAddress());
        facturaBuyerDTO.setMobile(getBuyerMobile());
        facturaBuyerDTO.setWorkPhone(getBuyerWorkPhone());
        facturaBuyerDTO.setOked(getBuyerOked());
        //facturaBuyerDTO.setDistrictId(getBuyerDistrictId());
        if (buyer.getAddress() != null) {
            if (buyer.getAddress().getDistrict() != null) {
                facturaBuyerDTO.setDistrictName(buyer.getAddress().getDistrict().getName());
            }
            if (buyer.getAddress().getRegion() != null) {
                facturaBuyerDTO.setRegionName(buyer.getAddress().getRegion().getName());
            }
        }
        facturaBuyerDTO.setDirector(getBuyerDirector());
        facturaBuyerDTO.setAccountant(getBuyerAccountant());
        facturaBuyerDTO.setVatRegCode(getBuyerVatRegCode());
        facturaBuyerDTO.setVatRegStatus(getBuyerVatRegStatus());

        return facturaBuyerDTO;
    }

    public FacturaInvestmentObjectDoc toFacturaInvestmentObjectDocDTO() {
        return new FacturaInvestmentObjectDoc(getInvestId(), getInvestName());
    }


    public void fromFacturaInvestmentObjectDocDTO(FacturaInvestmentObjectDoc facturaInvestmentObjectDoc) {
       if(facturaInvestmentObjectDoc != null) {
           setInvestId(facturaInvestmentObjectDoc.getObjectId());
           setInvestName(facturaInvestmentObjectDoc.getObjectName());
       }
    }

    public void fromOldFacturaDTO(OldFacturaDoc oldFacturaDoc) {
        setOldFacturaId(oldFacturaDoc.getOldFacturaId());
        setOldFacturaNo(oldFacturaDoc.getOldFacturaNo());
        setOldFacturaDate(oldFacturaDoc.getOldFacturaDate());
    }

    public void fromEmpowermentDTO(FacturaEmpowermentDTO facturaEmpowermentDTO) {
        setAgentFacturaId(facturaEmpowermentDTO.getAgentFacturaId());
        setEmpowermentNo(facturaEmpowermentDTO.getEmpowermentNo());
        if (StringUtils.isNotEmpty(facturaEmpowermentDTO.getEmpowermentDateOfIssue())) {
            try {
                setEmpowermentDateOfIssue(LocalDate.parse(facturaEmpowermentDTO.getEmpowermentDateOfIssue(), DateUtils.YEAR_MONTH_DAY_HYPHEN_FORMATTER));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        setAgentPinfl(facturaEmpowermentDTO.getAgentPinfl());
        setAgentFio(facturaEmpowermentDTO.getAgentFio());
    }

    public void fromItemReleasedDocDTO(FacturaItemReleasedDocDTO facturaItemReleasedDocDTO) {
        setItemReleasedTin(facturaItemReleasedDocDTO.getItemReleasedTin());
        setItemReleasedPinfl(facturaItemReleasedDocDTO.getItemReleasedPinfl());
        setItemReleasedFio(facturaItemReleasedDocDTO.getItemReleasedFio());
    }

    public void fromForeignCompanyDTO(FacturaForeignCompanyDTO facturaEmpowermentDTO) {
        setForeignCountryId(facturaEmpowermentDTO.getCountryId());
        setForeignCompanyName(facturaEmpowermentDTO.getName());
        setForeignCompanyAddress(facturaEmpowermentDTO.getAddress());
        setForeignCompanyBank(facturaEmpowermentDTO.getBank());
        setForeignCompanyAccount(facturaEmpowermentDTO.getAccount());
    }

    public void fromProductListDTO(FacturaProductListDTO facturaProductListDTO) {
        setFacturaProductId(facturaProductListDTO.getFacturaProductId());
        setEmpowermentProductId(facturaProductListDTO.getEmpowermentProductId());
        setTin(facturaProductListDTO.getTin());
        setHasVat(Optional.ofNullable(facturaProductListDTO.getHasVat()).orElse(Boolean.FALSE));
        setHasExcise(Optional.ofNullable(facturaProductListDTO.getHasExcise()).orElse(Boolean.FALSE));
        setHasCommittent(Optional.ofNullable(facturaProductListDTO.getHasCommittent()).orElse(Boolean.FALSE));
        setHasLgota(Optional.ofNullable(facturaProductListDTO.getHasLgota()).orElse(Boolean.FALSE));
        setHasMedical(Optional.ofNullable(facturaProductListDTO.getHasMedical()).orElse(Boolean.FALSE));
        setHideReportCommittent(Optional.ofNullable(facturaProductListDTO.getHideReportCommittent()).orElse(Boolean.FALSE));
        setDeliverySumWithVat(facturaProductListDTO.getDeliverySumWithVat());
    }
}
