package uz.uzkassa.smartpossupply.domain.company;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;

import java.io.Serial;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "company_vat_registration",
        indexes = {
                @Index(columnList = "company_id", name = "vat_reg_company_idx")
        })
public class CompanyVatRegistration extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 9722542L;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "vat_reg_code")
    String vatRegCode;

    @Column(name = "vat_reg_status")
    Integer vatRegStatus;
}
