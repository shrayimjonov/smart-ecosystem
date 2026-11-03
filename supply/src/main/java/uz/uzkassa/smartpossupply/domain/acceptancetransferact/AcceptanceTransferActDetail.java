package uz.uzkassa.smartpossupply.domain.acceptancetransferact;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "acceptance_transfer_act_detail"
)
@SQLDelete(sql = "UPDATE acceptance_transfer_act_detail SET deleted = true WHERE id=?")
public class AcceptanceTransferActDetail extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 289382631L;

    //Seller
    @Column(name = "seller_name")
    String sellerName;

    @Column(name = "seller_address")
    String sellerAddress;

    @Column(name = "seller_account")
    String sellerAccount;

    @Column(name = "seller_bank_id")
    String sellerBankId;

    //Buyer
    @Column(name = "buyer_name")
    String buyerName;

    @Column(name = "buyer_address")
    String buyerAddress;

    @Column(name = "buyer_account")
    String buyerAccount;

    @Column(name = "buyer_bank_id")
    String buyerBankId;

    @Column(name = "buyer_vat_reg_code")
    String buyerVatRegCode;

    @Column(name = "buyer_vat_reg_status")
    Integer buyerVatRegStatus;

    @Column(name = "buyer_branch_code")
    String buyerBranchCode;

    @Column(name = "buyer_branch_name")
    String buyerBranchName;

    //Contract
    @Column(name = "contract_no")
    String contractNo;

    @Column(name = "contract_date")
    LocalDate contractDate;

    @OneToMany(mappedBy = "acceptanceTransferActDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordNo")
    @JsonIgnore
    Set<AcceptanceTransferActProduct> products = new HashSet<>();

    public void addProduct(AcceptanceTransferActProduct acceptanceTransferActProduct) {
        acceptanceTransferActProduct.setAcceptanceTransferActDetailId(this.getId());
        acceptanceTransferActProduct.setAcceptanceTransferActDetail(this);
        products.add(acceptanceTransferActProduct);
    }
}
