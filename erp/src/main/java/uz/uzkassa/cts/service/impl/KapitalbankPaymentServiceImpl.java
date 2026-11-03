package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.KapitalbankPayment;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.kapitalbankpayment.response.KapitalbankPaymentResponseDto;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoice1cResponse;
import uz.uzkassa.cts.enums.KapitalbankPaymentStatus;
import uz.uzkassa.cts.filter.KapitalBankFilter;
import uz.uzkassa.cts.mappings.KapitalbankPaymentMapper;
import uz.uzkassa.cts.repository.KapitalbankPaymentRepository;
import uz.uzkassa.cts.service.KapitalbankPaymentService;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KapitalbankPaymentServiceImpl extends BaseServiceImpl<KapitalbankPayment, Long> implements KapitalbankPaymentService {

    private final KapitalbankPaymentRepository repository;
    private final KapitalbankPaymentMapper kapitalbankPaymentMapper;

    public KapitalbankPaymentServiceImpl(KapitalbankPaymentRepository repository, KapitalbankPaymentMapper kapitalbankPaymentMapper) {
        super(repository);
        this.repository = repository;
        this.kapitalbankPaymentMapper = kapitalbankPaymentMapper;
    }

    @Override
    public Page<KapitalbankPaymentResponseDto> list(KapitalBankFilter filter) {
        ResultList<KapitalbankPayment> resultList = repository.getList(filter);
        List<KapitalbankPaymentResponseDto> getList = resultList.getList().stream().map(kapitalbankPaymentMapper::toDto).collect(Collectors.toList());
        return new PageImpl<>(getList, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public byte[] getDownloadUrl(KapitalBankFilter filter) {
        List<KapitalbankPaymentResponseDto> dtoList = repository.getDownloadUrl(filter).stream()
                .map(kapitalbankPaymentMapper::toDto)
                .collect(Collectors.toList());
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Invoices");
            String[] headerNames = new String[]{"id", "ИНН", "серийный номер устройства", "Название компании",
                    "расчетный счет", "код банка", "Банк", "номер карты", "истечение срока карты", "сумма", "ID транзакции",
                    "замаскированный номер телефона", "сообщение об ошибке", "статус", "Время создания", "payData", "payErrorMessage",
                    "статус оплаты", "Время оплаты", "статус у нас"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            InvoiceCreateExcelUtil.createBody(dtoList, sheet);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while creating excel file", e);
        }
    }

    public BigDecimal getConfirmedAmount(KapitalBankFilter filter) {
        return repository.getConfirmedAmount(filter);
    }

    @Override
    public Page<OneTimeOrderInvoice1cResponse> list1c(KapitalBankFilter filter) {
        filter.setKapitalbankPaymentStatus(KapitalbankPaymentStatus.CONFIRMED);

        ResultList<KapitalbankPayment> resultList = repository.getList1c(filter);
        List<OneTimeOrderInvoice1cResponse> getList = resultList.getList().stream().map(kapitalbankPaymentMapper::to1cDto).collect(Collectors.toList());
        return new PageImpl<>
                (getList, filter.getOrderedPageable(), resultList.getCount());
    }
}
