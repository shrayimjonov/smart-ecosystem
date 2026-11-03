package uz.uzkassa.cts.service;

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

import java.util.List;

public interface ThymeleafService extends BaseService<Thymeleaf, Long> {

    String generateHTML(ThymeleafDTO thymeleafDTO);

    String generateContractForBuyHTML(Contract contract);

    String generateSpecificationForServiceHTML(String serialNumbers, CtsServiceOrderSpecification specification);

    String generateActOfTransmissionPoaHTML(ActOfTransmission actOfTransmission, GeneratePoaTemplateDTO dto);

    String generateActOfTransmissionPrimePoaHTML(ActOfTransmission actOfTransmission, GeneratePoaTemplateDTO dto);

    String generateApplicationTemplate(Fiscalization fiscalization, ApplicationTemplateDTO applicationTemplateDTO);

    String generateServiceInvoiceHTML(ServiceOrderInvoice invoice);

    String generateOneTimeSpecHTML(OneTimeOrder order);

    String generateCronBlockTextHTML();

    String generateOneTimeOrderInvoiceHTML(OneTimeOrderInvoice invoice);

    String generateContractForLeasingHTML(ContractForLeasing contract);

    String generateContractForLeasingPrimeHTML();

    String generateWarrantyForLeasingHTML(ContractForLeasing contract);

    String generateActForTransmissionHTML(ActOfTransmission actOfTransmission);

    String generateActForTransmissionPrimeHTML(ActOfTransmission actOfTransmission);

    String generatePrimePaymentQuoteHTML(ContractForLeasing contractForLeasing);

    String generateActForTransmissionScheduleHTML(ActOfTransmissionDetail actOfTransmissionDetail);

    String generatePaymentScheduleInvoice(PaymentScheduleInvoice invoice);

    String generateActOfTransmissionInvoice(ActOfTransmissionInvoice invoice);

    String generateTradeInContractHTML(TradeInContract contract);

    String generateTradeInActHTML(TradeInAct act);

    String generateTradeInCounterContractHTML(TradeInCounterContract contract);

    String generateTradeInCounterActHTML(TradeInCounterAct act);

    String generateTradeInInvoice(TradeInInvoice byId);

    String generateTradeInActPoaHTML(TradeInAct tradeInAct, TradeInActPoaRequestDto dto);

    String generateRentAct(RentAct act);

    String generateRentActOfReturn(RentActOfReturn act);

    String generateRentInvoice(RentInvoice invoice);

    String getRentOfferTemplate(RentOffer offer);

    String generateRentActPoaHTML(RentAct act, TradeInActPoaRequestDto dto);

    String generateRentActOfReturnPoaHTML(RentActOfReturn act, TradeInActPoaRequestDto dto);

    String generateTradeLetterOfGuaranteeHTML(TradeInLetterOfGuarantee guarantee);

    String generateSaleContractHTML(SaleContractAccessoryListWithVatDTO saleAccessories, SaleContract saleContract);

    String generateSaleActHTML(SaleAct saleAct, SaleContractAccessoryListWithVatDTO saleAccessories);

    String generateSaleActPowerOfAttorneyHTML(SaleAct saleAct, SaleActPowerOfAttorneyDTO dto, List<SaleAccessory> accessories);

    String generateSaleInvoice(SaleInvoice saleInvoice);

    String lossModule(CaseLossModuleDto dto);

    String phoneChangeStatement(PhoneNumberChangeDto dto);

    String managerChangeStatement(ManagerChangeDto dto);

    String ctsChangeStatement(CtsChangeDto dto);

    String bankDetailsChangeStatement(BankDetailsDto dto);

    String legalDataChangeStatement(LegalDataChangeDto dto);

    String directorNameChangeStatement(DirectorChangeDto dto);

    String branchDataChangeStatement(CustomerBranchUpdateDto dto);

    String humoBankDetailsChangeStatement(HumoBankDetailsChangeDto dto);

    String changeTariffStatement(ChangeTariffStatementDto dto);

    String changeDeviceListTariffStatement(ChangeTariffStatementDto dto);

    String humoConnectionStatement(HumoConnectionDto dto);

    String humoCloseAccount(HumoConnectionDto dto);

    String humoCloseAccountHamkor(HumoConnectionDto dto);

    String humoOpenAccount(HumoConnectionDto dto);

    String humoOpenAccountIP(HumoConnectionDto dto);

    String humoChangeAccount(HumoBankDetailsChangeDto dto);

    String humoConnectionTrans(HumoConnectionDto connectionDTO);

    String generatePaymentQuoteHTML(PaymentQuoteTemplateDTO paymentQuoteTemplateDTO);

    String generatePaymentQuoteLeasingHTML(PaymentQuote paymentQuote);

    String generatePaymentContractHTML(PaymentContractTemplateDTO paymentContractTemplateDTO);

    String generatePaymentActHTML(PaymentAct paymentAct);

    String generatePaymentActPoaHTML(PaymentAct paymentAct, PaymentActPoaRequestDTO dto);

    String generateContractPaymentQuoteHTML(PaymentCompanyQuoteTemplateDTO paymentCompanyQuoteTemplateDTO);

    String generatePaymentCompanyContractHTML(PaymentCompanyContractTemplateDTO paymentCompanyContractTemplateDTO);

    String generateFiscalModuleDoc(CustomerFiscalModuleTemplateDTO customerFiscalModuleTemplateDTO);

    String generateRentDeviceAct(RentDeviceActTemplateDTO dto);

    String generateRentDeviceActReturn(RentDeviceActTemplateDTO dto);

    String generateRentDeviceActPowerOfAttorneyHTML(RentDeviceAct rentDeviceAct, RentDeviceActPowerOfAttorneyDTO dto);

    String generateRepairTicketAcceptAct(RepairTicket repairTicket);

    String generateRepairTicketDefectAct(RepairTicket repairTicket);
}
