package uz.uzkassa.horeca.domain.factura;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.factura.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "factura_detail", indexes = { @Index(columnList = "factura_id", name = "factura_detail_factura_id_idx") })
@Slf4j
public class FacturaDetail extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4444400834843L;

    @Column(name = "factura_id")
    Long facturaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", updatable = false, insertable = false)
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
    String contractDate;

    //FacturaEmpowermentDoc
    @Column(name = "empowerment_id")
    String empowermentId;

    @Column(name = "agent_factura_id")
    String agentFacturaId;

    @Column(name = "empowerment_no")
    String empowermentNo;

    @Column(name = "empowerment_date_of_issue")
    String empowermentDateOfIssue;

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

    @Column(name = "delivery_sum_with_vat", scale = 2, precision = 25)
    BigDecimal deliverySumWithVat;

    @Column(name = "has_marking", columnDefinition = "boolean default false")
    Boolean hasMarking;

    @Column(name = "lot_id")
    String lotId;

    @OneToMany(mappedBy = "facturaDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    @JsonIgnore
    @BatchSize(size = 50)
    Set<FacturaProduct> products = new HashSet<>();

    public void addProduct(FacturaProduct facturaProduct) {
        facturaProduct.setFacturaDetail(this);
        products.add(facturaProduct);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FacturaDetail)) {
            return false;
        }
        return id != null && id.equals(((FacturaDetail) o).id);
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

    public FacturaCompanyDTO toSellerDTO(Company seller) {
        FacturaCompanyDTO facturaSellerDTO = new FacturaCompanyDTO();
        facturaSellerDTO.setTin(seller.getTin());
        facturaSellerDTO.setName(seller.getName());
        facturaSellerDTO.setAccount(getSellerAccount());
        facturaSellerDTO.setBankId(getSellerBankId());
        facturaSellerDTO.setBankName(getSellerBankName());
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
        return facturaSellerDTO;
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

    public FacturaCompanyDTO toBuyerDTO(Company buyer) {
        FacturaCompanyDTO facturaBuyerDTO = new FacturaCompanyDTO();
        facturaBuyerDTO.setTin(buyer.getTin());
        facturaBuyerDTO.setName(buyer.getName());
        facturaBuyerDTO.setAccount(getBuyerAccount());
        facturaBuyerDTO.setBankId(getBuyerBankId());
        facturaBuyerDTO.setBranchCode(getBuyerBranchCode());
        facturaBuyerDTO.setBranchName(getBuyerBranchName());
        facturaBuyerDTO.setBankName(getBuyerBankName());
        facturaBuyerDTO.setAddress(getBuyerAddress());
        facturaBuyerDTO.setMobile(getBuyerMobile());
        facturaBuyerDTO.setWorkPhone(getBuyerWorkPhone());
        facturaBuyerDTO.setOked(getBuyerOked());
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

    public void fromOldFacturaDocDTO(OldFacturaDoc oldFacturaDoc) {
        setOldFacturaId(oldFacturaDoc.getOldFacturaId());
        setOldFacturaNo(oldFacturaDoc.getOldFacturaNo());
        setOldFacturaDate(oldFacturaDoc.getOldFacturaDate());
    }

    public OldFacturaDoc toOldFacturaDocDTO() {
        return new OldFacturaDoc(getOldFacturaId(), getOldFacturaNo(), getOldFacturaDate());
    }

    public void fromContractDTO(FacturaContractDTO contractDocDTO) {
        setContractNo(contractDocDTO.getContractNo());
        setContractDate(contractDocDTO.getContractDate());
    }

    public FacturaContractDTO toContractDTO() {
        return new FacturaContractDTO(getContractNo(), getContractDate());
    }

    public void fromEmpowermentDTO(FacturaEmpowermentDTO facturaEmpowermentDocDTO) {
        setAgentFacturaId(facturaEmpowermentDocDTO.getAgentFacturaId());
        setEmpowermentNo(facturaEmpowermentDocDTO.getEmpowermentNo());
        setEmpowermentDateOfIssue(facturaEmpowermentDocDTO.getEmpowermentDateOfIssue());
        setAgentPinfl(facturaEmpowermentDocDTO.getAgentPinfl());
        setAgentFio(facturaEmpowermentDocDTO.getAgentFio());
    }

    public FacturaEmpowermentDTO toEmpowermentDTO() {
        FacturaEmpowermentDTO facturaEmpowermentDTO = new FacturaEmpowermentDTO();
        facturaEmpowermentDTO.setAgentFacturaId(getAgentFacturaId());
        facturaEmpowermentDTO.setEmpowermentNo(getEmpowermentNo());
        facturaEmpowermentDTO.setEmpowermentDateOfIssue(getEmpowermentDateOfIssue());
        facturaEmpowermentDTO.setAgentPinfl(getAgentPinfl());
        facturaEmpowermentDTO.setAgentFio(getAgentFio());
        return facturaEmpowermentDTO;
    }

    public void fromForeignCompanyDTO(FacturaForeignCompanyDTO facturaForeignCompanyDTO) {
        setForeignCountryId(facturaForeignCompanyDTO.getCountryId());
        setForeignCompanyName(facturaForeignCompanyDTO.getName());
        setForeignCompanyAddress(facturaForeignCompanyDTO.getAddress());
        setForeignCompanyBank(facturaForeignCompanyDTO.getBank());
        setForeignCompanyAccount(facturaForeignCompanyDTO.getAccount());
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

    public void fromItemReleasedDocDTO(FacturaItemReleasedDocDTO facturaItemReleasedDocDTO) {
        setItemReleasedTin(facturaItemReleasedDocDTO.getItemReleasedTin());
        setItemReleasedFio(facturaItemReleasedDocDTO.getItemReleasedFio());
        setItemReleasedPinfl(facturaItemReleasedDocDTO.getItemReleasedPinfl());
    }

    public FacturaItemReleasedDocDTO toFacturaItemReleasedDocDTO() {
        return new FacturaItemReleasedDocDTO(getItemReleasedTin(), getItemReleasedFio(), getItemReleasedPinfl());
    }

    public FacturaProductListDTO toProductListDTO(FacturaDetail facturaDetail) {
        FacturaProductListDTO facturaProductListDTO = new FacturaProductListDTO();
        facturaProductListDTO.setFacturaProductId(getFacturaProductId());
        facturaProductListDTO.setEmpowermentProductId(getEmpowermentProductId());
        facturaProductListDTO.setTin(facturaDetail.getTin());
        facturaProductListDTO.setHasVat(facturaDetail.getHasVat());
        facturaProductListDTO.setHasExcise(facturaDetail.getHasExcise());
        facturaProductListDTO.setHasCommittent(facturaDetail.getHasCommittent());
        facturaProductListDTO.setHasLgota(facturaDetail.getHasLgota());
        facturaProductListDTO.setHasMedical(facturaDetail.getHasMedical());
        facturaProductListDTO.setHideReportCommittent(facturaDetail.getHideReportCommittent());
        facturaProductListDTO.setDeliverySumWithVat(facturaDetail.getDeliverySumWithVat());
        if (!CollectionUtils.isEmpty(facturaDetail.getProducts())) {
            facturaProductListDTO.setProducts(
                facturaDetail.getProducts().stream().map(FacturaProduct::toProductDTO).collect(Collectors.toList())
            );
        }
        return facturaProductListDTO;
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
