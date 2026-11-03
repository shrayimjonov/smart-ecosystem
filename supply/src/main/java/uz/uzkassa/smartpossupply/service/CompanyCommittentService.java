package uz.uzkassa.smartpossupply.service;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.CompanyCommittentWarehouse;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentTokenBatchSyncDTO;
import uz.uzkassa.smartpossupply.dto.companycommittent.CompanyCommittentDTO;
import uz.uzkassa.smartpossupply.dto.report.CompanyCommittentReportDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.repository.CompanyCommittentRepository;
import uz.uzkassa.smartpossupply.repository.CompanyCommittentWarehouseRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.10.2023
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CompanyCommittentService extends BaseService {

    CompanyCommittentRepository companyCommittentRepository;
    CompanyCommittentWarehouseRepository companyCommittentWarehouseRepository;

    @Transactional
    public void syncTokens(CompanyCommittentTokenBatchSyncDTO companyCommittentTokenBatchSyncDTO) {
        companyCommittentTokenBatchSyncDTO.getCompanies().forEach(committentTokenDTO -> {
            Company committent = companyRepository.findByTinAndDeletedIsFalse(committentTokenDTO.getTin()).orElse(null);
            if (committent != null) {
                List<Long> companyIds = companyRepository.findAllByTinInAndDeletedIsFalse(committentTokenDTO.getCommittents());
                if (!CollectionUtils.isEmpty(companyIds)) {
                    Map<Long, List<CompanyCommittent>> existingByCompanyId = companyCommittentRepository
                            .findAllByCompanyIdInAndCommittentIdOrderByTokenTimeDesc(companyIds, committent.getId())
                            .stream()
                            .collect(Collectors.groupingBy(CompanyCommittent::getCompanyId));
                    Set<CompanyCommittent> companyCommittentList = new HashSet<>();
                    companyIds.forEach(companyId -> {
                        List<CompanyCommittent> companyCommittents = existingByCompanyId.get(companyId);
                        if (CollectionUtils.isEmpty(companyCommittents)) {
                            CompanyCommittent companyCommittent = new CompanyCommittent();
                            companyCommittent.setCompanyId(companyId);
                            companyCommittent.setCommittentId(committent.getId());
                            companyCommittent.setToken(committentTokenDTO.getToken());
                            companyCommittent.setTokenTime(LocalDateTime.now());
                            companyCommittentList.add(companyCommittent);
                        } else {
                            companyCommittents.forEach(companyCommittent -> {
                                companyCommittent.setToken(committentTokenDTO.getToken());
                                companyCommittent.setTokenTime(LocalDateTime.now());
                                companyCommittentList.add(companyCommittent);
                            });
                        }
                    });
                    companyCommittentRepository.saveAll(companyCommittentList);
                    companyCommittentList.clear();
                }
            }
        });
    }

    public List<CompanyCommittentLookUpDTO> lookUp(BaseFilter filter) {
        return companyCommittentRepository.lookUp(filter);
    }

    public String getToken(Long id) {
        return companyCommittentRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanyCommittent.class.getSimpleName(), "id", id)).getToken();
    }

    @Transactional
    public CompanyCommittent createOrGet(CompanyCommittentDTO companyCommittentDTO) {
        CompanyCommittent companyCommittent = companyCommittentRepository.findFirstByCompanyIdAndCommittentIdOrderByTokenTimeDesc(
                companyCommittentDTO.getCompanyId(),
                companyCommittentDTO.getCommittentId()
        ).orElse(new CompanyCommittent());
        if (companyCommittent.getId() == null) {
            companyCommittent.setCompanyId(companyCommittentDTO.getCompanyId());
            companyCommittent.setCommittentId(companyCommittentDTO.getCommittentId());
            companyCommittentRepository.save(companyCommittent);
        }
        return companyCommittent;
    }

    public Page<CompanyCommittentReportDTO> getReport(BalanceReportFilter filter) {
        ResultList<CompanyCommittentWarehouse> resultList = companyCommittentWarehouseRepository.getResultList(filter);
        List<CompanyCommittentReportDTO> result = resultList
                .getList()
                .stream()
                .map(companyCommittentWarehouse -> {
                    CompanyCommittentReportDTO companyCommittentReportDTO = new CompanyCommittentReportDTO();
                    companyCommittentReportDTO.setId(companyCommittentWarehouse.getId());
                    companyCommittentReportDTO.setProduct(companyCommittentWarehouse.getProduct().toProductCommonDTO());
                    companyCommittentReportDTO.setCommittent(companyCommittentWarehouse.getCompanyCommittent().getCommittent().toCommonBaseDTO());
                    companyCommittentReportDTO.setQty(companyCommittentWarehouse.getQty());
                    companyCommittentReportDTO.setBranch(companyCommittentWarehouse.getProduct().getBranch().toCommonDTO());
                    companyCommittentReportDTO.setWarehouse(companyCommittentWarehouse.getWarehouse().toCommonDTO());
                    companyCommittentReportDTO.setPrice(companyCommittentWarehouse.getProduct().getPrice());
                    companyCommittentReportDTO.setTotalPrice(Optional.ofNullable(companyCommittentReportDTO.getPrice()).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(companyCommittentReportDTO.getQty()).orElse(BigDecimal.ZERO)));
                    companyCommittentReportDTO.setHasMark(companyCommittentWarehouse.getProduct().isHasMark());
                    companyCommittentReportDTO.setCapacity(companyCommittentWarehouse.getProduct().getCapacity());
                    companyCommittentReportDTO.setProductType(companyCommittentWarehouse.getProduct().getProductType());
                    companyCommittentReportDTO.setBoxBarcode(companyCommittentWarehouse.getProduct().getBoxBarcode());
                    companyCommittentReportDTO.setBlockBarcode(companyCommittentWarehouse.getProduct().getBlockBarcode());
                    if (companyCommittentWarehouse.getProduct().getParentId() != null) {
                        companyCommittentReportDTO.setParent(companyCommittentWarehouse.getProduct().getParent().toProductCommonDTO());
                        if (StringUtils.isEmpty(companyCommittentReportDTO.getBlockBarcode())) {
                            companyCommittentReportDTO.setBlockBarcode(companyCommittentWarehouse.getProduct().getParent().getBarcode());
                        }
                    }
                    return companyCommittentReportDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public BigDecimal getReportTotalQty(BalanceReportFilter filter) {
        return companyCommittentWarehouseRepository.getReportTotalQty(filter);
    }

    public StockProductQtyStatisticsDTO getQtyStatistics(StockFilter filter) {
        return companyCommittentWarehouseRepository.getQtyStatistics(filter);
    }

    @Transactional
    public void updateQty(Long comittentId) {
        for (CompanyCommittentWarehouse warehouseProduct : companyCommittentWarehouseRepository.findAllByCompanyCommittentId(comittentId)) {
            StockFilter filter = new StockFilter();
            filter.setCompanyId(warehouseProduct.getProduct().getCompanyId());
            filter.setCompanyCommittentId(comittentId);
            filter.setWarehouseId(warehouseProduct.getWarehouseId());
            filter.setBranchId(warehouseProduct.getWarehouse().getBranchId());
            StockProductQtyStatisticsDTO statisticsDTO = this.getQtyStatistics(filter);
            BigDecimal qty = BigDecimal.ZERO;
            qty = qty.add(Optional.ofNullable(statisticsDTO.getStockInTotal()).orElse(BigDecimal.ZERO));
            qty = qty.subtract(Optional.ofNullable(statisticsDTO.getStockOutTotal()).orElse(BigDecimal.ZERO));
            qty = qty.add(Optional.ofNullable(statisticsDTO.getStockReturnTotal()).orElse(BigDecimal.ZERO));
            qty = qty.subtract(Optional.ofNullable(statisticsDTO.getStockAdjustmentTotal()).orElse(BigDecimal.ZERO));
            qty = qty.subtract(Optional.ofNullable(statisticsDTO.getStockTransferFromTotal()).orElse(BigDecimal.ZERO));
            qty = qty.add(Optional.ofNullable(statisticsDTO.getStockTransferToTotal()).orElse(BigDecimal.ZERO));
            warehouseProduct.setQty(qty);
            companyCommittentWarehouseRepository.save(warehouseProduct);
        }
    }

    public Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter) {
        return companyCommittentWarehouseRepository.getQtyDetails(filter);
    }
}
