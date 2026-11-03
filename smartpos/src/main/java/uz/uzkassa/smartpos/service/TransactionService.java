package uz.uzkassa.smartpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uz.uzkassa.smartpos.domain.postgres.sales.Transaction;
import uz.uzkassa.smartpos.domain.postgres.sales.Transaction_;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.dto.ResponseData;
import uz.uzkassa.smartpos.dto.sales.*;
import uz.uzkassa.smartpos.dto.terminal.sales.*;
import uz.uzkassa.smartpos.enums.*;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.filter.TransactionFilter;
import uz.uzkassa.smartpos.integration.RailwaysClient;
import uz.uzkassa.smartpos.mappers.TransactionMapper;
import uz.uzkassa.smartpos.repository.postgres.sales.TransactionRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService extends SmartposService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final DeviceRepository deviceRepository;
    private final PushkassaNotificationService pushkassaNotificationService;
    private final ObjectMapper objectMapper;
    private final RailwaysClient railwaysClient;

    @Transactional
    public ResponseData<?> createPaymentForRailways(TransactionRailwaysPaymentDto paymentDto) {
        if (!PaymentType.m1Types().contains(paymentDto.getPaymentType())) {
            throw new BadRequestException("Неверный тип платежа");
        }
        if (paymentDto.getOperation() == TransactionOperation.REVERSAL && StringUtils.isBlank(paymentDto.getOriginUid())) {
            throw new BadRequestException(localizationService.getMessage("should.send.origin.uid.in.reversal"));
        }
        Optional<Device> m1 = deviceRepository.findFirstBySerialNumber(paymentDto.getTerminalId());
        if (m1.isEmpty()) {
            throw new BadRequestException("Устройство не найдено");
        }
        transactionRepository.findByUid(paymentDto.getUid())
                .ifPresent(transaction -> {
                    throw new BadRequestException(localizationService.getMessage("transaction.exist.with.this.uid"));
                });
        Transaction transaction = new Transaction();
        transaction.setUid(paymentDto.getUid());
        transaction.setOriginUid(paymentDto.getOriginUid());
        transaction.setDeviceSN(m1.get().getSerialNumber());
        transaction.setBranchId(m1.get().getBranchId());
        transaction.setCompanyId(getCurrentCompanyId());
        transaction.setUserId(getCurrentUserId());
        transaction.setPaymentType(paymentDto.getPaymentType());
        transaction.setAmount(paymentDto.getAmount());
        transaction.setCurrency("UZS");
        transaction.setOperation(paymentDto.getOperation());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setSource(Source.RAILWAYS);
        transactionRepository.save(transaction);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    paymentDto.setUid(transaction.getUid());
                    pushkassaNotificationService.sendNotification(objectMapper.writeValueAsString(paymentDto), "smartpos", "payment", m1.get().getSerialNumber());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return ResponseData.ok();
    }

    @Transactional
    public String createPayment(TransactionCreatePaymentDto paymentDto) {
        if (!PaymentType.m1Types().contains(paymentDto.getPaymentType())) {
            throw new BadRequestException("Неверный тип платежа");
        }
        if (paymentDto.getOperation() == TransactionOperation.REVERSAL && StringUtils.isBlank(paymentDto.getOriginUid())) {
            throw new BadRequestException(localizationService.getMessage("should.send.origin.uid.in.reversal"));
        }
        List<Device> devices = deviceRepository.findAllByBranchIdAndTypeIn(paymentDto.getBranchId(), List.of(DeviceType.M1, DeviceType.TERMINAL));
        Optional<Device> m1 = devices.stream().filter(device -> device.getType() == DeviceType.M1).findFirst();
        if (m1.isEmpty()) {
            m1 = devices.stream().filter(device -> device.getType() == DeviceType.TERMINAL).findFirst();
        }
        if (m1.isEmpty()) {
            throw new BadRequestException("Устройство не найдено");
        }
        Transaction transaction = new Transaction();
        transaction.setUid(Optional.ofNullable(paymentDto.getUid()).orElseGet(() -> getCurrentUserId() + "" + System.currentTimeMillis()));
        transaction.setOriginUid(paymentDto.getOriginUid());
        transaction.setDeviceSN(m1.get().getSerialNumber());
        transaction.setBranchId(paymentDto.getBranchId());
        transaction.setCompanyId(getCurrentCompanyId());
        transaction.setUserId(getCurrentUserId());
        transaction.setPaymentType(paymentDto.getPaymentType());
        transaction.setAmount(paymentDto.getAmount());
        transaction.setCurrency(paymentDto.getCurrency());
        transaction.setOperation(paymentDto.getOperation());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDateTime(LocalDateTime.now());
        transactionRepository.save(transaction);
        Optional<Device> finalM = m1;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    paymentDto.setUid(transaction.getUid());
                    pushkassaNotificationService.sendNotification(objectMapper.writeValueAsString(paymentDto), "smartpos", "payment", finalM.get().getSerialNumber());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return transaction.getUid();
    }


    public void receivedPayment(String uid) {
        pushkassaNotificationService.sendNotification(uid, "smartpos", "payment-received", uid);
    }

    @Transactional
    public void updatePayment(TransactionUpdatePaymentDto paymentDto) {
        Transaction transaction = transactionRepository.findByUid(paymentDto.getUid())
                .orElseThrow(() -> new BadRequestException("Транзакция не найдена"));
        transaction.setStatus(paymentDto.getStatus());
        transaction.setInfo(paymentDto.getInfo());
        transactionRepository.save(transaction);
        TransactionWebsocketDto webSocketDto = new TransactionWebsocketDto();
        webSocketDto.setStatus(transaction.getStatus());
        webSocketDto.setInfo(transaction.getInfo());
        webSocketDto.setContent(paymentDto.getContent());
        try {
            pushkassaNotificationService.sendNotification(objectMapper.writeValueAsString(webSocketDto), "smartpos", "payment-status", transaction.getUid());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        sendToRailways(transaction);
    }

    private void sendToRailways(Transaction transaction) {
        if (TransactionStatus.PENDING != transaction.getStatus() && Source.RAILWAYS == transaction.getSource()) {
            try {
                railwaysClient.callback(applicationProperties.getRailways().getPassword(), new TransactionRailwaysPayload(transaction));
            } catch (Exception e) {
                log.error("Railways error", e);
            }
        }
    }

    @Transactional
    public void batchSave(Set<TransactionCreateDto> transactionDtos) {
        List<String> uidList = transactionDtos.stream()
                .map(TransactionCreateDto::getUid)
                .collect(Collectors.toList());

        Map<String, Transaction> uidMap = transactionRepository.findAllByUidIn(uidList)
                .stream()
                .collect(Collectors.toMap(Transaction::getUid, transaction -> transaction));
        List<Transaction> transactions = transactionDtos.stream()
                .map(dto -> {
                    if (dto.getUserId() == null) {
                        dto.setUserId(getCurrentUserId());
                    }
                    if (uidMap.containsKey(dto.getUid())) {
                        Transaction transaction = uidMap.get(dto.getUid());
                        transactionMapper.updateEntity(transaction, dto);
                        return transaction;
                    } else {
                        return transactionMapper.toEntity(dto);
                    }
                })
                .collect(Collectors.toList());

        transactionRepository.saveAll(transactions);
    }

    public Page<TransactionDto> findAllByFilter(TransactionFilter filter) {
        if (!isAdmin()) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return transactionRepository.findAll((Specification<Transaction>) (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (filter.getCompanyId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Transaction_.companyId), filter.getCompanyId()));
            }
            if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
                predicates.add(root.get(Transaction_.branchId).in(filter.getBranchIds()));
            }
            if (StringUtils.isNotBlank(filter.getDeviceSN())) {
                predicates.add(criteriaBuilder.equal(root.get(Transaction_.deviceSN), filter.getDeviceSN()));
            }
            if (filter.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Transaction_.userId), filter.getUserId()));
            }
            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Transaction_.status), filter.getStatus()));
            }
            if (filter.getPaymentType() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Transaction_.paymentType), filter.getPaymentType()));
            }
            if (filter.getOperation() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Transaction_.operation), filter.getOperation()));
            }
            if (filter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Transaction_.dateTime), filter.getFrom()));
            }
            if (filter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Transaction_.dateTime), filter.getTo()));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.desc == filter.getSortOrder()) {
                    query.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                } else {
                    query.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                }
            } else {
                query.orderBy(criteriaBuilder.desc(root.get(Transaction_.id)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(transactionMapper::toDto);
    }

    public TransactionStatisticsDto statistics(TransactionFilter filter) {
        if (!isAdmin()) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return transactionRepository.statistics(filter);
    }

    public List<TransactionDynamicsDto> transactionDynamics(TransactionFilter filter) {
        if (!isAdmin()) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        return transactionRepository.transactionDynamics(filter);
    }

    public List<TransactionAdminStatisticsDto> adminStatistics(TransactionFilter filter) {
        return transactionRepository.adminStatistics(filter);
    }

    public TransactionDto findByUid(String uid) {
        return transactionRepository.findByUid(uid).map(transactionMapper::toDto).orElseThrow(notFound(localizationService.getMessage("transaction")));
    }
}
