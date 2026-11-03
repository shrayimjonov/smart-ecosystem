package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.dto.launcher.LauncherNotification;
import uz.uzkassa.cts.dto.launcher.LauncherTerminalRequestDto;
import uz.uzkassa.cts.dto.ledger.response.CurrentStatementResponseDto;
import uz.uzkassa.cts.dto.servicepaymenthistory.ServicePaymentHistoryCronDto;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.enums.PeriodType;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class NotificationOldServiceImpl implements NotificationOldService {

    CustomerBranchDeviceRepository customerBranchDeviceRepository;
    LauncherService launcherService;
    ServicePaymentHistoryCronService servicePaymentHistoryCronService;
    LedgerService ledgerService;
    SmsService smsService;

    private static final String NOTFiCATION_TEXT_RUS = "Произведите пожалуйста оплату за сервисное обслуживание. В конце месяца работа онлайн ККМ будет приостановлена.";
    private static final String NOTFiCATION_TEXT_UZ = "Texnik xizmat ko'rsatish uchun to’lovni amalga oshirishni unutmang. Oy oxirida onlayn NKM faoliyati to'xtatiladi.";


//    private static final String ONE_TIME_NOTFiCATION_TEXT_UZ = "Katalog bilan ishlash bo'yicha bosqichma-bosqich ko'rsatmalar bizning  Telegram kanalimizda:  https://t.me/uzkassainfo/187";
//    private static final String ONE_TIME_NOTFiCATION_TEXT_RUS = "Пошаговая инструкции по работе с каталогом в нашем Telegram-канале:  https://t.me/uzkassainfo/187";

//    private static final String ONE_TIME_NOTFiCATION_TEXT_RUS = "Подарочная пятница! Розыгрыш призов в Telegram-канале. Подписывайтесь и побеждайте: https://t.me/uzkassainfo";
//    private static final String ONE_TIME_NOTFiCATION_TEXT_UZ = "Sovg‘alar ulashish jumasi! Telegram-kanalda sovg‘alar o‘ynalishi. Obuna bo‘ling va g‘alabani qo‘lga kiriting: https://t.me/uzkassainfo";

    private static final String ONE_TIME_NOTFiCATION_TEXT_RUS = "Подарочная пятница! Розыгрыш призов в Telegram-канале. Подписывайтесь и побеждайте: https://t.me/suppteam24";
    private static final String ONE_TIME_NOTFiCATION_TEXT_UZ = "Sovg‘alar ulashish jumasi! Telegram-kanalda sovg‘alar o‘ynalishi. Obuna bo‘ling va g‘alabani qo‘lga kiriting: https://t.me/suppteam24";

    @Override
    public void notifyAllDevices(LauncherNotification notification) {
        List<CustomerBranchDevice> devices = customerBranchDeviceRepository.findAll();
        LauncherTerminalRequestDto dto = new LauncherTerminalRequestDto();
        dto.setNotification(notification);
        List<String> serialNumbers = new ArrayList<>();
        for (CustomerBranchDevice dev : devices) {
            serialNumbers.add(dev.getDevice().getSerialNumber());
        }
        serialNumbers.add("N5000103180");
        dto.setTerminalNumbers(serialNumbers);
        launcherService.notify(dto);
    }

    private List<String> getSerialNumbers(String tin) {
        CurrentStatementResponseDto statement = ledgerService.getNeedAmountAndStatement(tin, ServiceType.SERVICE);
        // calculate next month
        LocalDate nextMonthFirstDate = DateUtil.calculateDueDate(LocalDate.now());
        LocalDate nextMontDueDate = DateUtil.calculateDueDate(nextMonthFirstDate);
        BigDecimal nextMonthAmount = BigDecimal.ZERO;
        List<String> serialNumbers = new ArrayList<>();

        List<ServicePaymentHistoryCronDto> list = servicePaymentHistoryCronService.getCustomerDevices(tin);
        for (ServicePaymentHistoryCronDto dto : list) {
            if (dto.getTariffPeriodType() == PeriodType.MONTHLY) {
                nextMonthAmount = calcNeedAmount(nextMonthFirstDate, nextMontDueDate, nextMonthAmount, dto.getTariffPrice(), 0);
                serialNumbers.add(dto.getSerialNumber());
            }
        }
        if (serialNumbers.isEmpty())
            return serialNumbers;

        if (statement.getBalance() == null)
            statement.setBalance(BigDecimal.ZERO);

        BigDecimal needAmountWithNextMonth = statement.getNeedAmount().subtract(nextMonthAmount);
        if (statement.getBalance().compareTo(BigDecimalUtil.setScaleByTwo(needAmountWithNextMonth)) < 0)
            return serialNumbers;

        return new ArrayList<>();
    }

    @Override
    public void notifyAboutMonthlyServiceFee(String tin) {
        List<String> serialNumbers = getSerialNumbers(tin);
        if (serialNumbers.isEmpty())
            return;

        LauncherNotify(serialNumbers, NOTFiCATION_TEXT_RUS,"Оплата услуг", 1L);
        LauncherNotify(serialNumbers, NOTFiCATION_TEXT_UZ,"Xizmatlar uchun to'lov.", 2L);
    }



    @Override
    public void notifyAboutMonthlyServiceFeeBySms(String tin) {
        List<String> serialNumbers = getSerialNumbers(tin);
//        if (serialNumbers.size() < 1)
//            return;

//        Customer customer = customerRepository.findByTin(tin);

//        EtcRequestDto dto = new EtcRequestDto(customer.getPhone(), NOTFiCATION_TEXT_UZ + "\n" + NOTFiCATION_TEXT_RUS);
        EtcRequestDTO dto = new EtcRequestDTO("998901234567", NOTFiCATION_TEXT_UZ + "\n \n" + NOTFiCATION_TEXT_RUS);
        smsService.send(dto);
    }

    @Override
    public void notifyBySms(String phoneNumber) {
        EtcRequestDTO dto = new EtcRequestDTO(phoneNumber,ONE_TIME_NOTFiCATION_TEXT_UZ + "\n \n" + ONE_TIME_NOTFiCATION_TEXT_RUS);
        smsService.send(dto);
    }

    public void notifyBySms(String phoneNumber, String text) {
        EtcRequestDTO dto = new EtcRequestDTO(phoneNumber,text);
        smsService.send(dto);
    }

    public void notifyBySms(EtcRequestDTO dto) {
        smsService.send(dto);
    }

    private BigDecimal calcNeedAmount(LocalDate nextMonthFirstDate, LocalDate nextMontDueDate, BigDecimal
        nextMonthAmount, BigDecimal tariffPrice, int discount) {
        BigDecimal calculatedAmount = BigDecimalUtil.calculatePrice(nextMonthFirstDate, nextMontDueDate, tariffPrice, discount);
        nextMonthAmount = nextMonthAmount.add(calculatedAmount);
        return nextMonthAmount;
    }

    private void LauncherNotify(List<String> serialNumbers, String content, String header, Long subjectId) {
        // Notification
        LauncherNotification notification = new LauncherNotification();
        notification.setSubjectId(subjectId);
        notification.setContent(content);
        notification.setHeader(header);

        LauncherTerminalRequestDto requestDto = new LauncherTerminalRequestDto();
        requestDto.setTerminalNumbers(serialNumbers);
        requestDto.setNotification(notification);

        launcherService.notify(requestDto);
    }
}
