package uz.uzkassa.cts.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.cron.CronDeviceSwitchDto;
import uz.uzkassa.cts.dto.cron.CronInvoiceDto;
import uz.uzkassa.cts.dto.invoice.request.LedgerInvoiceRequestDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.repository.ServiceOrderInvoiceRepository;
import uz.uzkassa.cts.service.ContractService;
import uz.uzkassa.cts.service.CronInvoiceService;
import uz.uzkassa.cts.service.CronService;
import uz.uzkassa.cts.service.LedgerService;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CronInvoiceServiceImpl extends BaseServiceImpl<ServiceOrderInvoice, Long> implements CronInvoiceService {

    private final LedgerService ledgerService;
    private final CronService cronService;
    private final ContractService contractService;

    public CronInvoiceServiceImpl(ServiceOrderInvoiceRepository repository,
                                  LedgerService ledgerService,
                                  CronService cronService,
                                  ContractService contractService) {
        super(repository);
        this.ledgerService = ledgerService;
        this.cronService = cronService;
        this.contractService = contractService;
    }

    @Override
    public void generateInvoice(LocalDate localDate) {
        List<CronDeviceSwitchDto> deviceList = cronService.getCustomerDevicesForInvoice(localDate);
        Map<String, Map<Long, Map<Long, List<CronDeviceSwitchDto>>>> map = deviceList.stream()
            .collect(
                Collectors.groupingBy(CronDeviceSwitchDto::getCustomerTin,
                    Collectors.groupingBy(CronDeviceSwitchDto::getContractId,
                        Collectors.groupingBy(CronDeviceSwitchDto::getTariffId)
                    )
                )
            );

        for (Map.Entry<String, Map<Long, Map<Long, List<CronDeviceSwitchDto>>>> tinEntry : map.entrySet()) {

            List<CronInvoiceDto> cronInvoiceDtos = new ArrayList<>();

            for (Map.Entry<Long, Map<Long, List<CronDeviceSwitchDto>>> contractEntry : tinEntry.getValue().entrySet()) {

                for (Map.Entry<Long, List<CronDeviceSwitchDto>> tariffEntry : contractEntry.getValue().entrySet()) {

                    BigDecimal price = BigDecimal.ZERO;
                    int deviceCount = 0;
                    String tariffName = "";
                    BigDecimal tariffPrice = BigDecimal.ZERO;
                    Long tariffId = tariffEntry.getKey();

                    List<CronDeviceSwitchDto> deviceSwitchDtos = tariffEntry.getValue();
                    for (CronDeviceSwitchDto dto : deviceSwitchDtos) {
                        if (!dto.getStartDate().isAfter(LocalDate.now())) {
                            deviceCount++;
                            price = price.add(calculatePrice(dto.getStartDate(), dto.getDueDate(), dto.getTariffPrice()));
                            tariffName = dto.getTariffName();
                            tariffPrice = dto.getTariffPrice();
                        }
                    }
                    if (deviceCount > 0) {
                        CronInvoiceDto cronInvoiceDto = new CronInvoiceDto();
                        cronInvoiceDto.setTariffId(tariffId);
                        cronInvoiceDto.setPrice(BigDecimalUtil.setScaleByTwo(price));
                        cronInvoiceDto.setTariffName(tariffName);
                        cronInvoiceDto.setDeviceCount(deviceCount);
                        cronInvoiceDto.setTariffPrice(tariffPrice);
                        cronInvoiceDto.setContractId(contractEntry.getKey());
                        cronInvoiceDtos.add(cronInvoiceDto);
                    }
                }
            }
            if (!cronInvoiceDtos.isEmpty())
                createInvoice(cronInvoiceDtos, BigDecimal.ZERO);
        }

    }

    private BigDecimal calculatePrice(LocalDate startDate, LocalDate dueDate, BigDecimal price) {
        int dateDayOfMonth = startDate.lengthOfMonth();

        long daysForPay = ChronoUnit.DAYS.between(startDate, dueDate);

        return price.divide(BigDecimal.valueOf(dateDayOfMonth), MathContext.DECIMAL128)
            .multiply(BigDecimal.valueOf(daysForPay), MathContext.DECIMAL128);
    }

    private void createInvoice(List<CronInvoiceDto> cronInvoiceDtos, BigDecimal vatPercent) {
        ServiceOrderInvoice invoice = new ServiceOrderInvoice();

        Long contractId = 0L;
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalSellPrice = BigDecimal.ZERO;
        BigDecimal totalVatPrice = BigDecimal.ZERO;
        int totalProductCount = 0;

        for (CronInvoiceDto dto : cronInvoiceDtos) {
            contractId = dto.getContractId();

            ServiceOrderInvoiceDetail invoiceDetail = new ServiceOrderInvoiceDetail();

            Integer deviceCount = dto.getDeviceCount();
            BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(dto.getTariffPrice(), vatPercent);
            BigDecimal productPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(dto.getTariffPrice(), vatPriceFor1);

            invoiceDetail.setTariffId(dto.getTariffId());
            invoiceDetail.setTariffName(dto.getTariffName());
            invoiceDetail.setProductCount(dto.getDeviceCount());
            invoiceDetail.setPrice1pc(productPriceFor1);
            invoiceDetail.setSellPrice1pc(BigDecimalUtil.setScaleByTwo(dto.getPrice()));
            invoiceDetail.setVatPrice1pc(vatPriceFor1);
            invoiceDetail.setVatRate(vatPercent);

            BigDecimal overallPrice = productPriceFor1.multiply(BigDecimal.valueOf(deviceCount));
            BigDecimal overallSellPrice = BigDecimalUtil.setScaleByTwo(dto.getPrice());
            BigDecimal overallVatPrice = vatPriceFor1.multiply(BigDecimal.valueOf(deviceCount));
            invoiceDetail.setOverallPrice(overallPrice);
            invoiceDetail.setOverallSellPrice(BigDecimalUtil.setScaleByTwo(overallSellPrice));
            invoiceDetail.setOverallVatPrice(overallVatPrice);

            invoice.addChild(invoiceDetail);

            totalPrice = totalPrice.add(overallPrice);
            totalSellPrice = totalSellPrice.add(overallSellPrice);
            totalVatPrice = totalVatPrice.add(overallVatPrice);
            totalProductCount += deviceCount;
        }

        invoice.setTotalPrice(BigDecimalUtil.setScaleByTwo(totalPrice));
        invoice.setTotalPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalPrice)));
        invoice.setTotalSellPrice(BigDecimalUtil.setScaleByTwo(totalSellPrice));
        invoice.setTotalSellPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalSellPrice)));
        invoice.setTotalVatPrice(BigDecimalUtil.setScaleByTwo(totalVatPrice));
        invoice.setTotalVatPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalVatPrice)));
        invoice.setTotalProductCount(totalProductCount);
        invoice.setStatus(ContractStatus.ACCEPTED);
        invoice.setContractId(contractId);
        if (invoice.getTotalSellPrice().compareTo(BigDecimal.ZERO) != 0) {
            ServiceOrderInvoice newInvoice = save(invoice);
            changeBalance(newInvoice);
        }
    }

    private void changeBalance(ServiceOrderInvoice invoice) {
        Contract contract = contractService.findById(invoice.getContractId());

        Company company = contract.getCompany();
        Customer customer = contract.getCustomer();
        LedgerInvoiceRequestDto requestDto = new LedgerInvoiceRequestDto()
            .id(invoice.getId())
            .documentNo(contract.getName())
            .transactionNumber(invoice.getId().toString())
            .contractNumber(contract.getName())
            .serviceType(ServiceType.SERVICE)
            .amount(invoice.getTotalSellPrice())
            .paymentDate(invoice.getCreatedDate().toLocalDate())
            .company(company)
            .customer(customer)
            .link("/api/serviceOrderInvoice/" + invoice.getId() + "/generateTemplate")
            .description(invoice.createDescription(contract));
        ledgerService.payForOrder(requestDto);
    }

}
