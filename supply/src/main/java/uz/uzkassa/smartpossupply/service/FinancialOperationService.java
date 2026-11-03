package uz.uzkassa.smartpossupply.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.FinancialOperation;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.customer.AccountReceivableMapDTO;
import uz.uzkassa.smartpossupply.dto.customer.AccountReceivableReportDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationDTO;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationDetailDTO;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationListDTO;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.FinancialOperationType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.filters.FinancialOperationFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.interfaces.Crud;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.repository.CustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.FinancialOperationRepository;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 09.01.2024
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
@Slf4j
public class FinancialOperationService extends BaseService implements Crud<Long, FinancialOperationDTO, FinancialOperationDetailDTO, FinancialOperationListDTO, FinancialOperationFilter> {

    FinancialOperationRepository financialOperationRepository;
    PartnerRepository partnerRepository;
    CustomerBranchRepository customerBranchRepository;
    NumberingContext numberingContext;

    @Override
    public void validate(FinancialOperationDTO financialOperationDTO) {

        if (FinancialOperationType.CREDIT.equals(financialOperationDTO.getOperationType())) {
            throw validationService.badRequestExceptionThrow("Редактирование не разрешено.", "Внутренняя финансовая операция не может редактироваться").get();
        }
        if (financialOperationDTO.getCustomerId() == null) {
            throw validationService.badRequestExceptionThrow("Требуется Ид клиента.", "Выберите клиента").get();
        }
        if (financialOperationDTO.getPaymentType() == null) {
            throw validationService.badRequestExceptionThrow("Укажите тип платежа.", "Выберите тип платежа").get();
        }
    }

    @Override
    @Transactional
    public Long create(FinancialOperationDTO financialOperationDTO) {
        this.validate(financialOperationDTO);
        return this.createEntity(financialOperationDTO).getId();
    }

    @Transactional
    public Long update(Long id, FinancialOperationDTO financialOperationDTO) {
        return financialOperationRepository.findById(id).map(financialOperation -> {
            financialOperationDTO.setId(id);
            financialOperationDTO.setOperationType(financialOperation.getOperationType());
            this.validate(financialOperationDTO);
            financialOperation.setAmount(financialOperationDTO.getAmount());
            financialOperation.setPaymentType(financialOperationDTO.getPaymentType());
            financialOperation.setDescription(financialOperationDTO.getDescription());
            financialOperation.setStockOutId(financialOperationDTO.getStockOutId());
            financialOperation.setFacturaId(financialOperationDTO.getFacturaId());
            financialOperation.setPaymentReceivedDate(financialOperationDTO.getPaymentReceivedDate());
            financialOperation.setForeignPrice(financialOperationDTO.getForeignPrice());
            financialOperation.setCurrencyType(financialOperationDTO.getCurrencyType());
            financialOperation.setExchangeRate(financialOperationDTO.getExchangeRate());
            financialOperationRepository.save(financialOperation);
            partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(financialOperation.getCompanyId(), financialOperation.getCustomerId(), financialOperation.getCompanyId()).ifPresent(partners -> {
                BigDecimal currentBalance = Optional.ofNullable(partners.getCurrentBalance()).orElse(BigDecimal.ZERO);
                currentBalance = currentBalance.subtract(financialOperationDTO.getOldAmount());
                currentBalance = currentBalance.add(financialOperationDTO.getAmount());
                partners.setCurrentBalance(currentBalance);
                partnerRepository.save(partners);
            });
            if (financialOperation.getCustomerBranchId() != null) {
                customerBranchRepository.findById(financialOperation.getCustomerBranchId()).ifPresent(customerBranch -> {
                    BigDecimal currentBalance = Optional.ofNullable(customerBranch.getCurrentBalance()).orElse(BigDecimal.ZERO);
                    currentBalance = currentBalance.subtract(financialOperationDTO.getOldAmount());
                    if (financialOperationDTO.getCustomerBranchId() != null) {
                        currentBalance = currentBalance.add(financialOperationDTO.getAmount());
                    }
                    customerBranch.setCurrentBalance(currentBalance);
                    customerBranchRepository.save(customerBranch);
                });
            }
            financialOperation.setCustomerBranchId(financialOperationDTO.getCustomerBranchId());
            return financialOperation.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(FinancialOperation.class.getSimpleName(), "id", id));
    }

    public FinancialOperationDetailDTO get(Long id) {
        return financialOperationRepository.findById(id).map(financialOperation -> {
            FinancialOperationDetailDTO financialOperationDetailDTO = new FinancialOperationDetailDTO();
            financialOperationDetailDTO.setId(financialOperation.getId());
            financialOperationDetailDTO.setOperationNumber(financialOperation.getOperationNumber());
            financialOperationDetailDTO.setOperationType(financialOperation.getOperationType());
            financialOperationDetailDTO.setAmount(financialOperation.getAmount());
            financialOperationDetailDTO.setPaymentType(financialOperation.getPaymentType());
            financialOperationDetailDTO.setDescription(financialOperation.getDescription());
            financialOperationDetailDTO.setCreatedDate(financialOperation.getCreatedDate());
            financialOperationDetailDTO.setStatus(financialOperation.getStatus());
            financialOperationDetailDTO.setPaymentReceivedDate(financialOperation.getPaymentReceivedDate());
            financialOperationDetailDTO.setForeignPrice(financialOperation.getForeignPrice());
            financialOperationDetailDTO.setCurrencyType(financialOperation.getCurrencyType());
            financialOperationDetailDTO.setExchangeRate(financialOperation.getExchangeRate());
            if (financialOperation.getCustomerBranchId() != null) {
                financialOperationDetailDTO.setCustomerBranch(financialOperation.getCustomerBranch().toCommonDTO());
            }
            if (financialOperation.getCustomerId() != null) {
                financialOperationDetailDTO.setCustomer(financialOperation.getCustomer().toCommonBaseDTO());
            }
            if (financialOperation.getCreator() != null) {
                financialOperationDetailDTO.setCreator(financialOperation.getCreator().toCommonDTO());
            }
            if (financialOperation.getStockOut() != null) {
                financialOperationDetailDTO.setStockOut(new CommonCodeDTO(financialOperation.getStockOut().getId(), financialOperation.getStockOut().getStockNumber(), financialOperation.getStockOut().getTransferType().name()));
                if (financialOperation.getFactura() != null) {
                    financialOperationDetailDTO.setFactura(financialOperation.getFactura().toCommonDTO());
                }
            }
            return financialOperationDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(FinancialOperation.class.getSimpleName(), "id", id));
    }

    public Page<FinancialOperationListDTO> getList(FinancialOperationFilter filter) {
        ResultList<FinancialOperation> resultList = financialOperationRepository.getResultList(filter);
        List<FinancialOperationListDTO> result = resultList.getList().stream()
            .map(financialOperation -> {
                FinancialOperationListDTO financialOperationListDTO = new FinancialOperationListDTO();
                financialOperationListDTO.setId(financialOperation.getId());
                financialOperationListDTO.setOperationNumber(financialOperation.getOperationNumber());
                financialOperationListDTO.setCustomer(financialOperation.getCustomer().toCommonBaseDTO());
                if (financialOperation.getCustomerBranch() != null) {
                    financialOperationListDTO.setCustomerBranch(financialOperation.getCustomerBranch().toCommonDTO());
                }
                financialOperationListDTO.setAmount(financialOperation.getAmount());
                financialOperationListDTO.setPaymentType(financialOperation.getPaymentType());
                financialOperationListDTO.setOperationType(financialOperation.getOperationType());
                financialOperationListDTO.setDescription(financialOperation.getDescription());
                financialOperationListDTO.setCreatedDate(financialOperation.getCreatedDate());
                financialOperationListDTO.setStatus(financialOperation.getStatus());
                financialOperationListDTO.setPaymentReceivedDate(financialOperation.getPaymentReceivedDate());
                financialOperationListDTO.setForeignPrice(financialOperation.getForeignPrice());
                financialOperationListDTO.setCurrencyType(financialOperation.getCurrencyType());
                financialOperationListDTO.setExchangeRate(financialOperation.getExchangeRate());
                if (financialOperation.getCreator() != null) {
                    financialOperationListDTO.setCreator(financialOperation.getCreator().toCommonDTO());
                }
                if (financialOperation.getStockOut() != null) {
                    financialOperationListDTO.setStockOut(new CommonCodeDTO(financialOperation.getStockOut().getId(), financialOperation.getStockOut().getStockNumber(), financialOperation.getStockOut().getTransferType().name()));
                    if (financialOperation.getFactura() != null) {
                        financialOperationListDTO.setFactura(financialOperation.getFactura().toCommonDTO());
                    }
                }
                return financialOperationListDTO;
            }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public FinancialOperationStatisticsDTO getStatistics(FinancialOperationFilter filter) {
        return financialOperationRepository.getStatistics(filter);
    }

    private FinancialOperation createEntity(FinancialOperationDTO financialOperationDTO) {
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.FINANCIAL_OPERATION, getCurrentCompanyId());
        FinancialOperation financialOperation = new FinancialOperation();
        financialOperation.setOperationNumber(numberingMap.get(NUMBER));
        financialOperation.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        financialOperation.setCustomerId(financialOperationDTO.getCustomerId());
        financialOperation.setCustomerBranchId(financialOperationDTO.getCustomerBranchId());
        financialOperation.setPaymentType(financialOperationDTO.getPaymentType());
        financialOperation.setForeignPrice(financialOperationDTO.getForeignPrice());
        financialOperation.setCurrencyType(financialOperationDTO.getCurrencyType());
        financialOperation.setExchangeRate(financialOperationDTO.getExchangeRate());
        if (FinancialOperationType.DEBIT.equals(financialOperationDTO.getOperationType())) {
            partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(getCurrentCompanyId(), financialOperationDTO.getCustomerId(), getCurrentCompanyId()).ifPresent(partners -> {
                BigDecimal currentBalance = Optional.ofNullable(partners.getCurrentBalance()).orElse(BigDecimal.ZERO);
                currentBalance = currentBalance.add(financialOperationDTO.getAmount());
                partners.setCurrentBalance(currentBalance);
                partnerRepository.save(partners);
            });
            if (financialOperation.getCustomerBranchId() != null) {
                customerBranchRepository.findById(financialOperation.getCustomerBranchId()).ifPresent(customerBranch -> {
                    BigDecimal currentBalance = Optional.ofNullable(customerBranch.getCurrentBalance()).orElse(BigDecimal.ZERO);
                    currentBalance = currentBalance.add(financialOperationDTO.getAmount());
                    customerBranch.setCurrentBalance(currentBalance);
                    customerBranchRepository.save(customerBranch);
                });
            }
        }

        financialOperation.setDescription(financialOperationDTO.getDescription());
        financialOperation.setCompanyId(getCurrentCompanyId());
        financialOperation.setStockOutId(financialOperationDTO.getStockOutId());
        financialOperation.setFacturaId(financialOperationDTO.getFacturaId());
        financialOperation.setAmount(financialOperationDTO.getAmount());
        financialOperation.setOperationType(financialOperationDTO.getOperationType());
        financialOperation.setPaymentReceivedDate(financialOperationDTO.getPaymentReceivedDate());
        return financialOperationRepository.save(financialOperation);
    }

    @Transactional
    public void createFromStockOut(Stock stockOut) {
        FinancialOperationDTO financialOperationDTO = new FinancialOperationDTO();
        financialOperationDTO.setCustomerBranchId(stockOut.getDeliveryBranchId());
        financialOperationDTO.setCustomerId(stockOut.getCustomerId());
        financialOperationDTO.setAmount(Optional.ofNullable(stockOut.getTotalPrice()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(stockOut.getTotalDiscountPrice()).orElse(BigDecimal.ZERO)));
        financialOperationDTO.setDescription(stockOut.getDescription());
        financialOperationDTO.setStockOutId(stockOut.getId());
        if (PaymentType.TRANSFER == stockOut.getPaymentType()) {
            financialOperationDTO.setOperationType(FinancialOperationType.CREDIT);
        } else {
            financialOperationDTO.setOperationType(FinancialOperationType.DEBIT);
        }
        financialOperationDTO.setPaymentType(stockOut.getPaymentType());
        financialOperationDTO.setPaymentReceivedDate(LocalDateTime.now());
        financialOperationDTO.setExchangeRate(stockOut.getExchangeRate());
        financialOperationDTO.setCurrencyType(stockOut.getCurrencyType());
        this.createEntity(financialOperationDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        financialOperationRepository.findById(id).ifPresentOrElse(financialOperation -> {
            partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(financialOperation.getCompanyId(), financialOperation.getCustomerId(), financialOperation.getCompanyId()).ifPresent(partners -> {
                BigDecimal currentBalance = Optional.ofNullable(partners.getCurrentBalance()).orElse(BigDecimal.ZERO);
                currentBalance = currentBalance.subtract(financialOperation.getAmount());
                partners.setCurrentBalance(currentBalance);
                partnerRepository.save(partners);
            });
            if (financialOperation.getCustomerBranchId() != null) {
                customerBranchRepository.findById(financialOperation.getCustomerBranchId()).ifPresent(customerBranch -> {
                    BigDecimal currentBalance = Optional.ofNullable(customerBranch.getCurrentBalance()).orElse(BigDecimal.ZERO);
                    currentBalance = currentBalance.subtract(financialOperation.getAmount());
                    customerBranch.setCurrentBalance(currentBalance);
                    customerBranchRepository.save(customerBranch);
                });
            }
            financialOperation.setDeleted(true);
            financialOperationRepository.save(financialOperation);
        }, () -> validationService.entityNotFoundExceptionThrow(FinancialOperation.class.getSimpleName(), "id", id));
    }


    public Page<AccountReceivableReportDTO> getAccountReceivableReport(StockFilter filter) {
        ResultList<AccountReceivableMapDTO> resultList = financialOperationRepository.getAccountReceivableReport(filter);
        List<AccountReceivableReportDTO> result = new ArrayList<>();
        for (AccountReceivableMapDTO accountReceivableMapDTO : resultList.getList()) {
            AccountReceivableReportDTO itemDTO = new AccountReceivableReportDTO();
            itemDTO.setFinancialOperation(new CommonDTO(accountReceivableMapDTO.getOperationId(), accountReceivableMapDTO.getOperationNumber()));
            itemDTO.setOperationType(FinancialOperationType.valueOf(accountReceivableMapDTO.getOperationType()));
            itemDTO.setCustomer(new CompanyBaseDTO(accountReceivableMapDTO.getCustomerId(), accountReceivableMapDTO.getCustomerName(), accountReceivableMapDTO.getCustomerTin()));
            if (accountReceivableMapDTO.getStockOutId() != null) {
                itemDTO.setStockOut(new CommonDTO(accountReceivableMapDTO.getStockOutId(), accountReceivableMapDTO.getStockOutNumber()));
            }
            if (accountReceivableMapDTO.getFacturaId() != null) {
                itemDTO.setFactura(new FacturaLinkDTO(accountReceivableMapDTO.getFacturaId(), accountReceivableMapDTO.getFacturaNo(), Status.valueOf(accountReceivableMapDTO.getFacturaStatus()), Status.valueOf(accountReceivableMapDTO.getFacturaStatus()).getName(), accountReceivableMapDTO.getFacturaDate()));
            }
            itemDTO.setOperationDate(accountReceivableMapDTO.getOperationDate());
            if (accountReceivableMapDTO.getPlanRouteId() != null) {
                itemDTO.setPlanRoute(new CommonDTO(accountReceivableMapDTO.getPlanRouteId(), accountReceivableMapDTO.getPlanRouteNumber()));
            }
            if (accountReceivableMapDTO.getAgentId() != null) {
                itemDTO.setAgent(new CommonDTO(accountReceivableMapDTO.getAgentId(), accountReceivableMapDTO.getAgentFirstName().concat(" ".concat(accountReceivableMapDTO.getAgentLastName()))));
            }
            if (accountReceivableMapDTO.getExpeditorId() != null) {
                itemDTO.setExpeditor(new CommonDTO(accountReceivableMapDTO.getExpeditorId(), accountReceivableMapDTO.getExpeditorFirstName().concat(" ".concat(accountReceivableMapDTO.getExpeditorLastName()))));
            }
            if (FinancialOperationType.DEBIT.equals(itemDTO.getOperationType())) {
                itemDTO.setTotalDebit(Optional.ofNullable(accountReceivableMapDTO.getTotalPrice()).orElse(BigDecimal.ZERO));
            } else {
                itemDTO.setTotalCredit(Optional.ofNullable(accountReceivableMapDTO.getTotalPrice()).orElse(BigDecimal.ZERO));
                itemDTO.setOverdueDays(ChronoUnit.DAYS.between(accountReceivableMapDTO.getOperationDate(), LocalDateTime.now()));
            }
            result.add(itemDTO);
        }
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<AccountReceivableReportDTO> getAccountReceivableReportToList(StockFilter filter) {
        filter.setSize(ACCOUNT_RECEIVABLE_REPORT_IMPORT_LIMIT);
        return this.getAccountReceivableReport(filter).getContent();
    }

    @Transactional
    public void deleteByStockOutId(Long stockOutId) {
        Long id = (financialOperationRepository.findIdByStockOutId(stockOutId));
        if (id == null) {
            return;
        }
        this.delete(id);
    }
}
