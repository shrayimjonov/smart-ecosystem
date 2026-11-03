package uz.uzkassa.smartpos.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.receipt.Additional;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.user.TelegramAccount;
import uz.uzkassa.smartpos.dto.analytics.OverallSalesStatsDTO;
import uz.uzkassa.smartpos.dto.analytics.ProductStatsDTO;
import uz.uzkassa.smartpos.dto.analytics.UserSalesStatsDTO;
import uz.uzkassa.smartpos.dto.filter.TelegramAccountFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.dto.integration.quote.ContractStatus;
import uz.uzkassa.smartpos.dto.integration.quote.QuoteChangeStatusRequestDto;
import uz.uzkassa.smartpos.dto.integration.quote.QuoteResponseDto;
import uz.uzkassa.smartpos.dto.shift.ShiftReportDTO;
import uz.uzkassa.smartpos.dto.telegram.TelegramAccountDTO;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.enums.Emoji;
import uz.uzkassa.smartpos.enums.ReportType;
import uz.uzkassa.smartpos.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptSalesStatsRepository;
import uz.uzkassa.smartpos.repository.clickhouse.receiptdetail.ClickhouseReceiptDetailRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ShiftReportRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.repository.postgres.user.TelegramAccountRepository;
import uz.uzkassa.smartpos.social.TelegramBotApi;
import uz.uzkassa.smartpos.utils.DateUtils;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TelegramService extends SmartposService {
    private final TelegramAccountService telegramAccountService;
    private final ShiftReportRepository shiftReportRepository;
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final ClickhouseReceiptDetailRepository clickhouseReceiptDetailRepository;
    private final TelegramBotApi botApi;
    private final TokenStoreService tokenStoreService;
    private final ClickhouseReceiptSalesStatsRepository clickhouseReceiptSalesStatsRepository;
    private final TelegramAccountRepository telegramAccountRepository;
    private final DeviceRepository deviceRepository;
    private final ReceiptRepository receiptRepository;

    public void exchange(Update update) {
        if (update.message() != null) {
            if (update.message().contact() != null) {
                final com.pengrad.telegrambot.model.User from = update.message().from();
                List<TelegramAccount> accounts = telegramAccountService.findAllByTelegramId(from.id());
                log.info("a. 1. From:{}:{}", from.username(), from.id());
                log.info("a. 2. size of found accounts: {}", accounts.size());
                if (CollectionUtils.isEmpty(accounts)) {
                    Contact contact = update.message().contact() != null ?
                            update.message().contact() :
                            update.message().replyToMessage().contact();
                    final String phone = contact.phoneNumber().replace("+", "");
                    log.info("a. 3. Create account as no accounts are found");
                    createTelegramAccount(from, phone);
                } else {
                    for (TelegramAccount account : accounts) {
                        log.info("a. 4. sending home page message to account:{}", account.getName());
                        sendHomePageMessage(account);
                    }
                }
            } else if (update.message().text() != null) {
                if (update.message().from() != null && update.message().chat() != null
                    && Chat.Type.Private.equals(update.message().chat().type()) && !Boolean.TRUE.equals(update.message().from().isBot())) {

                    final com.pengrad.telegrambot.model.User from = update.message().from();
                    final Long chatId = update.message().chat().id();
                    List<TelegramAccount> accounts = telegramAccountService.findAllByTelegramId(from.id());
                    log.info("b. 1. From:{}:{}", from.username(), from.id());
                    log.info("b. 2. size of found accounts{}", accounts.size());
                    log.info("b. 3. chatId:{}", chatId);
                    if (CollectionUtils.isEmpty(accounts)) {
                        requestForContact(chatId);
                    } else {
                        for (TelegramAccount account : accounts) {
                            log.info("b. 4. account:{}", account.getName());
                            if (validate(account, chatId)) {
                                String message = update.message().text();
                                message = message.replace(Emoji.RU_FLAG.getUnicode(), "").replace(Emoji.UZ_FLAG.getUnicode(), "");
                                message = message.replace(Emoji.SETTINGS.getUnicode(), "").replace(Emoji.HELP.getUnicode(), "");
                                message = message.replace(Emoji.HOME.getUnicode(), "").replace(Emoji.ORDER.getUnicode(), "");
                                message = message.replace(Emoji.PHONE.getUnicode(), "").replace(Emoji.BACK.getUnicode(), "");
                                message = message.replace(Emoji.CASH.getUnicode(), "").replace(Emoji.CREDIT_CARD.getUnicode(), "");
                                message = message.replace(Emoji.EDIT_NEW.getUnicode(), "").replace(Emoji.REPORT.getUnicode(), "");
                                message = message.replace(Emoji.Z_REPORT.getUnicode(), "").replace(Emoji.RED_APPLE.getUnicode(), "");
                                message = message.replace(Emoji.CASHIER.getUnicode(), "").replace(Emoji.SHIFT.getUnicode(), "");
                                message = message.replace(Emoji.CHECK.getUnicode(), "").replace(Emoji.CANCEL.getUnicode(), "");
                                message = message.replace(Emoji.MENU.getUnicode(), "").replace(Emoji.LOCATION.getUnicode(), "");
                                message = message.replace(Emoji.PHONE_NEW.getUnicode(), "").replace(Emoji.TRASH.getUnicode(), "");
                                message = message.replace(Emoji.KORZINKA.getUnicode(), "").replace(Emoji.DOWN.getUnicode(), "");
                                log.info("b. 5. message:{}", account.getName());

                                switch (message) {
                                    case "/start":
                                    case "Bosh sahifa":
                                    case "На главную":
                                        sendHomePageMessage(account);
                                        break;
                                    case "O'zbekcha":
                                    case "Выбрать узбекский язык":
                                        sendChangeLanguageMessage(account, true);
                                        break;
                                    case "Русский":
                                    case "Rus tilini tanlash":
                                        sendChangeLanguageMessage(account, false);
                                        break;
                                    case "Statistika va hisobotlar":
                                    case "Статистика и отчеты":
                                        sendStatisticMessage(account);
                                        break;
                                    case "Z-hisobot", "Z-отчет":
                                        sendReportTypeMessage(account, ReportType.Z_REPORT);
                                        break;
                                    case "Statistika":
                                    case "Статистика":
                                        sendReportTypeMessage(account, ReportType.STATS);
                                        break;
                                    case "Faol ishchilar":
                                    case "Топ сотрудники":
                                        sendReportTypeMessage(account, ReportType.TOP_CASHIERS);
                                        break;
                                    case "Xaridorgir mahsulotlar":
                                    case "ТОП продажи":
                                        sendReportTypeMessage(account, ReportType.TOP_PRODUCTS);
                                        break;
                                    case "Orqaga":
                                    case "Назад":
                                        break;
                                    case "Yordam":
                                    case "Помощь":
                                        sendHelpMessage(account);
                                        break;
                                    case "Sozlamalar":
                                    case "Настройки":
                                        sendSettingsMessage(account);
                                        break;
                                    case "Tasdiqlash":
                                    case "Подтвердить":
                                        sendPhoneMessage(account);
                                        break;
                                    default:
                                        String messageText = "Invalid message!";
                                        botApi.sendMessageWithKeyboard(update.message().chat().id(), messageText, null, Long.valueOf(update.message().messageId()));
                                        break;
                                }
                            }
                        }
                    }
                }
            } else {
                String message = "Invalid message!";
                botApi.sendMessageWithKeyboard(update.message().chat().id(), message, null, Long.valueOf(update.message().messageId()));
            }
        } else if (update.callbackQuery() != null && update.callbackQuery().maybeInaccessibleMessage() != null && StringUtils.isNotEmpty(update.callbackQuery().data())) {
            final com.pengrad.telegrambot.model.User from = update.callbackQuery().from();
            final Long chatId = update.callbackQuery().maybeInaccessibleMessage().chat().id();
            final int messageId = update.callbackQuery().maybeInaccessibleMessage().messageId();
            List<TelegramAccount> accounts = telegramAccountService.findAllByTelegramId(from.id());
            if (accounts == null) {
                requestForContact(chatId);
            } else {
                for (TelegramAccount account : accounts) {
                    if (validate(account, chatId)) {
                        boolean isUzbek = account.isUzbek();
                        String data = update.callbackQuery().data();
                        if (data.contains("#")) {
                            ReportType reportType = ReportType.valueOf(data.split("#")[0]);
                            Long branchId = Long.valueOf(data.split("#")[1]);

                            StringBuilder message = new StringBuilder(isUzbek ? "Tanlangan shahobcha: " : "Выбранный филиал: ");
                            Branch branch = branchRepository.getReferenceById(branchId);
                            if (ReportType.TOP_PRODUCTS.equals(reportType)) {
                                sendTopProductsReport(account, branch);
                                return;
                            }
                            message.append("<b>").append(branch.getName()).append("</b>");
                            botApi.removeInlineKeyboard(account.getTelegramId(), messageId, message.toString());

                            if (ReportType.Z_REPORT.equals(reportType)) {
                                List<Device> terminals = deviceRepository.findAllByBranchIdAndType(branchId, DeviceType.FISCAL);
                                if (CollectionUtils.isNotEmpty(terminals)) {
                                    if (terminals.size() > 1) {
                                        sendTerminalMessage(account, terminals, reportType);
                                    } else {
                                        message = new StringBuilder(isUzbek ? "Tanlangan KKM: " : "Выбранный ККМ: ");
                                        message.append("<b>").append(terminals.getFirst().getSerialNumber()).append("</b>");
                                        botApi.sendMessageWithKeyboard(account.getTelegramId(), message.toString());

                                        sendReportMessage(account, null, terminals.getFirst().getId(), reportType);
                                    }
                                } else {
                                    botApi.sendMessageWithKeyboard(account.getTelegramId(), isUzbek ? "NKM mavjud emas" : "Нету ККМ");
                                }
                            } else {
                                sendReportMessage(account, branch, null, reportType);
                            }
                        } else if (data.contains("@")) {
                            ReportType reportType = ReportType.valueOf(data.split("@")[0]);
                            Long terminalId = Long.valueOf(data.split("@")[1]);

                            StringBuilder message = new StringBuilder(isUzbek ? "Tanlangan KKM: " : "Выбранный ККМ: ");
                            var terminal = deviceRepository.getReferenceById(terminalId);
                            message.append("<b>").append(terminal.getSerialNumber()).append("</b>");
                            botApi.sendMessageWithKeyboard(account.getTelegramId(), message.toString());

                            sendReportMessage(account, null, terminalId, reportType);
                        } else if (data.contains("%")) {
                            String identifier = data.split("%")[0];
                            String entityId = data.split("%")[1];
                            new StringBuilder(Emoji.CHECK.getUnicode());
                            if (Constants.QUOTE.equals(identifier)) {
                                String token = tokenStoreService.getToken(TokenType.BILLING);
                                billingServiceFeignClient.changeStatus(token, entityId, new QuoteChangeStatusRequestDto(entityId, ContractStatus.ACCEPTED.name()));
                                ResponseEntity<QuoteResponseDto> response = billingServiceFeignClient.getByQuoteId(token, entityId);
                                sendQuote(Objects.requireNonNull(response.getBody()), messageId);
                            }
                        }
                    }
                }
            }
        }
    }

    private void sendReportMessage(TelegramAccount telegramAccount, Branch branch, Long terminalId, ReportType reportType) {
        switch (reportType) {
            case Z_REPORT:
                sendXZReportMessage(telegramAccount, terminalId);
                break;
            case TOP_CASHIERS:
                sendTopCashiersReport(telegramAccount, branch);
                break;
            case TOP_PRODUCTS:
                sendTopProductsReport(telegramAccount, branch);
                break;
            case STATS:
                sendStatisticReport(telegramAccount, branch);
                break;
        }
    }

    private void sendTopProductsReport(TelegramAccount telegramAccount, Branch branch) {
        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), getTopProductContent(telegramAccount, branch));
    }

    private String getTopProductContent(TelegramAccount telegramAccount, Branch branch) {
        final boolean isUzbek = telegramAccount.isUzbek();
        StringBuilder content = new StringBuilder();
        content.append("**********");
        content.append(isUzbek ? "XARIDORGIR MAHSULOTLAR" : "ТОП ПРОДУКТЫ");
        content.append("**********");
        if (branch != null) {
            content.append("\n");
            content.append("----------");
            content.append(branch.getName());
            content.append("----------");
        }
        content.append("\n\n");

        SalesStatsFilter filter = new SalesStatsFilter();
        filter.setCompanyId(telegramAccount.getCompanyId());
        if (branch != null) {
            filter.setBranchId(branch.getId());
        }
        List<ProductStatsDTO> report = clickhouseReceiptDetailRepository.getTopProducts(filter);
        if (report.isEmpty()) {
            return isUzbek ? "Mahsulot mavjud emas" : "Продукты отсуствует";
        }
        int i = 1;
        for (ProductStatsDTO dto : report) {
            content.append(i++).append(". ");
            content.append(dto.getName());
            content.append("     | ");
            content.append("<b>").append(ServerUtils.formatPrice(dto.getSalesTotal())).append("</b>\n");
        }
        return content.toString();
    }

    private void sendStatisticReport(TelegramAccount telegramAccount, Branch branch) {
        final boolean isUzbek = telegramAccount.isUzbek();
        SalesStatsFilter filter = new SalesStatsFilter();
        filter.setCompanyId(branch.getCompanyId());
        filter.setBranchId(branch.getId());
        OverallSalesStatsDTO dto = clickhouseReceiptSalesStatsRepository.getSalesStats(filter);
        StringBuilder content = new StringBuilder();
        if (dto != null) {
            content.append("**********");
            content.append(isUzbek ? "STATISTIKA" : "СТАТИСТИКА");
            content.append("**********");
            content.append("\n\n");
            content.append(isUzbek ? "Umumiy savdo: " : "Общая продажа: ").append("<b>").append(ServerUtils.formatPrice(dto.getSalesTotal())).append("</b> \n");
            content.append(isUzbek ? "Cheklar soni: " : "Количество чеков: ").append("<b>").append(dto.getSalesCount()).append("</b> \n");
            content.append(isUzbek ? "Naqd: " : "Наличными: ").append("<b>").append(ServerUtils.formatPrice(dto.getSalesCash())).append("</b> \n");
            content.append(isUzbek ? "Kartochka: " : "Карточка: ").append("<b>").append(ServerUtils.formatPrice(dto.getSalesCard())).append("</b> \n");
            content.append(isUzbek ? "Umumiy chegirma: " : "Общая скидка: ").append("<b>").append(ServerUtils.formatPrice(dto.getDiscount())).append("</b> \n");
            content.append(isUzbek ? "QQS: " : "НДС: ").append("<b>").append(ServerUtils.formatPrice(dto.getNds())).append("</b> \n");
            content.append(isUzbek ? "Aksiz: " : "Акциз: ").append("<b>").append(ServerUtils.formatPrice(dto.getExcise())).append("</b> \n");
            content.append(isUzbek ? "Qaytim: " : "Возврат: ").append("<b>").append(ServerUtils.formatPrice(dto.getReturned())).append("</b> \n");
        } else {
            content.append(isUzbek ? "Statistika mavjud emas" : "Статистики отсуствует");
        }

        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), content.toString());
    }

    private void sendTopCashiersReport(TelegramAccount telegramAccount, Branch branch) {
        final boolean isUzbek = telegramAccount.isUzbek();
        StringBuilder content = new StringBuilder();
        content.append("**********");
        content.append(isUzbek ? "FAOL ISHCHILAR" : "ТОП СОТРУДНИКИ");
        content.append("**********");
        content.append("\n\n");

        SalesStatsFilter statsFilter = new SalesStatsFilter();
        statsFilter.setSize(10);
        statsFilter.setCompanyId(branch.getCompanyId());
        statsFilter.setBranchId(branch.getId());
        Page<UserSalesStatsDTO> report = clickhouseReceiptSalesStatsRepository.getUserSalesStats(statsFilter, false);
        if (report.getTotalElements() == 0) {
            botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), isUzbek ? "Faol ishchilar mavjud emas" : "Топ сотрудники отсуствует");
            return;
        }
        int i = 1;
        for (UserSalesStatsDTO dto : report.getContent()) {
            content.append(i++).append(". ");
            content.append(dto.getName());
            content.append("     | ");
            content.append("<b>").append(ServerUtils.formatPrice(dto.getSalesTotal())).append("</b>\n");
        }
        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), content.toString());
    }

    private void sendXZReportMessage(TelegramAccount telegramAccount, Long terminalId) {
        final boolean isUzbek = telegramAccount.isUzbek();
        Optional<ShiftReport> shift = shiftReportRepository.findFirstByTerminalIdOrderByEndDateTimeDesc(terminalId);
        if (shift.isPresent()) {
            sendXZReport(telegramAccount.getTelegramId(), shift.get().getDTO(), isUzbek);
        } else {
            botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), isUzbek ? "Smena mavjud emas" : "Смена отсуствует");
        }
    }

    public void sendXZReport(Long telegramId, ShiftReportDTO dto, boolean isUzbek) {
        botApi.sendMessageWithKeyboard(telegramId, getXZReportContent(dto, isUzbek));
    }

    private String getXZReportContent(ShiftReportDTO dto, boolean isUzbek) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder content = new StringBuilder();
        content.append("**********");
        content.append(isUzbek ? "Z-HISOBOT" : "Z-ОТЧЕТ");
        if (dto.getFiscalNumber() != null) {
            content.append(" <b>№").append(dto.getFiscalNumber()).append("</b>");
        }
        content.append("**********");
        content.append("\n\n");
        if (dto.getBusinessType() != null) {
            content.append(isUzbek ? dto.getBusinessType().getNameUzCyrillic() : dto.getBusinessType().getNameRu());
            content.append(" ");
        }
        if (StringUtils.isNotEmpty(dto.getCompanyName())) {
            content.append("<b>").append(dto.getCompanyName()).append("</b>\n");
        }
        if (StringUtils.isNotEmpty(dto.getBranchName())) {
            content.append("<b>").append(dto.getBranchName()).append("</b>\n");
        }
        if (StringUtils.isNotEmpty(dto.getBranchAddress())) {
            content.append("<b>").append(dto.getBranchAddress()).append("</b>\n");
        }
        content.append("\n");
        if (dto.getCompanyDateTime() != null) {
            content.append(isUzbek ? "Ro'yxatga olingan sanasi: " : "Дата регистрации: ")
                    .append("<b>").append(formatter.format(dto.getCompanyDateTime())).append("</b>\n");
        }
        if (StringUtils.isNotEmpty(dto.getCompanyINN())) {
            if (dto.getCompanyINN().length() > 9) {
                content.append(isUzbek ? "JSH SHIR: " : "ПИНФЛ: ");
            } else {
                content.append(isUzbek ? "STIR: " : "ИНН: ");
            }
            content.append("<b>").append(dto.getCompanyINN()).append("</b>\n");
        }
        if (StringUtils.isNotEmpty(dto.getTerminalSN())) {
            content.append(isUzbek ? "NKM: " : "ККМ: ")
                    .append("<b>").append(dto.getTerminalSN()).append("</b>\n");
        }
        content.append("------------------------------------\n");

        if (dto.getStartDateTime() != null) {
            content.append(isUzbek ? "Smena ochilgan sanasi: " : "Дата открытия смены: ")
                    .append("<b>").append(formatter.format(dto.getStartDateTime())).append("</b>\n");
        }
        if (dto.getEndDateTime() != null) {
            content.append(isUzbek ? "Smena yopilgan sanasi: " : "Дата закрытия смены: ")
                    .append("<b>").append(formatter.format(dto.getEndDateTime())).append("</b>\n");
        }
        content.append("\n\n");

        BigDecimal totalCard = Optional.ofNullable(dto.getTotalCard()).orElse(BigDecimal.ZERO);
        BigDecimal totalCash = Optional.ofNullable(dto.getTotalCash()).orElse(BigDecimal.ZERO);
        BigDecimal totalCardReturned = Optional.ofNullable(dto.getTotalCardReturned()).orElse(BigDecimal.ZERO);
        BigDecimal totalCashReturned = Optional.ofNullable(dto.getTotalCashReturned()).orElse(BigDecimal.ZERO);
        BigDecimal total = totalCard.add(totalCash);
        BigDecimal totalReturned = totalCardReturned.add(totalCashReturned);
        content.append(isUzbek ? "KIRIM          " : "ПРИХОД         ").append("=").append("<b>").append(total).append("</b>\n");
        content.append("  ").append(isUzbek ? "NAQD         " : "НАЛИЧНЫМИ    ").append("=").append("<b>").append(totalCash).append("</b>\n");
        content.append("  ").append(isUzbek ? "KARTA        " : "КАРТА        ").append("=").append("<b>").append(totalCard).append("</b>\n");
        content.append(isUzbek ? "QAYTIMLAR      " : "ВОЗВРАТЫ       ").append("=").append("<b>").append(totalReturned).append("</b>\n");
        content.append("  ").append(isUzbek ? "NAQD         " : "НАЛИЧНЫМИ    ").append("=").append("<b>").append(totalCashReturned).append("</b>\n");
        content.append("  ").append(isUzbek ? "KARTA        " : "КАРТА        ").append("=").append("<b>").append(totalCardReturned).append("</b>\n");
        content.append("------------------------------------\n");

        Long receipts = Optional.ofNullable(dto.getReceipts()).orElse(0L);
        Long receiptsReturned = Optional.ofNullable(dto.getReceiptsReturned()).orElse(0L);
        Long receiptsCount = receipts + receiptsReturned;
        BigDecimal totalNds = Optional.ofNullable(dto.getNDS()).orElse(BigDecimal.ZERO);
        content.append(isUzbek ? "CHEKLAR SONI   " : "КОЛ-ВО ЧЕКОВ   ").append("=").append("<b>").append(receiptsCount).append("</b>\n");
        content.append("  ").append(isUzbek ? "KIRIM        " : "ПРИХОД       ").append("=").append("<b>").append(receipts).append("</b>\n");
        content.append("  ").append(isUzbek ? "QAYTIMLAR    " : "ВОЗВРАТЫ     ").append("=").append("<b>").append(receiptsReturned).append("</b>\n");
        content.append(isUzbek ? "JAMI QQS       " : "СУММА НДС      ").append("=").append("<b>").append(totalNds).append("</b>\n");
        if (StringUtils.isNotEmpty(dto.getUserName())) {
            content.append("------------------------------------\n");
            content.append(isUzbek ? "Kassir: " : "Кассир: ")
                    .append("<b>").append(dto.getUserName()).append("</b>\n");
        }

        return content.toString();
    }

    public void sendQuote(QuoteResponseDto dto, int messageId) {
        Company company = companyRepository.findOneByInn(dto.getTin())
                .orElseThrow(notFound("Company"));
        if (company.getOwner() == null) {
            throw badRequest("Owner not found").get();
        }
        Optional<TelegramAccount> telegramAccount = telegramAccountRepository.findFirstByPhoneAndTelegramIdIsNotNull(company.getOwner().getLogin());
        if (telegramAccount.isEmpty()) {
            throw notFound("Telegram").get();
        }
        StringBuilder message = getQuoteContent(dto, telegramAccount.get().getPhone());
        if (messageId == 0) {
            InlineKeyboardButton approveButton = new InlineKeyboardButton(telegramAccount.get().isUzbek() ? "Tasdiqlash" : "Подтвердить").callbackData(Constants.QUOTE + "%" + dto.getId());
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(approveButton);
            botApi.sendMessageWithKeyboard(telegramAccount.get().getTelegramId(), message.toString(), keyboardMarkup);
        } else {
            message.append("\n\n").append(Emoji.CHECK.getUnicode()).append("Счет на оплату успешно подтвержден");
            botApi.removeInlineKeyboard(telegramAccount.get().getTelegramId(), messageId, message.toString());
        }
    }

    private StringBuilder getQuoteContent(QuoteResponseDto dto, String phoneNumber) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        StringBuilder content = new StringBuilder();
        content.append("Счет на оплату <b>№")
                .append(dto.getDocumentNumber())
                .append(" от ")
                .append(formatter.format(dto.getCreatedDate())).append(" г.</b>");

        content.append("\n\n");

        content.append("Поставщик:\n");
        content.append("<b>ИНН 306344019, ОКЭД 58290, OOO \"CENTER FOR DIGITAL TECHNOLOGY AND INNOVATION\"</b>\n");

        content.append("Покупатель:\n");
        content.append("<b>");
        content.append("ИНН ").append(dto.getTin());
        if (StringUtils.isNotEmpty(dto.getCustomerOKED())) {
            content.append(", ОКЭД ").append(dto.getCustomerOKED());
        }
        if (StringUtils.isNotEmpty(dto.getCustomerName())) {
            content.append(", ").append(dto.getCustomerName());
        }
        if (StringUtils.isNotEmpty(dto.getCustomerAddress())) {
            content.append(", ").append(dto.getCustomerAddress());
        }
        if (StringUtils.isNotEmpty(phoneNumber)) {
            content.append(", тел.:").append(phoneNumber);
        }
        content.append("</b>");

        content.append("\n\n");

        content.append("Договор: \n")
                .append("<b>оферта ")
                .append(dto.getPublicOfferNumber())
                .append(" от ")
                .append(formatter.format(dto.getPublicOfferCreateDate()))
                .append("</b>");

        content.append("\n\n");

        content.append("Сумма: <b>").append(ServerUtils.formatPrice(dto.getTotalPriceWithVat())).append(" сум</b>")
                .append("()");

        return content;
    }

    private void sendTerminalMessage(TelegramAccount telegramAccount, List<Device> terminals, ReportType
            reportType) {
        boolean isUzbek = telegramAccount.isUzbek();
        int size = terminals.size();
        InlineKeyboardButton[][] branchButtons;
        InlineKeyboardButton[] buttons;
        int k = 0;
        branchButtons = new InlineKeyboardButton[size][];
        for (var terminal : terminals) {
            buttons = new InlineKeyboardButton[1];
            buttons[0] = new InlineKeyboardButton(terminal.getSerialNumber()).callbackData(reportType.name() + "@" + terminal.getId());
            branchButtons[k++] = buttons;
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(branchButtons);
        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), (isUzbek ? "KKMlardan birini tanlang" : "Выберите один из ККМ"), inlineKeyboardMarkup);
    }

    private void sendReportTypeMessage(TelegramAccount telegramAccount, ReportType reportType) {
        boolean isUzbek = telegramAccount.isUzbek();
        List<Branch> branchList = branchRepository.findAllByCompanyIdAndFiscalIsTrueAndDeletedIsFalse(telegramAccount.getCompanyId());
        if (CollectionUtils.isEmpty(branchList)) {
            botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), isUzbek ? "Faol shahobchalar mavjud emas" : "Нету активных филиалов");
            return;
        } else if (branchList.size() == 1) {
            final Branch branch = branchList.getFirst();

            if (ReportType.TOP_PRODUCTS.equals(reportType)) {
                sendTopProductsReport(telegramAccount, branch);
                return;
            }

            StringBuilder message = new StringBuilder(isUzbek ? "Tanlangan shahobcha: " : "Выбранный филиал: ");
            message.append("<b>").append(branch.getName()).append("</b>");
            botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), message.toString());

            if (ReportType.Z_REPORT.equals(reportType)) {
                List<Device> terminals = deviceRepository.findAllByBranchIdAndType(branch.getId(), DeviceType.FISCAL);
                if (CollectionUtils.isNotEmpty(terminals)) {
                    if (terminals.size() > 1) {
                        sendTerminalMessage(telegramAccount, terminals, reportType);
                    } else {
                        message = new StringBuilder(isUzbek ? "Tanlangan KKM: " : "Выбранный ККМ: ");
                        message.append("<b>").append(terminals.getFirst().getSerialNumber()).append("</b>");
                        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), message.toString());

                        sendReportMessage(telegramAccount, null, terminals.getFirst().getId(), reportType);
                    }
                } else {
                    botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), isUzbek ? "NKM mavjud emas" : "Нету ККМ");
                }
            } else {
                sendReportMessage(telegramAccount, branch, null, reportType);
            }
            return;
        }
        int size = branchList.size();
        boolean twoSide = branchList.size() > 10;
        InlineKeyboardButton[][] branchButtons;
        InlineKeyboardButton[] buttons;
        int k = 0;
        if (twoSide) {
            branchButtons = new InlineKeyboardButton[size % 2 == 0 ? size / 2 : size / 2 + 1][];
            for (int i = 0; i < size; i += 2) {
                buttons = new InlineKeyboardButton[size > i + 1 ? 2 : 1];
                buttons[0] = new InlineKeyboardButton(branchList.get(i).getName()).callbackData(reportType.name() + "#" + branchList.get(i).getId());
                if (size > i + 1) {
                    buttons[1] = new InlineKeyboardButton(branchList.get(i + 1).getName()).callbackData(reportType.name() + "#" + branchList.get(i + 1).getId());
                }
                branchButtons[k++] = buttons;
            }
        } else {
            branchButtons = new InlineKeyboardButton[size][];
            for (Branch branch : branchList) {
                buttons = new InlineKeyboardButton[1];
                buttons[0] = new InlineKeyboardButton(branch.getName()).callbackData(reportType.name() + "#" + branch.getId());
                branchButtons[k++] = buttons;
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(branchButtons);
        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), (isUzbek ? "Shahobchalardan birini tanlang" : "Выберите один из филиалов"), inlineKeyboardMarkup);
    }

    private void sendStatisticMessage(TelegramAccount telegramAccount) {
        boolean isUzbek = telegramAccount.isUzbek();

        String message = (isUzbek ? "Hisobot turini tanlang" : "Выберите тип отчета");

        String zReportButton = Emoji.Z_REPORT.getUnicode() + (isUzbek ? "Z-hisobot" : "Z-отчет");
        String shiftButton = Emoji.SHIFT.getUnicode() + (isUzbek ? "Statistika" : "Статистика");
        String cashiersButton = Emoji.CASHIER.getUnicode() + (isUzbek ? "Faol ishchilar" : "Топ сотрудники");
        String productsButton = Emoji.RED_APPLE.getUnicode() + (isUzbek ? "Xaridorgir mahsulotlar" : "ТОП продажи");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{zReportButton, shiftButton},
                new String[]{cashiersButton, productsButton},
                new String[]{Emoji.HOME.getUnicode() + (isUzbek ? "Bosh sahifa" : "На главную")});
        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), message, keyboardMarkup);
    }

    public void sendLanguageMessage(Long chatId) {
        String uzFlag = Emoji.UZ_FLAG.getUnicode() + "O'zbekcha";
        String ruFlag = Emoji.RU_FLAG.getUnicode() + "Русский";
        String message = Emoji.UZ_FLAG.getUnicode() + " Tilni tanlang " + Emoji.RU_FLAG.getUnicode() + " Выберите язык";
        botApi.sendMessageWithKeyboard(chatId, message, new ReplyKeyboardMarkup(uzFlag, ruFlag));
    }

    public void requestForContact(Long telegramId) {
        String messageText = Emoji.HANDPHONE.getUnicode() + "Bog'lanish uchun Telefon raqamingizni jo'nating";
        botApi.sendMessageWithKeyboard(telegramId, messageText, new ReplyKeyboardMarkup(new KeyboardButton(Emoji.PHONE.getUnicode() + "Telefon raqamni jo'natish").requestContact(true)));
    }

    public void sendHomePageMessage(TelegramAccount telegramAccount) {
        boolean isUzbek = telegramAccount.isUzbek();

        StringBuilder message = new StringBuilder(Emoji.FAX.getUnicode()).append(isUzbek ? "Assalomu aleykum" : "Здравствуйте");
        if (StringUtils.isNotEmpty(telegramAccount.getName())) {
            message.append(", <b>").append(telegramAccount.getName()).append("</b>");
        }
        message.append("!\n\n");
        if (isUzbek) {
            message.append("SmartPOS tizimiga xush kelibsiz!\n\n");
        } else {
            message.append("Добро пожаловать в систему SmartPOS!\n\n");
        }
        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), message.toString(), getHomeButtons(isUzbek));
    }

    public void sendSettingsMessage(TelegramAccount telegramAccount) {//Todo add phone change
        boolean isUzbek = telegramAccount.isUzbek();
        String languageButton = (isUzbek ? Emoji.RU_FLAG.getUnicode() : Emoji.UZ_FLAG.getUnicode()) + (isUzbek ? "Rus tilini tanlash" : "Выбрать узбекский язык");
//        StringBuilder changeNumberButton = new StringBuilder(Emoji.PHONE.getUnicode()).append(isUzbek ? "Raqamni uzgartirish" : "Изменить номер");
        String homeButton = Emoji.HOME.getUnicode() + (isUzbek ? "Bosh sahifa" : "На главную");
        String message = "<b>" + (isUzbek ? "Til:" : "Язык:") + "</b> " +
                         (isUzbek ? Emoji.UZ_FLAG.getUnicode() : Emoji.RU_FLAG.getUnicode()) +
                         (isUzbek ? "O'zbekcha" : "Русский") +
                         "\n\n";
        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), message, new ReplyKeyboardMarkup(
                new String[]{languageButton},
//            new String[]{changeNumberButton.toString()},
                new String[]{homeButton}));
    }

    public void sendHelpMessage(TelegramAccount telegramAccount) {
        boolean isUzbek = telegramAccount.isUzbek();
        StringBuilder message = new StringBuilder();
        if (isUzbek) {
            message.append("71 200-30-50 yoki 71 200-10-70 raqamlariga murojaat qiling");
        } else {
            message.append("Свяжитесь по номеру <b>71 200-30-50</b> или <b>71 200-10-70</b>");
        }
        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), message.toString());
    }

    public void sendChangeLanguageMessage(TelegramAccount telegramAccount, boolean isUzbek) {
        telegramAccount.setLangKey(isUzbek ? "uz" : "ru");
        telegramAccountService.save(telegramAccount);
        StringBuilder message = new StringBuilder(Emoji.FAX.getUnicode()).append(isUzbek ? "Assalomu aleykum" : "Здравствуйте");
        if (StringUtils.isEmpty(telegramAccount.getName())) {
            message.append(", <b>").append(telegramAccount.getName()).append("</b>");
        }
        message.append("!\n\n");
        if (isUzbek) {
            message.append("SmartPOS tizimiga xush kelibsiz!\n\n");
        } else {
            message.append("Добро пожаловать в систему SmartPOS!\n\n");
        }
        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), message.toString(), getHomeButtons(isUzbek));
    }

    public void sendPhoneMessage(TelegramAccount telegramAccount) {
        boolean isUzbek = telegramAccount.isUzbek();
        String message = (isUzbek ? "Siz bilan bog'lanish uchun telefon raqam kiriting" : "Введите номер телефона чтобы связаться с Вами") + "\n" +
                         (isUzbek ? "O'z raqamingiz bilan davom etish uchun <b>Akkaunt raqami</b> tugmasini bosing" : "Чтобы продолжить со своим номером нажмите на кнопку <b>Номер аккаунта</b>");

        String agreeButton = Emoji.PHONE_NEW.getUnicode() + (isUzbek ? "Akkaunt raqami" : "Номер аккаунта");

        botApi.sendMessageWithKeyboard(telegramAccount.getTelegramId(), message,
                new ReplyKeyboardMarkup(agreeButton, Emoji.BACK.getUnicode() + (isUzbek ? "Orqaga" : "Назад")));
    }

    public ReplyKeyboardMarkup getHomeButtons(boolean isUzbek) {
        String reportButton = Emoji.REPORT.getUnicode() + (isUzbek ? "Statistika va hisobotlar" : "Статистика и отчеты");
        String settingsButton = Emoji.SETTINGS.getUnicode() + (isUzbek ? "Sozlamalar" : "Настройки");
        return new ReplyKeyboardMarkup(
                new String[]{reportButton},
                new String[]{Emoji.HELP.getUnicode() + (isUzbek ? "Yordam" : "Помощь")},
                new String[]{settingsButton});
    }

    public void createTelegramAccount(com.pengrad.telegrambot.model.User from, String phone) {
        List<TelegramAccount> telegramAccounts = telegramAccountService.findByPhone(phone);
        log.info("x. size of accounts by \"{}\":{}", phone, telegramAccounts.size());
        if (CollectionUtils.isEmpty(telegramAccounts)) {
            botApi.sendMessageWithKeyboard(from.id(), "Bu akkaunt raqami <b>SmartPOS</b> tizimida ro'yxatdan o'tmagan");
            return;
        }
        for (TelegramAccount account : telegramAccounts) {
            if (account.getTelegramId() == null) {
                account.setTelegramId(from.id());
                account.setName(from.username());
                telegramAccountService.save(account);
            }
        }
        sendLanguageMessage(from.id());
    }

    public boolean validate(TelegramAccount telegramAccount, Long chatId) {
        if (telegramAccount == null || !telegramAccount.isEnabled()) {
            StringBuilder message = new StringBuilder(Emoji.STOP.getUnicode());
            boolean isUzbek = telegramAccount != null ? telegramAccount.isUzbek() : false;
            if (isUzbek) {
                message.append("Sizning mazkur botdan foydalanish huquqingiz cheklab qo'yilgan.\n ");
                message.append("Ma'lumot uchun administratorga murojaat qiling.");
            } else {
                message.append("Ваше право пользования этим ботом ограничено. \n").append("Для информации обратитесь к администратору ");
            }
            message.append(Emoji.DOWN.getUnicode());
            InlineKeyboardButton adminButton = new InlineKeyboardButton(isUzbek ? "Ulanish" : "Связаться").url("https://t.me/" + applicationProperties.getAdminName());
            botApi.sendMessageWithKeyboard(chatId, message.toString(), new InlineKeyboardMarkup(adminButton));
            return false;
        }
        return true;
    }

    public List<TelegramAccountDTO> getTelegramAccountsByCompanyId(Long companyId) {
        TelegramAccountFilter filter = new TelegramAccountFilter();
        filter.setCompanyId(companyId);
        filter.setSize(100);// hope they don't have more than 100 telegram accounts.
        return telegramAccountService.findAllByFilter(filter).getContent();
    }

    public void sendNonFiscalReceiptMessage(Long id) {
        Receipt receipt = receiptRepository.findById(id).orElseThrow();
        StringBuilder text = new StringBuilder("<b>!!!!! #Нефискальныйчек !!!!!</b>\n");
        text.append("------------------------------------\n\n");
        if (receipt.getCompany() != null) {
            text.append("<b>ИНН:</b> ").append(receipt.getCompany().getInn()).append("\n");
            text.append("<b>КОМПАНИЯ:</b> ").append(receipt.getCompany().getName()).append("\n");
        }
        if (receipt.getBranch() != null) {
            text.append("<b>ФИЛИАЛ:</b> ").append(receipt.getBranch().getName()).append("\n");
        }
        if (receipt.getCreatedBy() != null) {
            text.append("<b>КАССИР:</b> ").append(receipt.getCreatedBy().getFullNameStr()).append("\n");
        }
        if (receipt.getTerminal() != null) {
            text.append("<b>ККМ:</b> ").append(receipt.getTerminal().getSerialNumber()).append("\n");
        }
        text.append("<b>ВЕРСИЯ:</b> ").append(Optional.ofNullable(receipt.getAdditional()).map(Additional::getTerminalVersion).orElse("")).append("\n");
        text.append("<b>ДАТА:</b> ").append(DateUtils.toString(receipt.getReceiptDateTime())).append("\n");
        text.append("<b>СУММА:</b> ").append(ServerUtils.formatPrice(receipt.getTotalPaid())).append("\n");
        text.append("<b>СТАТУС:</b> ").append(receipt.getStatus().getNameRu()).append("\n");
        text.append("<b>UID:</b> ").append(receipt.getUid()).append("\n");
        botApi.sendMessageToAdminChannel(text.toString());
    }


    public void sendWrongPaymentsToTelegram(String payload) {
        botApi.sendMessageToAdminChannel(payload);
    }
}
