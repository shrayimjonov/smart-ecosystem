package uz.uzkassa.cts.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.kapitalbankpayment.response.KapitalbankPaymentResponseDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.enums.InvoiceType;
import uz.uzkassa.cts.repository.ServicePaymentHistoryForCronRepository;
import uz.uzkassa.cts.service.LedgerService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceCreateExcelUtil {

    public static void createHeader(String[] headerNames, XSSFSheet sheet) {
        Row header = sheet.createRow(0);
        int cellCount = 0;
        for (String headerName : headerNames) {
            header.createCell(cellCount++).setCellValue(headerName);
        }
    }

    public static void createBody(List<ServiceOrderInvoiceListDto> invoices, XSSFSheet sheet, InvoiceType type) {
        int rowCount = 1;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        DateTimeFormatter contractFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        for (ServiceOrderInvoiceListDto invoice : invoices) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(invoice.getId());
            row.createCell(1).setCellValue(invoice.getTin());
            row.createCell(2).setCellValue(invoice.getCustomerName());
            row.createCell(3).setCellValue(invoice.getContractNumber());
            row.createCell(4).setCellValue(invoice.getContractDate().format(contractFormatter));
            row.createCell(5).setCellValue(invoice.getInvoiceNumber());
            row.createCell(6).setCellValue(invoice.getInvoiceDate().format(contractFormatter));
            if (type == InvoiceType.ONE_TIME_ORDER_INVOICE) {
                row.createCell(7).setCellValue(invoice.getServiceName());
                row.createCell(8).setCellValue(invoice.getTotalPrice().doubleValue());
                row.createCell(9).setCellValue(invoice.getTotalVat().doubleValue());
                row.createCell(10).setCellValue(invoice.getAmount().doubleValue());
                row.createCell(11).setCellValue(invoice.getStatus().getName());
                row.createCell(12).setCellValue(invoice.getMfo());
                row.createCell(13).setCellValue(invoice.getAccountNumber());
                row.createCell(14).setCellValue(invoice.getBankName());
            } else {
                row.createCell(7).setCellValue(invoice.getTotalPrice().doubleValue());
                row.createCell(8).setCellValue(invoice.getTotalVat().doubleValue());
                row.createCell(9).setCellValue(invoice.getAmount().doubleValue());
                row.createCell(10).setCellValue(invoice.getStatus().getName());
                row.createCell(11).setCellValue(invoice.getMfo());
                row.createCell(12).setCellValue(invoice.getAccountNumber());
                row.createCell(13).setCellValue(invoice.getBankName());
            }
        }
    }


    public static void createBody(List<KapitalbankPaymentResponseDto> dtos, XSSFSheet sheet) {
        int rowCount = 1;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");

//        {"id", "ИНН", "серийный номер устройства", "Название компании",
//            "расчетный счет", "код банка", "Банк", "номер карты", "истечение срока карты", "сумма", "ID транзакции",
//            "замаскированный номер телефона", "сообщение об ошибке", "статус", "Время создания", "payData", "payErrorMessage",
//            "статус оплаты", "Время оплаты", "статус у нас"}

        for (KapitalbankPaymentResponseDto dto : dtos) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(dto.getId());
            row.createCell(1).setCellValue(dto.getTin());
            row.createCell(2).setCellValue(dto.getDeviceSerialNumber());
            row.createCell(3).setCellValue(dto.getCompanyName());
            row.createCell(4).setCellValue(dto.getAccountNumber());
            row.createCell(5).setCellValue(dto.getBankCode());
            row.createCell(6).setCellValue(dto.getBankName());
            row.createCell(7).setCellValue(dto.getPan());
            row.createCell(8).setCellValue(dto.getExpiry());
            row.createCell(9).setCellValue(dto.getAmount().doubleValue());
            if (dto.getTransactionId() != null)
                row.createCell(10).setCellValue(dto.getTransactionId());
            row.createCell(11).setCellValue(dto.getMaskedPhoneNumber());
            row.createCell(12).setCellValue(dto.getErrorMessage());
            row.createCell(13).setCellValue(dto.getStatus());
            row.createCell(14).setCellValue(dto.getTimestamp().format(dateTimeFormatter));
            row.createCell(15).setCellValue(dto.getPayData());
            row.createCell(16).setCellValue(dto.getPayErrorMessage());
            row.createCell(17).setCellValue(dto.getPayStatus());
            if (dto.getPayTimestamp() != null)
                row.createCell(18).setCellValue(dto.getPayTimestamp().format(dateTimeFormatter));
            row.createCell(19).setCellValue(dto.getKapitalbankPaymentStatus().getName());
        }
    }

    public static void createActBody(List<Object[]> dtos, XSSFSheet sheet,
                                     ServicePaymentHistoryForCronRepository cronRepository,
                                     LedgerService ledgerService) {
        int rowCount = 1;

        for (Object[] dto : dtos) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(dto[0].toString()); //Количество ККМ
            row.createCell(1).setCellValue(dto[1].toString()); //Номер договора
            row.createCell(2).setCellValue(dto[2].toString().substring(0, 10)); //Дата создания договора
            row.createCell(3).setCellValue(dto[3].toString()); //Номер акта
            row.createCell(4).setCellValue(dto[4].toString().substring(0, 10)); //Дата создания акта
            row.createCell(5).setCellValue(dto[5].toString()); //Название компании

            String tin = dto[6].toString(); //ИНН
            row.createCell(6).setCellValue(tin);
            row.createCell(7).setCellValue(dto[7].toString());    //Телефон
            row.createCell(8).setCellValue(dto[9].toString());    //Название региона
            row.createCell(9).setCellValue(dto[10].toString());   //Имя создателя
            if (dto[11] != null)
                row.createCell(10).setCellValue(dto[11].toString());  //Branch

            BigDecimal balance = (BigDecimal) dto[8];
            List<ServicePaymentHistory> unpaidHistories = cronRepository.findAllUnPaidCustomerTin(tin);
            BigDecimal calculateUnpaid = ledgerService.calculateUnpaidAndWithoutInvoice(unpaidHistories);
            BigDecimal statement = balance.subtract(calculateUnpaid);

            row.createCell(11).setCellValue(balance.doubleValue()); //Остаток
            row.createCell(12).setCellValue(statement.doubleValue()); //Предоплата
            if (dto[12] != null)
                row.createCell(13).setCellValue(dto[12].toString().substring(0, 10)); //Предоплата
            if (dto[13] != null)
                row.createCell(14).setCellValue(dto[13].toString()); //Предоплата
            if (dto[14] != null)
                row.createCell(15).setCellValue(dto[14].toString()); //Предоплата
        }
    }

    public static void createRejectedListReport(List<BankClientDetailRejected> details, XSSFSheet sheet) {
        int rowCount = 1;

        for (BankClientDetailRejected rejected : details) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(rejected.getPayerIdentityNumber());
            row.createCell(1).setCellValue(rejected.getPayerName());
            row.createCell(2).setCellValue(rejected.getAmount().doubleValue());
            row.createCell(3).setCellValue(rejected.getJournalName());
            row.createCell(4).setCellValue(rejected.getDescription());
            row.createCell(5).setCellValue(rejected.getPaymentDate().toString());
            row.createCell(6).setCellValue(rejected.getCreatedDate().toString().substring(0, 10));
            row.createCell(7).setCellValue(rejected.getLastModifiedDate().toString().substring(0, 10));
            row.createCell(8).setCellValue(rejected.getStatus().getName());
            row.createCell(9).setCellValue(rejected.getOldTin());
        }
    }

    public static void createActOfTransmissionDetails(List<ActOfTransmissionDetail> details, XSSFSheet sheet) {
        int rowCount = 1;
        //"Контракт", "Акт", "ИНН", "Клиент", "ККМ", "Сумма", "Период", "Статус акта", "Статус физ. документа", "причина"
        for (ActOfTransmissionDetail detail : details) {
            Row row = sheet.createRow(rowCount++);
            ActOfTransmission act = detail.getActOfTransmission();
            ContractForLeasing contract = act.getContract();
            Customer customer = contract.getCustomer();
            row.createCell(0).setCellValue(contract.getNumber());
            row.createCell(1).setCellValue(act.getNumber());
            row.createCell(2).setCellValue(customer.getCompanyName());
            row.createCell(3).setCellValue(detail.getDevice().getSerialNumber());
            row.createCell(4).setCellValue(detail.getPriceWithVatFor1pc().doubleValue());
            row.createCell(5).setCellValue(act.getPeriod());
            row.createCell(6).setCellValue(act.getStatus().getName());
            row.createCell(7).setCellValue(act.getDocFlowStatus().getName());
            row.createCell(8).setCellValue(act.getDocFlowReason());
        }
    }

    public static void createAPayPayments(List<APayPayment> aPayPayments, XSSFSheet sheet) {
        int rowCount = 1;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        //"id", "ИНН", "серийный номер устройства", "Название компании",
        //            "расчетный счет", "код банка", "Банк", "номер карты", "истечение срока карты", "сумма", "BillId транзакции",
        //            "замаскированный номер телефона", "сообщение об ошибке", "статус", "Время создания", "payErrorMessage",
        //            "статус оплаты", "Время оплаты", "статус у нас"
        for (APayPayment detail : aPayPayments) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(detail.getId());
            row.createCell(1).setCellValue(detail.getTin());
            row.createCell(2).setCellValue(detail.getDeviceSerialNumber());
            row.createCell(3).setCellValue(detail.getCompanyName());
            row.createCell(4).setCellValue(detail.getAccountNumber());
            row.createCell(5).setCellValue(detail.getBankCode());
            row.createCell(6).setCellValue(detail.getBankName());
            row.createCell(7).setCellValue(detail.getPan());
            row.createCell(8).setCellValue(detail.getExpiry());
            row.createCell(9).setCellValue(detail.getAmount().doubleValue());
            row.createCell(10).setCellValue(detail.getBillId());
            row.createCell(11).setCellValue(detail.getMaskedPhoneNumber());
            row.createCell(12).setCellValue(detail.getPayStatus() != null ? detail.getPayStatus().getName() : "");
            row.createCell(13).setCellValue(detail.getCreatedDate().format(dateTimeFormatter));
            row.createCell(14).setCellValue(detail.getCheckStatus() != null ? detail.getCheckStatus().getName() : "");
            row.createCell(15).setCellValue(detail.getAPayPaymentStatus().getNameRu());
        }
    }

    public static void createDeviceSuspendHistory(Page<DeviceSuspendHistory> aPayPayments, XSSFSheet sheet) {
        int rowCount = 1;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
//        {"id", "Tariff", "серийный номер устройства", "Название компании",
//            "StartDate", "DueDate" ,"month", "year", "creator", "modifier"}
        for (DeviceSuspendHistory detail : aPayPayments) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(detail.getId());
            row.createCell(1).setCellValue(detail.getTariff().getName());
            row.createCell(2).setCellValue(detail.getDeviceSerialNumber());
            row.createCell(3).setCellValue(detail.getContract().getCustomer().getCompanyName());
            row.createCell(4).setCellValue(detail.getStartDate().format(dateTimeFormatter));
            if (detail.getDueDate() != null)
                row.createCell(5).setCellValue(detail.getDueDate().format(dateTimeFormatter));
            row.createCell(6).setCellValue(detail.getMonth().toString());
            row.createCell(7).setCellValue(detail.getYear().doubleValue());
            row.createCell(8).setCellValue(detail.getCreator().getFullName());
            row.createCell(9).setCellValue(detail.getLastModifier().getFullName());
        }
    }
}
