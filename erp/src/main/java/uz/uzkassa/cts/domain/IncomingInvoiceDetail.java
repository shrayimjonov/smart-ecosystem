package uz.uzkassa.cts.domain;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "incoming_invoice_detail")
@Getter
@Setter
@SQLDelete(sql = "UPDATE incoming_invoice_detail SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class IncomingInvoiceDetail extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 6190015153L;

    private Integer version = 1;
    @Column(name = "factura_type")
    private Integer facturaType;
    @Column(name = "single_sided_type")
    private Integer singleSidedType = 0;
    @Column(name = "factura_id")
    private String facturaId;
    @Column(name = "invoice_id")
    private Long invoiceId;
    //--FacturaDocDTO -- details
    @Column(name = "factura_no")
    private String facturaNo;
    @Column(name = "factura_date")
    private LocalDate facturaDate;
    //--FacturaDocDTO -- details
    //--FacturaContractDTO -- details
    @Column(name = "contract_no")
    private String contractNo;
    @Column(name = "contract_date")
    private LocalDate contractDate;
    //--FacturaContractDTO -- details
    @Column(name = "empowerment_id")
    private String empowermentId;
    //--FacturaEmpowermentDTO --
    @Column(name = "agent_factura_id")
    private String agentFacturaId;
    @Column(name = "empowerment_no")
    private String empowermentNo;
    @Column(name = "empowerment_date_of_issue")
    private LocalDate empowermentDateOfIssue;
    @Column(name = "agent_tin")
    private String agentTin;
    @Column(name = "agent_pinfl")
    private String agentPinfl;
    @Column(name = "agent_fio")
    private String agentFio;
    //--FacturaEmpowermentDTO --

    //--FacturaItemReleasedDocDTO --
    @Column(name = "item_released_tin")
    private String itemReleasedTin;
    @Column(name = "item_released_fio")
    private String itemReleasedFio;
    @Column(name = "item_released_pinfl")
    private String itemReleasedPinfl;
    //--FacturaItemReleasedDocDTO --

    //--FacturaForeignCompanyDTO --
    @Column(name = "country_id")
    private String countryId;
    private String name;
    private String address;
    private String bank;
    private String account;
    //--FacturaForeignCompanyDTO --

    @Column(name = "seller_tin")
    private String sellerTin;
    @Column(name = "buyer_tin")
    private String buyerTin;
    @Column(name = "buyer_branch_tin")
    private String buyerBranchTin;

    //--FacturaCustomerDTO -- seller
    @Column(name = "seller_name")
    private String sellerName;
    @Column(name = "seller_account")
    private String sellerAccount;
    @Column(name = "seller_bank_id")
    private String sellerBankid;
    @Column(name = "seller_address")
    private String sellerAddress;
    @Column(name = "seller_mobile")
    private String sellerMobile;
    @Column(name = "seller_workphone")
    private String sellerWorkphone;
    @Column(name = "seller_oked")
    private String sellerOked;
    @Column(name = "seller_district_id")
    private String sellerDistrictid;
    @Column(name = "seller_director")
    private String sellerDirector;
    @Column(name = "seller_accountant")
    private String sellerAccountant;
    @Column(name = "seller_vatreg_code")
    private String sellerVatregcode;

    //--FacturaCustomerDTO -- seller

    //--FacturaCustomerDTO -- buyer
    @Column(name = "buyer_name")
    private String buyerName;
    @Column(name = "buyer_account")
    private String buyerAccount;
    @Column(name = "buyer_bank_id")
    private String buyerBankid;
    @Column(name = "buyer_address")
    private String buyerAddress;
    @Column(name = "buyer_mobile")
    private String buyerMobile;
    @Column(name = "buyer_workphone")
    private String buyerWorkphone;
    @Column(name = "buyer_oked")
    private String buyerOked;
    @Column(name = "buyer_district_id")
    private String buyerDistrictid;
    @Column(name = "buyer_director")
    private String buyerDirector;
    @Column(name = "buyer_accountant")
    private String buyerAccountant;
    @Column(name = "buyer_vatreg_code")
    private String buyerVatregcode;

    //--FacturaCustomerDTO -- buyer
    //--FacturaProductListDTO --
    @Column(name = "factura_product_id")
    private String facturaProductId;
    @Column(name = "empowerment_product_id")
    private String empowermentProductId;
    private String tin;
    @Column(name = "has_vat")
    private Boolean hasVat;
    @Column(name = "has_excise")
    private Boolean hasExcise;
    @Column(name = "has_comittent")
    private Boolean hasComittent;
    @Column(name = "has_medical")
    private Boolean hasMedical;
    @Column(name = "delivery_sum_with_vat")
    private BigDecimal deliverySumWithVat;

    @OneToMany(mappedBy = "incomingInvoiceDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncomingInvoiceProduct> products = new ArrayList<>();
    //--FacturaProductListDTO --

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incoming_invoice_id")
    private IncomingInvoice incomingInvoice;

    public void addChild(IncomingInvoiceProduct product) {
        product.setIncomingInvoiceDetail(this);
        products.add(product);
    }
}
