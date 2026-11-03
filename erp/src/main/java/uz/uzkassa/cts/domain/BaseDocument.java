package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ThymeleafTemplateType;

import java.io.Serial;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public class BaseDocument extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4441842109L;

    @NotNull
    @Column(name = "name", unique = true)
    private String name;
    @NotNull
    @Column(name = "doc_number")
    private Long docNumber;
    @NotNull
    @Column(name = "doc_date")
    private LocalDate docDate;
    @NotNull
    @Column(name = "doc_end_date")
    private LocalDate docEndDate;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "template_type")
    private ThymeleafTemplateType templateTypeEnum;

    @Column(name = "company_name")
    private String companyName;
    @Column(name = "company_address")
    private String companyAddress;
    @Column(name = "company_account_number")
    private String companyAccountNumber;
    @Column(name = "company_bank_name")
    private String companyBankName;
    @Column(name = "company_bank_mfo")
    private String companyBankMFO;
    @Column(name = "company_tin")
    private String companyTin;
    @Column(name = "company_oked")
    private String companyOKED;
    @Column(name = "company_phone_number")
    private String companyPhoneNumber;
    @Column(name = "company_director_full_name")
    private String companyDirectorFullName;

    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "customer_address")
    private String customerAddress;
    @Column(name = "customer_account_number")
    private String customerAccountNumber;
    @Column(name = "customer_bank_name")
    private String customerBankName;
    @Column(name = "customer_bank_mfo")
    private String customerBankMFO;
    @Column(name = "customer_tin")
    private String customerTin;
    @Column(name = "customer_oked")
    private String customerOKED;
    @Column(name = "customer_phone_number")
    private String customerPhoneNumber;
    @Column(name = "customer_director_full_name")
    private String customerDirectorFullName;


    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "company_id")
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

}
