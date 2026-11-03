package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.dialect.SpringStandardDialect;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.applicationfile.ApplicationTemplateDTO;
import uz.uzkassa.cts.dto.cases.request.*;
import uz.uzkassa.cts.dto.contractforleasing.request.GeneratePoaTemplateDTO;
import uz.uzkassa.cts.dto.customer.CustomerFiscalModuleTemplateDTO;
import uz.uzkassa.cts.dto.payment.PaymentActPoaRequestDTO;
import uz.uzkassa.cts.dto.payment.PaymentContractTemplateDTO;
import uz.uzkassa.cts.dto.paymentCompanyContract.PaymentCompanyContractTemplateDTO;
import uz.uzkassa.cts.dto.paymentCompanyQuote.PaymentCompanyQuoteTemplateDTO;
import uz.uzkassa.cts.dto.paymentQuote.PaymentQuoteTemplateDTO;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActPowerOfAttorneyDTO;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActTemplateDTO;
import uz.uzkassa.cts.dto.sale.act.SaleActPowerOfAttorneyDTO;
import uz.uzkassa.cts.dto.sale.contract.SaleContractAccessoryListWithVatDTO;
import uz.uzkassa.cts.dto.template.ThymeleafDTO;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInActPoaRequestDto;
import uz.uzkassa.cts.enums.CustomerVatTypeEnum;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.AddressService;
import uz.uzkassa.cts.service.ThymeleafService;
import uz.uzkassa.cts.utils.NumberWordConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Transactional(readOnly = true)
@Service
public class ThymeleafServiceImpl extends BaseServiceImpl<Thymeleaf, Long> implements ThymeleafService {

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final SpringTemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final CustomerEmployeeRepository customerEmployeeRepository;
    private final AddressService addressService;

    public ThymeleafServiceImpl(ThymeleafRepository thymeleafRepository, CompanyRepository companyRepository, CustomerRepository customerRepository, SpringTemplateEngine templateEngine, UserRepository userRepository, RegionRepository regionRepository, DistrictRepository districtRepository, NeighborhoodRepository neighborhoodRepository, CustomerEmployeeRepository customerEmployeeRepository, AddressService addressService) {
        super(thymeleafRepository);
        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;
        this.templateEngine = templateEngine;
        this.userRepository = userRepository;
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.neighborhoodRepository = neighborhoodRepository;
        this.customerEmployeeRepository = customerEmployeeRepository;
        this.addressService = addressService;
        this.templateEngine.setDialect(new SpringStandardDialect());
    }

    @Override
    public String generateHTML(ThymeleafDTO thymeleafDTO) {

        Company company = companyRepository.getReferenceById(thymeleafDTO.getCompanyId());
        Customer customer = customerRepository.getReferenceById(thymeleafDTO.getCustomerId());

        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("company", company);
        context.setVariable("customer", customer);

        return switch (thymeleafDTO.getTemplateType()) {
            case CONTRACT_FOR_BUY -> templateEngine.process("Contact_for_buy", context);
            case CONTRACT_FOR_SERVICE -> templateEngine.process("Contact_for_service", context);
            case SPECIFICATION -> templateEngine.process("Specification", context);
            case POWER_ATTORNEY -> templateEngine.process("PowerAttorney", context);
            case INVOICE -> templateEngine.process("Invoice", context);
            default -> null;
        };
    }

    @Override
    public String generateContractForBuyHTML(Contract contract) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("contract", contract);
        switch (contract.getTemplateTypeEnum()) {
            case CONTRACT_FOR_BUY, CONTRACT_FOR_SERVICE -> {
                if (contract.getCreatedDate().toLocalDate().isAfter(LocalDate.of(2020, 8, 14)))
                    return templateEngine.process("Contract_for_buy_after_14_08_2020", context);
                return templateEngine.process("Contract_for_buy", context);
            }
            case CONTRACT_FOR_LEASING -> {
                return templateEngine.process("Contract_for_leasing", context);
            }
            case CONTRACT_FOR_RENT -> {
                return templateEngine.process("Contract_for_rent", context);
            }
            default -> templateEngine.process("Contract_for_buy_after_14_08_2020", context);
        }
        return templateEngine.process("Contract_for_buy_after_14_08_2020", context);
    }


    @Override
    public String generateSpecificationForServiceHTML(String serialNumbers, CtsServiceOrderSpecification specification) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("serSpec", specification);
        context.setVariable("serialNumbers", serialNumbers);
        if (specification.getCreatedDate().toLocalDate().isAfter(LocalDate.of(2020, 8, 14)))
            return templateEngine.process("Specification_for_service_new_with_vat_after_14_08_2020", context);
        else if (specification.getCreatedDate().toLocalDate().isAfter(LocalDate.of(2020, 7, 1)))
            return templateEngine.process("Specification_for_service_new_with_vat", context);
        else if (specification.getCreatedDate().toLocalDate().isAfter(LocalDate.of(2020, 2, 5)))
            return templateEngine.process("Specification_for_service_new", context);
        return templateEngine.process("Specification_for_service", context);
    }

    @Override
    public String generateActOfTransmissionPoaHTML(ActOfTransmission actOfTransmission, GeneratePoaTemplateDTO dto) {
        dto.setQtyInWord(NumberWordConverter.convert(dto.getQty()));
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("actOfTransmission", actOfTransmission);
        context.setVariable("dto", dto);
        customerEmployeeRepository
                .findFirstByCustomerIdAndRole(actOfTransmission.getContract().getCustomerId(), Role.DIRECTOR)
                .ifPresent(customerEmployee -> context.setVariable("director", customerEmployee.getName()));
        return templateEngine.process("Power_of_attorney_sts", context);
    }

    @Override
    public String generateActOfTransmissionPrimePoaHTML(ActOfTransmission actOfTransmission, GeneratePoaTemplateDTO dto) {
        dto.setQtyInWord(NumberWordConverter.convert(dto.getQty()));
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("actOfTransmission", actOfTransmission);
        context.setVariable("primDto", dto);
        customerEmployeeRepository
                .findFirstByCustomerIdAndRole(actOfTransmission.getContract().getCustomerId(), Role.DIRECTOR)
                .ifPresent(customerEmployee -> context.setVariable("director", customerEmployee.getName()));
        return templateEngine.process("contractForLeasing/powerAttorneyPrime", context);
    }

    @Override
    public String generateServiceInvoiceHTML(ServiceOrderInvoice invoice) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("invoice", invoice);
        if (invoice.getCreatedDate().toLocalDate().isAfter(LocalDate.of(2020, 8, 14)))
            return templateEngine.process("invoice_for_service_with_vat_after_14_08_2020", context);
        else if (invoice.getCreatedDate().toLocalDate().isAfter(LocalDate.of(2020, 7, 1)))
            return templateEngine.process("invoice_for_service_with_vat", context);
        return templateEngine.process("invoice_for_service", context);
    }

    @Override
    public String generateOneTimeSpecHTML(OneTimeOrder order) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("order", order);
        if (order.getCreatedDate().toLocalDate().isAfter(LocalDate.of(2020, 8, 14)))
            return templateEngine.process("One_time_specification_after_14_08_2020", context);
        return templateEngine.process("One_time_specification", context);
    }

    @Override
    public String generateCronBlockTextHTML() {
        Context context = SecurityUtils.getDefaultContext();
        return templateEngine.process("CronTemplates/CronBlockText", context);
    }

    @Override
    public String generateOneTimeOrderInvoiceHTML(OneTimeOrderInvoice invoice) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("oneTimeInvoice", invoice);
        if (invoice.getCreatedDate().toLocalDate().isAfter(LocalDate.of(2020, 8, 14)))
            return templateEngine.process("one_time_order_invoice_with_vat_after_14_08_2020", context);
        else if (invoice.getCreatedDate().toLocalDate().isAfter(LocalDate.of(2020, 7, 1)))
            return templateEngine.process("one_time_order_invoice_with_vat", context);
        return templateEngine.process("one_time_order_invoice", context);
    }

    @Override
    public String generateApplicationTemplate(Fiscalization fiscalization, ApplicationTemplateDTO applicationTemplateDTO) {

        User currentUser = userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
        Address address = addressService.getPermanentAddress(currentUser.getEntityId(), currentUser.getEntityType());
        if (address == null) {
            throw validationService.entityNotFoundExceptionThrow(ADDRESS).get();
        }
        if (address.getRegion() == null) {
            throw validationService.entityNotFoundExceptionThrow(REGION).get();
        }

        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("fiscalization", fiscalization);
        context.setVariable("application", applicationTemplateDTO);
        context.setVariable("currentUser", currentUser);
        context.setVariable("address", address);
        customerEmployeeRepository
                .findFirstByCustomerIdAndRole(fiscalization.getCustomerId(), Role.DIRECTOR)
                .ifPresent(customerEmployee -> context.setVariable("director", customerEmployee.getName()));

        if (applicationTemplateDTO.getAddress() != null) {
            StringBuilder addressBuilder = new StringBuilder();
            if (applicationTemplateDTO.getAddress().getRegionId() != null) {
                regionRepository.findById(applicationTemplateDTO.getAddress().getRegionId()).ifPresent(region -> addressBuilder.append(region.getNameUzCyrillic()).append(" "));
            }
            if (applicationTemplateDTO.getAddress().getDistrictId() != null) {
                districtRepository.findById(applicationTemplateDTO.getAddress().getDistrictId()).ifPresent(district -> {
                            addressBuilder.append(district.getNameUzCyrillic()).append(" ");
                            context.setVariable("newDistrict", district.getNameUzCyrillic());
                        }
                );
            }
            if (applicationTemplateDTO.getAddress().getNeighborhoodId() != null) {
                neighborhoodRepository.findById(applicationTemplateDTO.getAddress().getNeighborhoodId()).ifPresent(neighborhood -> addressBuilder.append(neighborhood.getName()).append(" "));
            }
            if (StringUtils.isNotEmpty(applicationTemplateDTO.getAddress().getStreet())) {
                addressBuilder.append(applicationTemplateDTO.getAddress().getStreet()).append(" ");
            }
            if (StringUtils.isNotEmpty(applicationTemplateDTO.getAddress().getHouse())) {
                addressBuilder.append(applicationTemplateDTO.getAddress().getHouse()).append(" ");
            }
            if (StringUtils.isNotEmpty(applicationTemplateDTO.getAddress().getApartment())) {
                addressBuilder.append(applicationTemplateDTO.getAddress().getApartment()).append(" ");
            }
            context.setVariable("newAddress", addressBuilder.toString().trim());
        }

        OfdApplication ofdApplication = fiscalization.getOfdApplication();
        if (ofdApplication != null && StringUtils.isNotEmpty(ofdApplication.getApplicationFileUrl())) {
            String applicationFileUrl = ofdApplication.getApplicationFileUrl();
            String fileUrl = "https://reestr.ofd.uz/pdf/ticket?key=";
            String ofdId = applicationFileUrl.substring(fileUrl.length());
            if ("null".equals(ofdId) || ofdId.isEmpty()) {
                throw validationService.badRequestExceptionThrow(INVALID_INPUT, ofdApplication.getApplicationFileUrl()).get();
            }
            try {
                applicationTemplateDTO.setOfdFileId(Long.valueOf(ofdId));
            } catch (NumberFormatException e) {
                throw validationService.badRequestExceptionThrow(INVALID_INPUT, ofdApplication.getApplicationFileUrl()).get();
            }
        }

        return switch (applicationTemplateDTO.getTemplateType()) {
            case DEVICE_REPLACEMENT_ACT -> templateEngine.process("Device_replacement_act", context);
            case FISCAL_MODULE_REPLACEMENT_ACT -> templateEngine.process("Fiscal_module_replacement_act", context);
            case DEREGISTER -> templateEngine.process("Deregister", context);
            case DEVICE_REPLACEMENT_TEMPORARY_ACT -> templateEngine.process("Device_replacement_temporary_act", context);
            case CHANGE_CUSTOMER_BRANCH_ADDRESS -> templateEngine.process("Change_customer_branch_address", context);
        };
    }

    @Override
    public String generateContractForLeasingHTML(ContractForLeasing contract) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("contract", contract);
        return templateEngine.process("contractForLeasing/contract", context);
    }

    @Override
    public String generateContractForLeasingPrimeHTML() {
        return templateEngine.process("contractForLeasing/offerPrime", new Context());
    }

    @Override
    public String generateWarrantyForLeasingHTML(ContractForLeasing contract) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("contract", contract);
        return templateEngine.process("contractForLeasing/warranty", context);
    }

    @Override
    public String generateActForTransmissionHTML(ActOfTransmission actOfTransmission) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("act", actOfTransmission);
        return templateEngine.process("contractForLeasing/act", context);
    }

    @Override
    public String generateActForTransmissionPrimeHTML(ActOfTransmission actOfTransmission) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("primeAct", actOfTransmission);
        context.setVariable("primeActDetail", actOfTransmission.getDetails().iterator().next());
        templateEngine.setDialect(new SpringStandardDialect());
        return templateEngine.process("contractForLeasing/primeAct", context);
    }

    @Override
    public String generatePrimePaymentQuoteHTML(ContractForLeasing contractForLeasing) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("contractForLeasing", contractForLeasing);
        return templateEngine.process("contractForLeasing/primePaymentQuote", context);
    }

    @Override
    public String generateActForTransmissionScheduleHTML(ActOfTransmissionDetail actOfTransmissionDetail) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("actDetail", actOfTransmissionDetail);
        return templateEngine.process("contractForLeasing/schedule", context);
    }

    @Override
    public String generatePaymentScheduleInvoice(PaymentScheduleInvoice invoice) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("payInv", invoice);
        return templateEngine.process("contractForLeasing/invoice", context);
    }

    @Override
    public String generateActOfTransmissionInvoice(ActOfTransmissionInvoice invoice) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("inv", invoice);
        return templateEngine.process("contractForLeasing/invoice_xfile", context);
    }

    @Override
    public String generateTradeInContractHTML(TradeInContract contract) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("contract", contract);
        return templateEngine.process("contractTradeIn/contractN5", context);
    }

    @Override
    public String generateTradeInActHTML(TradeInAct act) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("act", act);
        return templateEngine.process("contractTradeIn/actN5", context);
    }

    @Override
    public String generateTradeInCounterActHTML(TradeInCounterAct act) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("act", act);

        SmartOrOldDeviceEnum smartOrOldDeviceEnum = act.getContract().getTradeInCounterDevicePrice().getSmartOrOldDeviceEnum();

        if (SmartOrOldDeviceEnum.SMART.equals(smartOrOldDeviceEnum)) {
            if (act.getContract().getTradeInCounterDevicePrice().getCustomerVatTypeEnum() == CustomerVatTypeEnum.WITH_VAT)
                return templateEngine.process("contractTradeIn/actSmartKKM", context);
            return templateEngine.process("contractTradeIn/actSmartKKMWithoutVat", context);

        }
        if (act.getContract().getTradeInCounterDevicePrice().getCustomerVatTypeEnum() == CustomerVatTypeEnum.WITH_VAT)
            return templateEngine.process("contractTradeIn/actKKMOld", context);
        return templateEngine.process("contractTradeIn/actKKMOldWithoutVat", context);


    }

    @Override
    public String generateTradeInCounterContractHTML(TradeInCounterContract contract) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("contract", contract);
        SmartOrOldDeviceEnum smartOrOldDeviceEnum = contract.getTradeInCounterDevicePrice().getSmartOrOldDeviceEnum();
        if (SmartOrOldDeviceEnum.SMART.equals(smartOrOldDeviceEnum)) {
            if (contract.getTradeInCounterDevicePrice().getCustomerVatTypeEnum() == CustomerVatTypeEnum.WITH_VAT)
                return templateEngine.process("contractTradeIn/contractSmartKKM", context);
            return templateEngine.process("contractTradeIn/contractSmartKKMWithoutVat", context);
        }
        if (contract.getTradeInCounterDevicePrice().getCustomerVatTypeEnum() == CustomerVatTypeEnum.WITH_VAT)
            return templateEngine.process("contractTradeIn/contractKKMOld", context);
        return templateEngine.process("contractTradeIn/contractKKMOldWithoutVat", context);
    }

    @Override
    public String generateTradeInInvoice(TradeInInvoice inInvoice) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("inv", inInvoice);
        return templateEngine.process("contractTradeIn/invoice", context);
    }

    @Override
    public String generateTradeInActPoaHTML(TradeInAct tradeInAct, TradeInActPoaRequestDto dto) {
        dto.setDueDate(LocalDate.now().plusDays(15));
        dto.setQtyInWord(NumberWordConverter.convert(dto.getQty()));
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("act", tradeInAct);
        context.setVariable("dto", dto);
        customerEmployeeRepository
                .findFirstByCustomerIdAndRole(tradeInAct.getContract().getCustomerId(), Role.DIRECTOR)
                .ifPresent(customerEmployee -> context.setVariable("director", customerEmployee.getName()));
        return templateEngine.process("contractTradeIn/powerAttorney", context);
    }

    @Override
    public String generateRentAct(RentAct rentAct) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("rentAct", rentAct);

        return templateEngine.process("docRent/actRent", context);
    }

    @Override
    public String generateRentActOfReturn(RentActOfReturn act) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("act", act);
        return templateEngine.process("docRent/actRentReturn", context);
    }

    @Override
    public String generateRentInvoice(RentInvoice invoice) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("inv", invoice);
        return templateEngine.process("docRent/invoice", context);
    }

    @Override
    public String getRentOfferTemplate(RentOffer offer) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("offer", offer);
        return templateEngine.process("docRent/offer", context);
    }

    @Override
    public String generateRentActPoaHTML(RentAct act, TradeInActPoaRequestDto dto) {
        dto.setDueDate(LocalDate.now().plusDays(15));
        dto.setQtyInWord(NumberWordConverter.convert(dto.getQty()));
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("rentAct", act);
        context.setVariable("dto", dto);
        return templateEngine.process("docRent/powerAttorney", context);
    }

    @Override
    public String generateRentActOfReturnPoaHTML(RentActOfReturn act, TradeInActPoaRequestDto dto) {
        dto.setDueDate(LocalDate.now().plusDays(15));
        dto.setQty(1);
        dto.setQtyInWord(NumberWordConverter.convert(dto.getQty()));
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("rentAct", act);
        context.setVariable("dto", dto);
        customerEmployeeRepository
                .findFirstByCustomerIdAndRole(act.getCustomerId(), Role.DIRECTOR)
                .ifPresent(customerEmployee -> context.setVariable("director", customerEmployee.getName()));
        return templateEngine.process("docRent/powerAttorneyReturn", context);
    }

    @Override
    public String generateTradeLetterOfGuaranteeHTML(TradeInLetterOfGuarantee guarantee) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("letter", guarantee);
        customerEmployeeRepository
                .findFirstByCustomerIdAndRole(guarantee.getCustomerId(), Role.DIRECTOR)
                .ifPresent(customerEmployee -> context.setVariable("director", customerEmployee.getName()));
        return templateEngine.process("contractTradeIn/TardeInLetterOfGuarantee", context);
    }


    @Override
    public String generateSaleContractHTML(SaleContractAccessoryListWithVatDTO saleAccessories, SaleContract saleContract) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("saleContractAccessory", saleAccessories);
        context.setVariable("saleContract", saleContract);
        return templateEngine.process("sale/saleContract", context);
    }

    @Override
    public String generateSaleActHTML(SaleAct saleAct, SaleContractAccessoryListWithVatDTO saleAccessories) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("saleAccessories", saleAccessories);
        context.setVariable("saleAct", saleAct);
        return templateEngine.process("sale/act", context);
    }

    @Override
    public String generateSaleActPowerOfAttorneyHTML(SaleAct saleAct, SaleActPowerOfAttorneyDTO dto, List<SaleAccessory> accessories) {
        dto.setDueDate(LocalDate.now().plusDays(15));
        dto.setQtyInWord(NumberWordConverter.convert(dto.getQty()));
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("saleAct", saleAct);
        context.setVariable("poaDto", dto);
        context.setVariable("accessories", accessories);
        customerEmployeeRepository
                .findFirstByCustomerIdAndRole(saleAct.getSaleContract().getCustomerId(), Role.DIRECTOR)
                .ifPresent(customerEmployee -> context.setVariable("director", customerEmployee.getName()));
        return templateEngine.process("sale/saleActPowerAttorney", context);
    }

    @Override
    public String generateSaleInvoice(SaleInvoice saleInvoice) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("saleInvoice", saleInvoice);
        return templateEngine.process("sale/saleInvoice", context);
    }


    public String lossModule(CaseLossModuleDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/lossOfModuleStatement", context);
    }

    @Override
    public String phoneChangeStatement(PhoneNumberChangeDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/phoneChangeStatement", context);
    }

    @Override
    public String managerChangeStatement(ManagerChangeDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/managerChangeStatement", context);
    }

    @Override
    public String ctsChangeStatement(CtsChangeDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/ctsChangeStatement", context);
    }

    @Override
    public String bankDetailsChangeStatement(BankDetailsDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("bankDetailDTO", dto);
        return templateEngine.process("cases/bankDetailsChangeStatement", context);
    }

    @Override
    public String legalDataChangeStatement(LegalDataChangeDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("legalDataChangeStatement", dto);
        return templateEngine.process("cases/legalDataChangeStatement", context);
    }

    @Override
    public String directorNameChangeStatement(DirectorChangeDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/directorChangeDtoStatement", context);
    }

    @Override
    public String branchDataChangeStatement(CustomerBranchUpdateDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/branchDataChangeStatement", context);
    }

    @Override
    public String humoBankDetailsChangeStatement(HumoBankDetailsChangeDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/humoBankDetailChangeStatement", context);
    }

    @Override
    public String changeTariffStatement(ChangeTariffStatementDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/changeTariffStatement", context);
    }

    @Override
    public String changeDeviceListTariffStatement(ChangeTariffStatementDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/changeTariffStatement", context);
    }

    @Override
    public String humoConnectionStatement(HumoConnectionDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("hamkorAcceptDTO", dto);
        return templateEngine.process("cases/hamkor-accept", context);
    }

    @Override
    public String humoCloseAccount(HumoConnectionDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/close-account", context);
    }

    @Override
    public String humoCloseAccountHamkor(HumoConnectionDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/closeAccountHamkor", context);
    }

    @Override
    public String humoOpenAccount(HumoConnectionDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("dto", dto);
        return templateEngine.process("cases/open-account", context);
    }

    @Override
    public String humoOpenAccountIP(HumoConnectionDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("openAccountIP", dto);
        return templateEngine.process("cases/humoOpenAccountIP", context);
    }

    @Override
    public String humoChangeAccount(HumoBankDetailsChangeDto dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("changeAccount", dto);
        return templateEngine.process("cases/change-account", context);
    }

    @Override
    public String humoConnectionTrans(HumoConnectionDto connectionDTO) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("connectionDTO", connectionDTO);
        return templateEngine.process("cases/humoConnectionTrans", context);
    }

    @Override
    public String generatePaymentQuoteHTML(PaymentQuoteTemplateDTO paymentQuoteTemplateDTO) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("paymentQuoteDTO", paymentQuoteTemplateDTO);
        return templateEngine.process("payment/payment_quote", context);
    }

    @Override
    public String generatePaymentQuoteLeasingHTML(PaymentQuote paymentQuote) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("paymentQuote", paymentQuote);
        return templateEngine.process("payment/payment_quote_leasing", context);
    }

    @Override
    public String generatePaymentContractHTML(PaymentContractTemplateDTO contractTemplateDTO) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("paymentContractDTO", contractTemplateDTO);
        return templateEngine.process("payment/payment_contract", context);
    }

    @Override
    public String generatePaymentActHTML(PaymentAct paymentAct) {
        Locale locale = Locale.forLanguageTag("ru");
        Context context = new Context(locale);
        context.setVariable("paymentAct", paymentAct);
        return templateEngine.process("payment/payment_act", context);
    }

    @Override
    public String generatePaymentActPoaHTML(PaymentAct paymentAct, PaymentActPoaRequestDTO dto) {
        dto.setDueDate(LocalDate.now().plusDays(15));
        dto.setQtyInWord(NumberWordConverter.convert(dto.getQty()));
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("paymentAct", paymentAct);
        context.setVariable("paymentActDto", dto);
        customerEmployeeRepository
                .findFirstByCustomerIdAndRole(paymentAct.getContract().getCustomerId(), Role.DIRECTOR)
                .ifPresent(customerEmployee -> context.setVariable("director", customerEmployee.getName()));
        return templateEngine.process("payment/powerAttorney", context);
    }

    @Override
    public String generateContractPaymentQuoteHTML(PaymentCompanyQuoteTemplateDTO paymentCompanyQuoteTemplateDTO) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("paymentCompanyQuoteDTO", paymentCompanyQuoteTemplateDTO);
        return templateEngine.process("payment/payment_company_quote", context);
    }

    @Override
    public String generatePaymentCompanyContractHTML(PaymentCompanyContractTemplateDTO paymentCompanyContractTemplateDTO) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("paymentCompanyContractDTO", paymentCompanyContractTemplateDTO);
        return templateEngine.process("payment/payment_company_contract", context);
    }

    @Override
    public String generateFiscalModuleDoc(CustomerFiscalModuleTemplateDTO customerFiscalModuleTemplateDTO) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("customerFiscalModuleDTO", customerFiscalModuleTemplateDTO);
        return templateEngine.process("fiscal_module_document", context);
    }

    @Override
    public String generateRentDeviceAct(RentDeviceActTemplateDTO dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("deviceRentAct", dto);

        return templateEngine.process("docRent/deviceRentAct", context);
    }

    @Override
    public String generateRentDeviceActReturn(RentDeviceActTemplateDTO dto) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("deviceRentActOfReturn", dto);

        return templateEngine.process("docRent/deviceRentActOfReturn", context);
    }

    @Override
    public String generateRentDeviceActPowerOfAttorneyHTML(RentDeviceAct rentDeviceAct, RentDeviceActPowerOfAttorneyDTO dto) {
        dto.setDueDate(LocalDate.now().plusDays(15));
        dto.setQtyInWord(NumberWordConverter.convert(dto.getQty()));
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("rentDeviceAct", rentDeviceAct);
        context.setVariable("poaDto", dto);
        customerEmployeeRepository
                .findFirstByCustomerIdAndRole(rentDeviceAct.getCustomerId(), Role.DIRECTOR)
                .ifPresent(customerEmployee -> context.setVariable("director", customerEmployee.getName()));
        return templateEngine.process("docRent/rentDeviceActPowerAttorney", context);
    }

    @Override
    public String generateRepairTicketAcceptAct(RepairTicket repairTicket) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("repairTicket", repairTicket);
        return templateEngine.process("repairticket/priem_act", context);
    }

    @Override
    public String generateRepairTicketDefectAct(RepairTicket repairTicket) {
        Context context = SecurityUtils.getDefaultContext();
        context.setVariable("repairTicket", repairTicket);
        context.setVariable("companyAddress", addressService.getLegalAddress(repairTicket.getCompany().getEntityId(), repairTicket.getCompany().getEntityType()));
        return templateEngine.process("repairticket/defect_act", context);
    }
}
