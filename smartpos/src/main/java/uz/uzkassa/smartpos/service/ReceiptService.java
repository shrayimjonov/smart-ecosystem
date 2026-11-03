package uz.uzkassa.smartpos.service;

import com.google.common.collect.Sets;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.postgres.catalog.Mxik;
import uz.uzkassa.smartpos.domain.postgres.common.Vat;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.receipt.Additional;
import uz.uzkassa.smartpos.domain.postgres.receipt.ExtraDetail;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.dto.EpsServicesLookupDto;
import uz.uzkassa.smartpos.dto.ListResult;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatDto;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatisticByStatusesDTO;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.dto.filter.receipt.CreditAdvanceReceiptFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.integration.CheckReturnedReceiptDto;
import uz.uzkassa.smartpos.dto.integration.ReceiptReturnDto;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.mq.Payload;
import uz.uzkassa.smartpos.dto.mq.PaymentPayload;
import uz.uzkassa.smartpos.dto.receipt.*;
import uz.uzkassa.smartpos.dto.sales.SalesCreateDto;
import uz.uzkassa.smartpos.enums.*;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.apay.ApayClient;
import uz.uzkassa.smartpos.integration.enums.MerchantReferenceType;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;
import uz.uzkassa.smartpos.mappers.ReceiptMapper;
import uz.uzkassa.smartpos.mappers.SalesMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptGenericRepository;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptSalesStatsRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.MxikRepository;
import uz.uzkassa.smartpos.repository.postgres.common.VatRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptRepository;
import uz.uzkassa.smartpos.repository.postgres.sales.SalesRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.security.HeaderUtils;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.clickhouse.ClickhouseService;
import uz.uzkassa.smartpos.utils.EncryptionUtils;
import uz.uzkassa.smartpos.utils.MarkingUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService extends SmartposService {
    private final MxikRepository mxikRepository;
    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;
    private final ClickhouseService clickhouseService;
    private final PushkassaNotificationService notificationService;
    private final EtcSmsService etcSmsService;
    private final TokenStoreService tokenStoreService;
    private final RabbitMqProducer rabbitMqProducer;
    private final ClickhouseReceiptGenericRepository clickhouseReceiptGenericRepository;
    private final ClickhouseReceiptSalesStatsRepository clickhouseReceiptSalesStatsRepository;
    private final ApayClient apayClient;
    private final SalesMapper salesMapper;
    private final SalesRepository salesRepository;
    private final VatRepository vatRepository;
    private final DeviceRepository deviceRepository;

    public Page<ReceiptDTO> getReceipts(ReceiptFilter filter, boolean forAdmin) {
        String terminalSN = HeaderUtils.getCurrentHeaderByKey(HeaderKeys.TERMINAL_SN);
        if (!forAdmin) {
            filter.setCompanyId(getCurrentCompanyId());
            Company company = companyRepository.findById(filter.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
            if (StringUtils.isBlank(terminalSN) && !company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.FISCAL_REPORT, LocalDateTime.MIN).isAfter(LocalDateTime.now()) && (filter.getFrom() == null || filter.getFrom().isBefore(LocalDateTime.now().minusDays(1)))) {
                throw new BadRequestException("Ошибка! Для детального просмотра приобретите услугу Архив");
            }
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        if (ReceiptStatus.DRAFT.equals(filter.getStatus())) {
            return receiptRepository.findAllDrafts(filter).map(Receipt::toListDto);
        } else {
            return clickhouseReceiptGenericRepository.findAllByFilter(filter, false);
        }
    }

    public ListResult<ReceiptDTO> getReceiptList(ReceiptFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        if (filter.getBranchId() == null && !isOwner()) {
            filter.setBranchId(getCurrentBranchId());
        }
        if (isCashier()) {
            filter.setUserId(getCurrentUserId());
        }
        if (ReceiptStatus.DRAFT.equals(filter.getStatus())) {
            Page<Receipt> page = receiptRepository.findAllDrafts(filter);
            List<ReceiptDTO> list = page.stream().map(receipt -> receipt.getDTO(false, false)).collect(Collectors.toList());
            return new ListResult<>(list, page.getTotalElements());
        } else {
            Page<ReceiptDTO> page = clickhouseReceiptGenericRepository.findAllByFilter(filter, false);
            return new ListResult<>(page.getContent(), page.getTotalElements());
        }
    }

    public ReceiptStatDto getReceiptStat(ReceiptFilter filter, boolean forAdmin) {
        if (!forAdmin && filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!forAdmin) {
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return clickhouseReceiptSalesStatsRepository.getReceiptStat(filter);
    }

    public ReceiptStatisticByStatusesDTO getReceiptStatisticByStatuses(ReceiptFilter filter, boolean forAdmin) {
        if (!forAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        Company company = companyRepository.findById(filter.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        if (!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.FISCAL_REPORT, LocalDateTime.MIN).isAfter(LocalDateTime.now()) && (filter.getFrom() == null || filter.getFrom().isBefore(LocalDateTime.now().minusDays(1)))) {
            throw new BadRequestException("Ошибка! Для детального просмотра приобретите услугу Архив");
        }
        if (!forAdmin) {
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return clickhouseReceiptSalesStatsRepository.getReceiptStatisticByStatuses(filter);
    }

    public ReceiptDTO getByUidMobile(String uid) {
        ReceiptDTO receipt = receiptRepository.findOneByUid(uid)
                .map(r -> r.getDTO(true, false))
                .orElseThrow(notFound(localizationService.getMessage("receipt"), uid, 400));

        if (!receipt.getCompanyId().equals(getCurrentCompanyId())) {
            throw badRequest(localizationService.getMessage("receipt.not.belong.organization")).get();
        }
        final String terminalSN = getCurrentTerminalSnByHeader();
        if (StringUtils.isNotEmpty(terminalSN) && !terminalSN.equals(receipt.getTerminalSN())) {
            throw badRequest(localizationService.getMessage("receipt.not.printed.kkm")).get();
        }
        return receipt;
    }

    public ReceiptDTO getByUid(String uid) {
        Optional<ReceiptDTO> optionalReceipt = receiptRepository.findOneByUid(uid).map(receipt -> receipt.getDTO(true, false));
        if (optionalReceipt.isEmpty()) throw notFound(localizationService.getMessage("receipt"), uid, 400).get();
        ReceiptDTO result = optionalReceipt.get();
        if (StringUtils.isAnyEmpty(result.getFiscalSign(), result.getFiscalUrl())) {
            throw badRequest(localizationService.getMessage("receipt.not.fiscal")).get();
        }
        return result;
    }

    public List<ReceiptDTO> getAllByOriginUid(String uid) {
        List<ReceiptDTO> list = clickhouseReceiptGenericRepository.findAllByOriginUid(uid);
        if (list != null && !list.isEmpty() && getCurrentCompanyId().equals(list.getFirst().getCompanyId())) {
            return list;
        } else {
            throw notFound(localizationService.getMessage("receipt")).get();
        }
    }

    public ReceiptDTO getById(Long id, boolean noFiscal) {
        Optional<ReceiptDTO> receiptOptional = clickhouseReceiptGenericRepository.findById(id, noFiscal);
        ReceiptDTO receipt = receiptOptional.orElseGet(() ->
                receiptRepository.findById(id)
                        .map(entity -> entity.getDTO(true, noFiscal))
                        .orElseThrow(notFound(localizationService.getMessage("receipt"), id)));
        if (receipt.getCompanyId() != null) {
            checkForAccessToCompany(receipt.getCompanyId());
        }
        if (StringUtils.isBlank(receipt.getTerminalSN())) {
            receipt.setTerminalSN(branchRepository.getReferenceById(receipt.getBranchId()).getSerialNumber());
        }
        return receipt;
    }

    public ReceiptDTO getByFiscalUrl(String fiscalUrl) {
        fiscalUrl = fiscalUrl.replaceAll("\"", "");
        return clickhouseReceiptGenericRepository.findFirstByFiscalUrl(fiscalUrl)
                .orElseThrow(notFound(localizationService.getMessage("receipt"), fiscalUrl));
    }

    /*
        Batch сохранение чеков
     */
    public void saveReceiptList(List<ReceiptDTO> receiptList) {
        if (CollectionUtils.isNotEmpty(receiptList)) {
            final Long currentBranchId = getCurrentBranchId();
            final Long currentCompanyId = getCurrentCompanyId();
            final String terminalSN = getCurrentTerminalSnByHeader();
            Set<String> uidList = receiptList.stream().map(ReceiptDTO::getUid).collect(Collectors.toSet());
            uidList = receiptRepository.getAlreadyUsedUidList(uidList);
            for (ReceiptDTO receiptDTO : receiptList) {
                if (uidList.contains(receiptDTO.getUid())) {
                    continue;
                }
                receiptDTO.setCompanyId(currentCompanyId);
                if (receiptDTO.getBranchId() == null) {
                    receiptDTO.setBranchId(currentBranchId);
                }
                if (StringUtils.isEmpty(receiptDTO.getTerminalSN())) {
                    receiptDTO.setTerminalSN(terminalSN);
                }

                receiptDTO.setUserId(receiptDTO.getUser() == null ?
                        receiptDTO.getUserId() == null ?
                                getCurrentUserId() : receiptDTO.getUserId() : receiptDTO.getUser().getId());

                rabbitMqProducer.sendWithType(receiptDTO, MqConstants.RECEIPT_QUEUE, "receipt_payload");
            }
        }
    }

    public void saveReceiptListV2(List<SalesCreateDto> receiptList) {
        if (CollectionUtils.isNotEmpty(receiptList)) {
            final Long currentBranchId = getCurrentBranchId();
            final Long currentCompanyId = getCurrentCompanyId();
            final String terminalSN = getCurrentTerminalSnByHeader();
            Set<String> uidList = receiptList.stream().map(SalesCreateDto::getUid).collect(Collectors.toSet());
            final var existingUids = receiptRepository.getAlreadyUsedUidList(uidList);
            receiptList.stream()
                    .filter(receipt -> !existingUids.contains(receipt.getUid()))
                    .peek(receipt -> {
                        receipt.setCompanyId(currentCompanyId);
                        receipt.setBranchId(Optional.ofNullable(receipt.getBranchId()).orElse(currentBranchId));
                        receipt.setTerminalSN(Optional.ofNullable(receipt.getTerminalSN()).orElse(terminalSN));
                        receipt.setCreatedById(Optional.ofNullable(receipt.getCreatedById()).orElse(currentCompanyId));
                    })
                    .map(receiptMapper::toReceiptDto)
                    .forEach(receipt -> rabbitMqProducer.sendWithType(receipt, MqConstants.RECEIPT_QUEUE, "receipt_payload"));
        }
    }

    @Transactional
    public void save(ReceiptDTO receiptDTO, Receipt receipt) {
        if (receipt == null) {
            receipt = receiptRepository.findOneByUid(receiptDTO.getUid()).orElseGet(Receipt::new);
        }
        receiptMapper.update(receipt, receiptDTO);
        if (StringUtils.isNotEmpty(receiptDTO.getTerminalSN())) {
            Optional<Device> terminalOptional = deviceRepository.findFirstBySerialNumberAndType(receiptDTO.getTerminalSN(), DeviceType.FISCAL);
            if (terminalOptional.isPresent()) {
                receipt.setTerminal(terminalOptional.get());
                receipt.setTerminalId(terminalOptional.get().getId());
            }
        }
        Company company = companyRepository.getReferenceById(receipt.getCompanyId());
        if (Source.ONE_C.equals(receipt.getSource())) {
            rabbitMqProducer.send(receipt.getCompanyId(), MqConstants.ONE_C_RECEIPT_QUEUE);
        }
        receiptRepository.save(receipt);
        final Receipt finalReceipt = receipt;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                receiptAfterCommit(finalReceipt, receiptDTO, company);
            }
        });
    }

    public void receiptAfterCommit(Receipt receipt, ReceiptDTO receiptDTO, Company company) {
        sendToReceiptKafkaQueue(receipt);
        sendToSalesFromReceiptQueue(receipt);
        sendSms(receipt, company, receipt.getCustomerPhone(), receiptDTO.getLanguage());
        sendHomeService(receipt);
        sendToPushkassa(receiptDTO);
        sendNonFiscalToTelegram(receipt);
        sendWrongPaymentsToTelegram(receipt);
    }

    public void sendWrongPaymentsToTelegram(Receipt receipt) {
        StringBuilder payments = new StringBuilder();
        if (receipt.getTotalCash() != null && !BigDecimal.ZERO.equals(receipt.getTotalCash()) && receipt.getPayments() != null && receipt.getPayments().containsKey(PaymentType.CASH) && !receipt.getTotalCash().equals(receipt.getPayments().get(PaymentType.CASH))) {
            payments.append("<b>CASH</b>: ").append(receipt.getTotalCash()).append("//").append(receipt.getPayments().get(PaymentType.CASH)).append("\n");
        }
        if (receipt.getTotalCard() != null && !BigDecimal.ZERO.equals(receipt.getTotalCard()) && receipt.getPayments() != null && receipt.getPayments().containsKey(PaymentType.CARD) && !receipt.getTotalCard().equals(receipt.getPayments().get(PaymentType.CARD))) {
            payments.append("<b>CARD</b>: ").append(receipt.getTotalCard()).append("//").append(receipt.getPayments().get(PaymentType.CARD)).append("\n");
        }
        if (receipt.getTotalDiscount() != null && !BigDecimal.ZERO.equals(receipt.getTotalDiscount()) && receipt.getPayments() != null && receipt.getPayments().containsKey(PaymentType.DISCOUNT) && !receipt.getTotalDiscount().equals(receipt.getPayments().get(PaymentType.DISCOUNT))) {
            payments.append("<b>DISCOUNT</b>: ").append(receipt.getTotalDiscount()).append("//").append(receipt.getPayments().get(PaymentType.DISCOUNT)).append("\n");
        }
        if (receipt.getTotalNds() != null && !BigDecimal.ZERO.equals(receipt.getTotalNds()) && receipt.getPayments() != null && receipt.getPayments().containsKey(PaymentType.NDS) && !receipt.getTotalNds().equals(receipt.getPayments().get(PaymentType.NDS))) {
            payments.append("<b>VAT</b>: ").append(receipt.getTotalNds()).append("//").append(receipt.getPayments().get(PaymentType.NDS)).append("\n");
        }
        if (!payments.isEmpty()) {
            payments.append("<b>UID</b>: ").append(receipt.getUid()).append("\n");
            payments.append("<b>BRANCH_ID</b>: ").append(receipt.getBranchId()).append("\n");
            payments.append("<b>COMPANY_ID</b>: ").append(receipt.getCompanyId()).append("\n");
            payments.append("<b>VERSION</b>: ").append(Optional.ofNullable(receipt.getAdditional()).map(Additional::getTerminalVersion).orElse("")).append("\n");
            payments.append("<b>TERMINAL</b>: ").append(Optional.ofNullable(receipt.getTerminal()).map(Device::getSerialNumber).orElse("")).append("\n");
            payments.append("<b>SOURCE</b>: ").append(receipt.getSource()).append("\n");
            String payload = "<b>Type</b>: #WrongPayments\n" + payments;
            rabbitMqProducer.send(payload, MqConstants.RECEIPT_WRONG_PAYMENTS_QUEUE);
        }
    }

    public void sendToReceiptKafkaQueue(Receipt receipt) {
        if (ReceiptStatus.DRAFT != receipt.getStatus()) {
            rabbitMqProducer.send(new Payload(receipt.getId()), MqConstants.RECEIPT_KAFKA_QUEUE);
        }
    }

    public void sendToSalesFromReceiptQueue(Receipt receipt) {
        if (ReceiptStatus.DRAFT != receipt.getStatus()) {
            rabbitMqProducer.send(receipt.getUid(), MqConstants.SALES_FROM_RECEIPT_QUEUE);
        }
    }

    public void sendHomeService(final Receipt receipt) {
        final String homeServiceAccount = Optional.ofNullable(receipt.getAdditional())
                .map(Additional::getHomeServiceAccount)
                .filter(StringUtils::isNotBlank)
                .orElse(null);
        if (receipt.getStatus() == ReceiptStatus.PAID && homeServiceAccount != null) {
            rabbitMqProducer.send(PaymentPayload.builder()
                    .accountCode(homeServiceAccount)
                    .receiptUid(receipt.getUid())
                    .companyId(receipt.getCompanyId())
                    .amount(receipt.getTotalPaid())
                    .build(), MqConstants.CREATE_PAYMENT);
        }
    }

    public void sendToPushkassa(ReceiptDTO receiptDTO) {
        if (!receiptDTO.isForceToPrint()) return;
        if (EnumSet.of(ReceiptStatus.DRAFT, ReceiptStatus.ADVANCE, ReceiptStatus.CREDIT).contains(receiptDTO.getStatus())) {
            notificationService.sendNotification(receiptDTO, "smartpos", "receipt", receiptDTO.getTerminalSN());
        }
    }

    public void sendNonFiscalToTelegram(Receipt receipt) {
        if (EnumSet.of(ReceiptStatus.PAID, ReceiptStatus.RETURNED).contains(receipt.getStatus())) {
            if (!receipt.isFiscal()) {
                rabbitMqProducer.send(receipt.getId(), MqConstants.NON_FISCAL_RECEIPT_QUEUE);
            }
        }
    }


    @Transactional
    public void sendReceiptToKafka(Long receiptId) {
        Receipt receipt = receiptRepository.getReferenceById(receiptId);
        clickhouseService.sendReceiptAndReceiptDetailToKafka(receipt);
    }


    @Transactional
    public void createReceiptFromSales(String uid) {
        Sales sales = salesRepository.findFirstByUid(uid).orElseThrow(() -> new EntityNotFound("Sales not found"));
        Optional<Receipt> receiptOptional = receiptRepository.findOneByUid(uid);
        if (receiptOptional.isPresent()) {
            throw new BadRequestException("Receipt with this uid already exists");
        }
        Receipt receipt = salesMapper.toReceiptFromSales(sales);
        receiptRepository.save(receipt);
    }

    @Transactional
    public String sendReceipt(ReceiptDTO receiptDTO) {
        checkPayments(receiptDTO);
        final Long companyId = getCurrentCompanyId();
        var terminal = deviceRepository.findFirstBySerialNumberAndType(receiptDTO.getTerminalSN(), DeviceType.FISCAL)
                .orElseThrow(notFound(localizationService.getMessage("terminal")));
        if (terminal.getBranch() != null && companyId.equals(terminal.getBranch().getCompanyId())) {
            badRequest(localizationService.getMessage("kkm.number.not.exist")).get();
        }
        Long branchId = terminal.getBranchId();
        Set<String> mxiks = receiptDTO.getReceiptDetails().stream().map(ReceiptDetailDTO::getVatBarcode)
                .filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        Map<String, Mxik> mxikMap = mxikRepository.findAllByMxikIn(mxiks).stream().collect(Collectors.toMap(Mxik::getMxik, Function.identity()));
        List<Vat> vatList = vatRepository.findAll();
        receiptDTO.getReceiptDetails().forEach(receiptDetailDTO -> {
            String vatBarcode = receiptDetailDTO.getVatBarcode();
            if (vatBarcode == null || !mxikMap.containsKey(vatBarcode)) {
                throw new BadRequestException("Вы должны отправлять tasnif «mxik» в каждом продукте: " + vatBarcode);
            }
            if (receiptDetailDTO.getNdsPercent() != null) {
                if (vatList.stream().noneMatch(vat -> vat.getPercent().compareTo(receiptDetailDTO.getNdsPercent()) == 0)) {
                    throw new BadRequestException("Вам необходимо отправить правильную ставку НДС: " + receiptDetailDTO.getNdsPercent());
                }
            }
            receiptDetailDTO.setVatBarcode(vatBarcode);
            ExtraDetail extraDetail = Optional.ofNullable(receiptDetailDTO.getExtraDetail()).orElseGet(ExtraDetail::new);
            MxikPackage defaultPackage = mxikMap.get(vatBarcode).getDefaultPackage();
            extraDetail.setPackageCode(Optional.ofNullable(defaultPackage).map(MxikPackage::getCode).map(String::valueOf).orElse(null));
            extraDetail.setPackageName(Optional.ofNullable(defaultPackage).map(MxikPackage::getNameRu).orElse(null));
            receiptDetailDTO.setExtraDetail(extraDetail);
        });
        final Long userId = receiptDTO.getUserId() != null ? receiptDTO.getUserId() : getCurrentUserId();
        if (StringUtils.length(receiptDTO.getUid()) >= 24 && StringUtils.length(receiptDTO.getUid()) <= 40) {
            Optional<Receipt> optionalReceipt = receiptRepository.findOneByUid(receiptDTO.getUid());
            if (optionalReceipt.isPresent()) {
                throw new BadRequestException("Чек с таким uid уже существует");
            }
        } else {
            receiptDTO.setUid(userId.toString() + System.currentTimeMillis());
        }
        receiptDTO.setStatus(ReceiptStatus.DRAFT);
        receiptDTO.setBranchId(branchId);
        receiptDTO.setCompanyId(companyId);
        receiptDTO.setUserId(userId);
        if (receiptDTO.getUser() == null) {
            receiptDTO.setUser(new UserDTO(userId));
        }
        save(receiptDTO, null);
        return receiptDTO.getUid();
    }

    private void checkPayments(ReceiptDTO receiptDTO) {
        if (receiptDTO.getTotalCash() != null && !BigDecimal.ZERO.equals(receiptDTO.getTotalCash()) && receiptDTO.getPayments() != null && receiptDTO.getPayments().containsKey(PaymentType.CASH) && !receiptDTO.getTotalCash().equals(receiptDTO.getPayments().get(PaymentType.CASH))) {
            throw new BadRequestException("Сумма в поле «totalCash» и поле «payments» «CASH» различаются.");
        }
        if (receiptDTO.getTotalCard() != null && !BigDecimal.ZERO.equals(receiptDTO.getTotalCard()) && receiptDTO.getPayments() != null && receiptDTO.getPayments().containsKey(PaymentType.CARD) && !receiptDTO.getTotalCard().equals(receiptDTO.getPayments().get(PaymentType.CARD))) {
            throw new BadRequestException("Сумма в поле «totalCard» и поле «payments» «CARD» различаются.");
        }
        if (receiptDTO.getTotalDiscount() != null && !BigDecimal.ZERO.equals(receiptDTO.getTotalDiscount()) && receiptDTO.getPayments() != null && receiptDTO.getPayments().containsKey(PaymentType.DISCOUNT) && !receiptDTO.getTotalDiscount().equals(receiptDTO.getPayments().get(PaymentType.DISCOUNT))) {
            throw new BadRequestException("Сумма в поле «totalDiscount» и поле «payments» «DISCOUNT» различаются.");
        }
        if (receiptDTO.getTotalExcise() != null && !BigDecimal.ZERO.equals(receiptDTO.getTotalExcise()) && receiptDTO.getPayments() != null && receiptDTO.getPayments().containsKey(PaymentType.EXCISE) && !receiptDTO.getTotalExcise().equals(receiptDTO.getPayments().get(PaymentType.EXCISE))) {
            throw new BadRequestException("Сумма в поле «totalExcise» и поле «payments» «EXCISE» различаются.");
        }
        if (receiptDTO.getTotalNds() != null && !BigDecimal.ZERO.equals(receiptDTO.getTotalNds()) && receiptDTO.getPayments() != null && receiptDTO.getPayments().containsKey(PaymentType.NDS) && !receiptDTO.getTotalNds().equals(receiptDTO.getPayments().get(PaymentType.NDS))) {
            throw new BadRequestException("Сумма в поле «totalNds» и поле «payments» «NDS» различаются.");
        }
    }


    public Page<ReceiptDTO> findAllDraftsByBranch(ReceiptFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        return receiptRepository.findAllDrafts(filter)
                .map(Receipt::toListDto);
    }

    public List<ReceiptDTO> findAllDrafts(String serialNumber) {
        var terminal = deviceRepository.findFirstBySerialNumberAndType(serialNumber, DeviceType.FISCAL)
                .orElseThrow(notFound(localizationService.getMessage("terminal")));
        if (!terminal.getCompanyId().equals(getCurrentCompanyId())) {
            return new ArrayList<>();
        }
        return receiptRepository.findAllByTerminalIdAndStatusAndFacturaIdNull(terminal.getId(), ReceiptStatus.DRAFT)
                .stream()
                .map(receipt -> receipt.getDTO(true, false))
                .collect(Collectors.toList());
    }

    public List<ReceiptDTO> findAllDrafts() {
        return findAllDrafts(getCurrentTerminalSnByHeader());
    }


    @Transactional
    public void delete(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("receipt")));
        checkForAccessToCompany(receipt.getCompanyId());
        if (!ReceiptStatus.DRAFT.equals(receipt.getStatus())) {
            throw badRequest(localizationService.getMessage("receipt.cannot.delete")).get();
        }
        receiptRepository.deleteById(id);
    }

    @Transactional
    public void deleteByUid(String uid) {
        Receipt receipt = receiptRepository.findOneByUid(uid)
                .orElseThrow(notFound(localizationService.getMessage("receipt"), 400));
        if (!ReceiptStatus.DRAFT.equals(receipt.getStatus())) {
            throw badRequest(localizationService.getMessage("receipt.cannot.delete")).get();
        }
        if (!receipt.getCompanyId().equals(getCurrentCompanyId())) {
            throw badRequest(localizationService.getMessage("company.different")).get();
        }
        final Long id = receipt.getId();
        receiptRepository.deleteById(id);
    }


    public boolean checkMarking(ReceiptCheckMarkingDTO dto) {
        String code = MarkingUtils.clearWithProductGroup(dto.getCode(), null);
        for (String marking : dto.getMarkings()) {
            if (marking.contains(code)) return true;
        }
        return false;
    }

    public Page<CreditAdvanceReceiptDTO> findAllCreditAdvanceReceipts(CreditAdvanceReceiptFilter filter) {
        if (!isAdmin() || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        return clickhouseReceiptGenericRepository.findAllCreditAdvanceReceipts(filter);
    }

    public void sendReceiptDetailViaSms(ReceiptSmsRequestDTO request) {
        Receipt receipt;
        if (request.getReceiptId() != null) {
            receipt = receiptRepository.findById(request.getReceiptId())
                    .orElseThrow(notFound(localizationService.getMessage("receipt")));
        } else if (StringUtils.isNotBlank(request.getUid())) {
            receipt = receiptRepository.findOneByUid(request.getUid())
                    .orElseThrow(notFound(localizationService.getMessage("receipt")));
        } else if (StringUtils.isNotBlank(request.getFiscalUrl())) {
            receipt = clickhouseReceiptGenericRepository.findFirstByFiscalUrl(request.getFiscalUrl())
                    .map(dto -> receiptRepository.findById(dto.getId()).orElseThrow(notFound(localizationService.getMessage("receipt"))))
                    .orElseThrow(notFound(localizationService.getMessage("receipt")));
        } else {
            throw new BadRequestException("Идентификатор или UID является обязательным");
        }
        if (StringUtils.isBlank(receipt.getFiscalUrl())) {
            throw new BadRequestException("Чек не фискальный");
        }
        Company company = receipt.getCompany();
        if (company == null) {
            throw new BadRequestException(localizationService.getMessage("company.not.found"));
        }
        sendSms(receipt, company, request.getPhone(), request.getLanguage());
    }

    public void sendSms(final Receipt receipt, final Company company, final String phone, final String language) {
        if (List.of(ReceiptStatus.PAID, ReceiptStatus.RETURNED).contains(receipt.getStatus()) || !receipt.isSendSms() || !EncryptionUtils.checkLogin(receipt.getCustomerPhone())) {
            return;
        }

        if (!company.isFreeTrial() && company.getBalances().getOrDefault(ServiceType.SMS, 0) <= 0 && company.getBalances().getOrDefault(ServiceType.ADDITIONAL_SMS, 0) <= 0) {
            return;
        }
        try {
            StringBuilder content = new StringBuilder();
            boolean equals = "uz".equals(language);
            String receiptType = (receipt.getStatus() == ReceiptStatus.PAID)
                    ? (equals ? " \nTo'lov: " : " \nOplata: ")
                    : (equals ? " \nQaytarish: " : " \nVozvrat: ");
            String currency = equals ? " so'm \n" : " sum \n";
            content.append(company.getName()).append(receiptType);
            content.append(receipt.getTotalPaid().setScale(2, RoundingMode.HALF_UP)).append(currency);
            content.append("https://internal.example.com/").append(Base64.getEncoder().encodeToString(receipt.getUid().getBytes()));
            etcSmsService.sendSmsForReceipt(new EtcRequestDto(phone, content.toString()));
            if (company.isFreeTrial() || company.getBalances().getOrDefault(ServiceType.SMS, 0) > 0) {
                company.updateBalance(ServiceType.SMS, -1);
            } else {
                company.updateBalance(ServiceType.ADDITIONAL_SMS, -1);
            }
            companyRepository.save(company);
        } catch (Exception e) {
            log.error("Send receipt via sms: {}", e.getMessage());
        }
    }


    public List<EpsServicesLookupDto> paymentProviders(Long branchId) {
        if (branchId == null) {
            branchId = getCurrentBranchIdByHeader() == null ? getCurrentBranchId() : Long.valueOf(getCurrentBranchIdByHeader());
        }
        Branch branch = branchRepository.findById(branchId).orElseThrow(notFound(localizationService.getMessage("branch")));
        return apayClient.paymentProviders(tokenStoreService.getToken(TokenType.APAY), branch.getCtoId(), String.valueOf(branch.getId()), MerchantReferenceType.SMARTPOS.name());
    }

    public void deleteDraftReceipts(Long branchId) {
        Page<Receipt> receiptPage = receiptRepository.findByBranchIdAndStatus(branchId, ReceiptStatus.DRAFT, PageRequest.of(0, 100));
        while (!receiptPage.isEmpty()) {
            receiptRepository.deleteAll(receiptPage);
            receiptPage = receiptRepository.findByBranchIdAndStatus(branchId, ReceiptStatus.DRAFT, PageRequest.of(0, 100));
        }
    }

    public Integer getAdvanceOrderFromOriginUid(String originUid) {
        return clickhouseReceiptGenericRepository.getAdvanceOrderFromOriginUid(originUid);
    }

    @Transactional
    public void cleanup() {
        int page = 0;
        List<Receipt> receiptList;
        do {
            log.info("==================================================================================");
            log.info("==================================================================================");
            receiptList = receiptRepository.findFirst1000ByStatusAndCreatedDateTimeLessThan(ReceiptStatus.DRAFT, LocalDateTime.now().minusMonths(1));
            log.info("Receipt cleanup started. Page: {}, {}", page, CollectionUtils.size(receiptList));
            page++;
            try {
                receiptRepository.deleteAll(receiptList);
            } catch (Exception e) {
                log.error("Receipt cleanup failed. Page: {}, {}", page, e.getMessage());
            }
            receiptList = receiptRepository.findFirst1000ByStatusAndCreatedDateTimeLessThan(ReceiptStatus.DRAFT, LocalDateTime.now().minusMonths(1));
            log.info("Receipt cleanup finished. Page: {}, {}", page, CollectionUtils.size(receiptList));
            log.info("==================================================================================");
            log.info("==================================================================================");
        } while (CollectionUtils.isNotEmpty(receiptList));
    }

    public ReceiptDTO getByOriginUidMobile(String originUID) {
        Optional<ReceiptDTO> receipt = clickhouseReceiptGenericRepository.findFirstByOriginUid(originUID);
        if (receipt.isEmpty()) {
            throw notFound(localizationService.getMessage("receipt"), 400).get();
        }
        if (!receipt.get().getCompanyId().equals(getCurrentCompanyId())) {
            throw badRequest(localizationService.getMessage("receipt.not.belong.organization")).get();
        }
        return receipt.get();
    }


    public ReceiptDTO getOriginByFiscalUrl(String fiscalUrl) {
        fiscalUrl = fiscalUrl.replaceAll("\"", "");
        Optional<Pair<ReceiptStatus, String>> originUid = clickhouseReceiptGenericRepository.findUidByFiscalUrl(fiscalUrl);
        if (originUid.isEmpty()) {
            throw notFound(localizationService.getMessage("receipt"), 400).get();
        }
        if (originUid.get().getFirst() == ReceiptStatus.RETURNED) {
            throw badRequest(localizationService.getMessage("receipt.return.loop")).get();
        }
        return getByOriginUidMobile(originUid.get().getSecond());
    }

    public List<ReceiptDTO> findAllReceiptsDetailByFacturaId(String facturaId) {
        List<Receipt> receipts = receiptRepository.findAllByFacturaId(facturaId);
        return receipts.stream().map(receipt -> receipt.getDTO(true, false)).toList();
    }

    public List<TransitReceiptDTO> findAllReceiptsByFacturaId(String facturaId) {
        return receiptRepository.findAllByFacturaId(facturaId)
                .stream()
                .map(receiptMapper::toTransitReceiptDto)
                .toList();
    }

    @Transactional
    public String receiptReturn(@Valid ReceiptReturnDto dto, String terminalSN) {
        Receipt receipt = receiptRepository.findOneByUid(dto.uid())
                .orElseThrow(notFound(localizationService.getMessage("receipt")));
        if (receipt.getStatus() != ReceiptStatus.PAID) {
            throw new BadRequestException("Чек не оплачен");
        }
        var clone = receipt.clone();
        clone.setUid(StringUtils.join(System.currentTimeMillis(), getCurrentUserId()));
        clone.setReceiptDateTime(LocalDateTime.now());
        clone.setOriginUid(dto.uid());
        clone.setStatus(ReceiptStatus.DRAFT);
        if (StringUtils.isNotBlank(terminalSN)) {
            deviceRepository.findFirstBySerialNumberAndType(terminalSN, DeviceType.FISCAL)
                    .ifPresentOrElse(
                            terminal -> {
                                clone.setTerminalId(terminal.getId());
                                clone.setTerminal(terminal);
                            },
                            () -> {
                                throw notFound(localizationService.getMessage("terminal")).get();
                            }
                    );
        }
        receiptRepository.save(clone);
        ReceiptDTO receiptDTO = clone.getDTO(true, false);
        notificationService.sendNotification(receiptDTO, "smartpos", "receipt", receiptDTO.getTerminalSN());
        return clone.getUid();
    }

    public CheckReturnedReceiptDto getReceiptReturnStatus(String uid) {
        Optional<CheckReturnedReceiptDto> dto = clickhouseReceiptGenericRepository.checkReceiptByOriginUid(uid, List.of(ReceiptStatus.RETURNED));
        if (dto.isEmpty() || dto.get().status() != ReceiptStatus.RETURNED) {
            throw new BadRequestException("Чек не найден или не был возвращен");
        }
        return dto.get();
    }

    public void sendReceipt(String uid) {
        ReceiptDTO receiptDTO = receiptRepository.findOneByUid(uid)
                .map(receipt -> receipt.getDTO(true, false))
                .orElseThrow(notFound(localizationService.getMessage("receipt")));
        if (receiptDTO.getStatus().isFiscalized()) {
            throw new BadRequestException("Чек уже фискализирован");
        }
        if (receiptDTO.getTerminalSN() == null) {
            var terminal = deviceRepository.findFirstByBranchIdAndType(receiptDTO.getBranchId(), DeviceType.FISCAL)
                    .orElseThrow(notFound(localizationService.getMessage("terminal")));
            receiptDTO.setTerminalSN(terminal.getSerialNumber());
        }
        notificationService.sendNotification(receiptDTO, "smartpos", "receipt", receiptDTO.getTerminalSN());
    }

    public LastReceiptDto getLastReceipt(String serialNumber, String tin, PaymentType paymentType) {
        return clickhouseReceiptGenericRepository.getLastReceipt(serialNumber, tin, paymentType).orElse(null);
    }
}
