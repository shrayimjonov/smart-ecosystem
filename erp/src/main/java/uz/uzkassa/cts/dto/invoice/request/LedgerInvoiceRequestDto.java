package uz.uzkassa.cts.dto.invoice.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.CompanyBank;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBank;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class LedgerInvoiceRequestDto implements Serializable {

    public LedgerInvoiceRequestDto() {
    }

    public LedgerInvoiceRequestDto(Long id, String documentNo, String transactionNumber, String contractNumber,
                                   ServiceType serviceType, BigDecimal amount, LocalDate paymentDate,
                                   String link, String description) {
        this.id = id;
        this.documentNo = documentNo;
        this.transactionNumber = transactionNumber;
        this.contractNumber = contractNumber;
        this.serviceType = serviceType;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.link = link;
        this.description = description;
    }

    public LedgerInvoiceRequestDto id(Long id) {
        this.id = id;
        return this;
    }

    public LedgerInvoiceRequestDto documentNo(String documentNo) {
        this.documentNo = documentNo;
        return this;
    }
    public LedgerInvoiceRequestDto transactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
        return this;
    }
    public LedgerInvoiceRequestDto contractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
        return this;
    }
    public LedgerInvoiceRequestDto serviceType(ServiceType serviceType) {
        this.serviceType = serviceType;
        return this;
    }
    public LedgerInvoiceRequestDto amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
    public LedgerInvoiceRequestDto paymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
        return this;
    }
    public LedgerInvoiceRequestDto link(String link) {
        this.link = link;
        return this;
    }

    public LedgerInvoiceRequestDto description(String description) {
        this.description = description;
        return this;
    }

    public LedgerInvoiceRequestDto company(Company company) {
        fillCompanyData(company);
        return this;
    }

    public LedgerInvoiceRequestDto customer(Customer customer) {
        fillCustomerData(customer);
        return this;
    }

    @NotNull
    private Long id;

    @NotNull
    private String documentNo;

    @NotNull
    private String transactionNumber;

    @Size(min = 20, max = 20)
    @NotNull
    private String payerAccountNumber;

    @Size(min = 9, max = 14)
    @NotNull
    private String payerIdentityNumber;

    private String payerName;

    @NotNull
    @Size(min = 5, max = 5)
    private String payerBankCode;

    private String payerBankName;

    @NotNull
    @Size(min = 20, max = 20)
    private String receiverAccountNumber;

    @NotNull
    @Size(min = 9, max = 14)
    private String receiverIdentityNumber;

    private String receiverName;

    @NotNull
    @Size(min = 5, max = 5)
    private String receiverBankCode;

    private String receiverBankName;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    private String description;

    @NotNull
    private String contractNumber;

    private String specificationNumber;

    private ServiceType serviceType;
    private String link;

    public void fillCompanyData(Company company) {
        CompanyBank companyBank = company.getCompanyBank();
        setReceiverAccountNumber(companyBank.getAccountNumber());
        setPayerBankName(companyBank.getBank().getNameNotNullable());
        setReceiverIdentityNumber(company.getTin());
        setReceiverName(company.getName() == null ? "" : company.getName());
        setReceiverBankCode(companyBank.getBank().getCode());
    }

    public void fillCustomerData(Customer customer) {
        CustomerBank customerBank = customer.getCustomerBank();
        if (customerBank == null) {
            throw new BadRequestException("Customer Bank not fount Customer tin: " + customer.getTin());
        }
        setPayerAccountNumber(customerBank.getAccountNumber());
        setPayerBankName(customerBank.getBank().getNameNotNullable());
        setPayerBankCode(customerBank.getBank().getCode());
        setPayerIdentityNumber(customer.getTin());
        setPayerName(customer.getName() == null ? "" : customer.getName());
    }

}
