package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.Bill;
import uz.uzkassa.horeca.domain.Terminal;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.dto.terminal.*;
import uz.uzkassa.horeca.enums.terminal.BillStatus;
import uz.uzkassa.horeca.integration.ApayClient;
import uz.uzkassa.horeca.repository.BillRepository;
import uz.uzkassa.horeca.repository.OrderRepository;
import uz.uzkassa.horeca.repository.TerminalRepository;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.web.rest.errors.ApayIntegrationException;

import java.math.BigDecimal;
import java.util.*;

import static uz.uzkassa.horeca.utils.HeaderUtils.getCurrentBranchIdByHeader;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BillService extends BaseService {

    ApayClient apayClient;
    TerminalRepository terminalRepository;
    BillRepository billRepository;
    OrderRepository orderRepository;

    @Transactional
    public ApayCreateBillResponse create(ApayCreateBillRequest request) {
        if (BigDecimal.valueOf(100).compareTo(request.getAmount()) > 0) {
            throw badRequestExceptionThrow("Минимальная сумма оплаты 100.00").get();
        }
        Long branchId = Optional.ofNullable(request.getBranchId()).orElse(HeaderUtils.getCurrentBranchId());
        if (branchId == null) {
            branchId = Optional.ofNullable(getCurrentBranchIdByHeader()).orElseGet(BaseService::getCurrentBranchId);
        }
        if (branchId == null) {
            Terminal terminal = terminalRepository.findFirstBySerialNumberAndDeletedIsFalse(HeaderUtils.getCurrentTerminalSnByHeader()).orElse(null);
            if (terminal != null) {
                branchId = terminal.getBranchId();
            }
        }
        if (branchId == null) {
            throw badRequestExceptionThrow("Branch is required").get();
        }
        Branch branch = branchRepository.findById(branchId).orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", branchId));
        request.setCtoId(branch.getCtsId());
        try {
            log.info("request: {}", objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.error("Writing object error: {}", e.getMessage());
        }
        try {
            ResponseEntity<ApayCreateBillResponse> response = apayClient.createBill(request);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                try {
                    throw new ApayIntegrationException(objectMapper.writeValueAsString(response.getBody()));
                } catch (JsonProcessingException e) {
                    log.info("Writing object error");
                }
            }
        } catch (Exception e) {
            log.error(" ERROR: {}", e.getMessage());
            throw new ApayIntegrationException(e.getMessage());
        }
        throw new ApayIntegrationException("Apay error");
    }

    @Transactional
    public ApayUpdateBillResponse update(ApayUpdateBillRequest request) {
        try {
            log.info("request: {}", objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.error("Writing object error: {}", e.getMessage());
        }
        try {
            ResponseEntity<ApayUpdateBillResponse> response = apayClient.updateBill(request);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                try {
                    throw new ApayIntegrationException(objectMapper.writeValueAsString(response.getBody()));
                } catch (JsonProcessingException e) {
                    log.info("Writing object error");
                }
            }
        } catch (Exception e) {
            log.error(" ERROR: {}", e.getMessage());
            throw new ApayIntegrationException(e.getMessage());
        }
        throw new ApayIntegrationException("Apay error");
    }

    @Transactional
    public List<ApayCardSimpleResponse> getUserCards(String nfc, String pin, String billId) {
        log.info("nfs: {}, pin: {}, billId: {}", nfc, pin, billId);
        try {
            ResponseEntity<List<ApayCardSimpleResponse>> response = apayClient.getUserCards(nfc, pin, billId);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                try {
                    throw new ApayIntegrationException(objectMapper.writeValueAsString(response.getBody()));
                } catch (JsonProcessingException e) {
                    log.info("Writing object error");
                }
            }
        } catch (Exception e) {
            log.error(" ERROR: {}", e.getMessage());
            throw new ApayIntegrationException(e.getMessage());
        }
        throw new ApayIntegrationException("Apay error");
    }

    @Transactional
    public ApayPayBillResponse payBill(ApayPayBillRequest request) {
        try {
            log.info("request: {}", objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.error("Writing object error: {}", e.getMessage());
        }
        try {
            ResponseEntity<ApayPayBillResponse> response = apayClient.payBill(request);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                try {
                    throw new ApayIntegrationException(objectMapper.writeValueAsString(response.getBody()));
                } catch (JsonProcessingException e) {
                    log.info("Writing object error");
                }
            }
        } catch (Exception e) {
            log.error(" ERROR: {}", e.getMessage());
            throw new ApayIntegrationException(e.getMessage());
        }
        throw new ApayIntegrationException("Apay error");
    }

    public List<BillCheckStatusResponseDTO> checkStatus(Long orderId) {
        List<BillCheckStatusResponseDTO> responseDTOList = new ArrayList<>();
        orderRepository.findById(orderId).map(order -> {
                    for (Bill bill : order.getBills()) {
                        BillCheckStatusResponseDTO billCheckStatusResponseDTO = this.checkStatus(bill.getBillId());
                        responseDTOList.add(billCheckStatusResponseDTO);
                    }
                    return order;
                })
                .orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", orderId));
        return responseDTOList;
    }

    public void updateStatus(String billId) {
        billRepository.findFirstByBillId(billId).map(bill -> {
                    try {
                        apayClient.cancelBill(billId);
                        bill.setStatus(BillStatus.CANCELED);
                        billRepository.save(bill);
                    } catch (Exception e) {
                        throw new ApayIntegrationException(e.getMessage());
                    }
                    return bill;
                })
                .orElseThrow(notFoundExceptionThrow(Bill.class.getSimpleName(), "id", billId));
    }

    public void updateStatus(Bill bill) {
        try {
            apayClient.cancelBill(bill.getBillId());
            bill.setStatus(BillStatus.CANCELED);
            billRepository.save(bill);
        } catch (Exception e) {
            log.info("ERROR: {}", e.getMessage());
        }
    }

    public BillCheckStatusResponseDTO checkStatus(String id) {
        return billRepository.findFirstByBillId(id).map(bill -> {
            try {
                log.info("================ Bill Check status. BILL_ID: {} ================", objectMapper.writeValueAsString(id));
            } catch (JsonProcessingException e) {
                log.error("Writing object error: {}", e.getMessage());
            }
            try {
                ResponseEntity<BillCheckStatusResponseDTO> response = apayClient.checkBillStatus(new BillIdRequestDTO(id));
                if (response.getStatusCode().is2xxSuccessful()) {
                    if (response.getBody() != null && StringUtils.isNotEmpty(response.getBody().getCode())) {
                        bill.setStatus(BillStatus.valueOf(response.getBody().getCode()));
                        response.getBody().setAmount(bill.getAmount());
                        billRepository.save(bill);
                    }
                    return response.getBody();
                } else {
                    try {
                        throw new ApayIntegrationException(objectMapper.writeValueAsString(response.getBody()));
                    } catch (JsonProcessingException e) {
                        log.info("Writing object error");
                    }
                }
            } catch (Exception e) {
                log.error(" ERROR: {}", e.getMessage());
                throw new ApayIntegrationException(e.getMessage());
            }
            throw new ApayIntegrationException("Apay error");
        }).orElseThrow(notFoundExceptionThrow(Bill.class.getSimpleName(), "billId", id));
    }

    public BillCheckStatusResponseDTO checkStatus(BillIdRequestDTO billIdRequestDTO) {
        try {
            log.info("request: {}", objectMapper.writeValueAsString(billIdRequestDTO));
        } catch (JsonProcessingException e) {
            log.error("Writing object error: {}", e.getMessage());
        }
        try {
            ResponseEntity<BillCheckStatusResponseDTO> response = apayClient.checkBillStatus(billIdRequestDTO);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                try {
                    throw new ApayIntegrationException(objectMapper.writeValueAsString(response.getBody()));
                } catch (JsonProcessingException e) {
                    log.info("Writing object error");
                }
            }
        } catch (Exception e) {
            log.error(" ERROR: {}", e.getMessage());
            throw new ApayIntegrationException(e.getMessage());
        }
        throw new ApayIntegrationException("Apay error");
    }

    public ApayBankLogoDTO getCardById(String pan) {
        log.info("pan: {}", pan);
        try {
            ResponseEntity<ApayBankLogoDTO> response = apayClient.getCardById(pan);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                try {
                    throw new ApayIntegrationException(objectMapper.writeValueAsString(response.getBody()));
                } catch (JsonProcessingException e) {
                    log.info("Writing object error");
                }
            }
        } catch (Exception e) {
            log.error(" ERROR: {}", e.getMessage());
            throw new ApayIntegrationException(e.getMessage());
        }
        throw new ApayIntegrationException("Apay error");
    }

    public AnonymousPaymentDTO getAnonymousPayment(String billId) {
        log.info("pan: {}", billId);

        try {
            ResponseEntity<AnonymousPaymentDTO> response = apayClient.anonymousPayment(billId);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                try {
                    throw new ApayIntegrationException(objectMapper.writeValueAsString(response.getBody()));
                } catch (JsonProcessingException e) {
                    log.info("Writing object error");
                }
            }
        } catch (Exception e) {
            log.error(" ERROR: {}", e.getMessage());
            throw new ApayIntegrationException(e.getMessage());
        }
        throw new ApayIntegrationException("Apay error");
    }

    @Transactional
    public ApayCreateBillResponse createForOrder(Long orderId, ApayCreateBillRequest apayCreateBillRequest) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    if (!CollectionUtils.isEmpty(order.getBills())) {
                        order.getBills().stream().filter(bill -> BillStatus.PENDING.equals(bill.getStatus())).forEach(this::updateStatus);
                    }
                    String terminalSn = HeaderUtils.getCurrentTerminalSnByHeader();
                    if (apayCreateBillRequest.getBranchId() == null) {
                        apayCreateBillRequest.setBranchId(order.getBranchId());
                        apayCreateBillRequest.setCtoId(order.getBranch().getCtsId());
                    }
                    Terminal terminal = null;
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(terminalSn)) {
                        terminal = terminalRepository.findFirstBySerialNumberAndDeletedIsFalse(terminalSn).orElse(null);
                    }
                    Map<String, String> properties = new LinkedHashMap<>();
                    properties.put("callbackUrl", applicationProperties.getServerUrl() + applicationProperties.getApayConfig().getCallBackUrl());
                    properties.put("clientType", "HORECA");
                    apayCreateBillRequest.setProperties(properties);
                    ApayCreateBillResponse response = this.create(apayCreateBillRequest);
                    if (response != null && org.apache.commons.lang3.StringUtils.isNotEmpty(response.getId())) {
                        Bill bill = new Bill();
                        bill.setBillId(response.getId());
                        bill.setEpsQrCode(response.getEpsQrCode());
                        bill.setAmount(apayCreateBillRequest.getAmount());
                        bill.setTerminalId(terminal != null ? terminal.getId() : null);
                        bill.setOrderId(order.getId());
                        billRepository.save(bill);
                    }
                    return response;
                })
                .orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", orderId));
    }

    @Transactional
    public void updateBillStatus(ApayUpdateBillStatusRequest request) {
        final Bill bill = billRepository.findFirstByBillId(request.getBillId())
                .orElseThrow(notFoundExceptionThrow(Bill.class.getSimpleName(), "billId", request.getBillId()));
        bill.setStatus(BillStatus.valueOf(request.getStatus()));
        billRepository.save(bill);
    }
}
