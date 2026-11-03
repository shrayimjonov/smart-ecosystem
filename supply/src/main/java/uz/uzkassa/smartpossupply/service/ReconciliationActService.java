package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpossupply.domain.FinancialOperation;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.reconciliationact.CalculationDTO;
import uz.uzkassa.smartpossupply.dto.reconciliationact.FinancialOperationACTDTO;
import uz.uzkassa.smartpossupply.dto.reconciliationact.ReconciliationActDTO;
import uz.uzkassa.smartpossupply.dto.reconciliationact.StockProductACTDTO;
import uz.uzkassa.smartpossupply.filters.ReconciliationActFilter;
import uz.uzkassa.smartpossupply.repository.CompanyRepository;
import uz.uzkassa.smartpossupply.repository.FinancialOperationRepository;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReconciliationActService {
    CompanyRepository companyRepository;
    FinancialOperationRepository financialOperationRepository;
    StockOutRepository stockOutRepository;
    StockProductRepository stockProductRepository;

    public Page<FinancialOperationACTDTO> getReconciliationActByFinancialOperation(ReconciliationActFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        ResultList<FinancialOperation> resultList = financialOperationRepository.getResultList(filter);
        List<FinancialOperationACTDTO> financialOperationACTDTOs = resultList.getList().stream().map(financialOperation -> {
            FinancialOperationACTDTO dto = new FinancialOperationACTDTO();
            dto.setId(financialOperation.getId());
            dto.setPaymentReceivedDate(financialOperation.getPaymentReceivedDate());
            dto.setOperationNumber(financialOperation.getOperationNumber());
            dto.setCustomerBranch(Optional.ofNullable(financialOperation.getCustomerBranch()).map(SimpleEntity::toCommonDTO).orElse(null));
            dto.setOperationType(financialOperation.getOperationType());
            dto.setAmount(financialOperation.getAmount());
            dto.setCurrencyType(financialOperation.getCurrencyType());
            dto.setForeignPrice(financialOperation.getForeignPrice());
            dto.setStockOutId(financialOperation.getStockOutId());
            return dto;
        }).toList();
        return new PageImpl<>(financialOperationACTDTOs, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<StockProductACTDTO> getReconciliationActByProduct(ReconciliationActFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        ResultList<StockProduct> resultList = stockProductRepository.getList(filter);
        List<StockProductACTDTO> stockProductACTDTOS = resultList.getList().stream().map(stockProduct -> {
            StockProductACTDTO dto = new StockProductACTDTO();
            dto.setId(stockProduct.getId());
            dto.setStockNumber(Optional.ofNullable(stockProduct.getStock()).map(Stock::getStockNumber).orElse(null));
            dto.setStockOutId(stockProduct.getStockId());
            dto.setProduct(stockProduct.getProduct().toCommonDTO());
            dto.setCustomerBranch(Optional.ofNullable(stockProduct.getStock().getDeliveryBranch()).map(SimpleEntity::toCommonDTO).orElse(null));
            dto.setQty(stockProduct.getQty());
            dto.setSellingPrice(stockProduct.getSellingPrice());
            dto.setTotalSellingPrice(stockProduct.getTotalSellingPrice());
            return dto;
        }).toList();
        return new PageImpl<>(stockProductACTDTOS, filter.getSortedPageable(), resultList.getCount());
    }


    public CalculationDTO calculation(ReconciliationActFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        CalculationDTO calculationDTO = stockOutRepository.calculationDeliveryAmount(filter);
        calculationDTO.setPaymentAmount(financialOperationRepository.calculationPaymentAmount(filter));
        return calculationDTO;
    }

    public ReconciliationActDTO financialOperationCalculation(ReconciliationActFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        ReconciliationActDTO reconciliationActDTO = new ReconciliationActDTO();
        Optional<Company> companyOptional = companyRepository.findByIdWithOwner(filter.getCompanyId());
        Optional<Company> customerOptional = companyRepository.findByIdWithOwner(filter.getCustomerId());
        companyOptional.ifPresent(c -> {
            reconciliationActDTO.setCompany(c.toCommonDTO());
            reconciliationActDTO.setCompanyOwner(Optional.ofNullable(c.getOwner()).map(SimpleEntity::toCommonDTO).orElse(null));
        });

        customerOptional.ifPresent(c -> {
            reconciliationActDTO.setCustomer(c.toCommonDTO());
            reconciliationActDTO.setCustomerOwner(Optional.ofNullable(c.getOwner()).map(SimpleEntity::toCommonDTO).orElse(null));
        });
        Object[] objects = financialOperationRepository.calculationDebitAndCredit(filter);
        reconciliationActDTO.setTotalDebitAmount(BigDecimal.valueOf(((Number) objects[0]).doubleValue()));
        reconciliationActDTO.setTotalCreditAmount(BigDecimal.valueOf(((Number) objects[1]).doubleValue()));
        return reconciliationActDTO;
    }
}
