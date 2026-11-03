package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.stock.CurrentStock;
import uz.uzkassa.horeca.domain.unit.MultiUnit;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.stock.CurrentStockDTO;
import uz.uzkassa.horeca.dto.stock.CurrentStockListDTO;
import uz.uzkassa.horeca.dto.stock.CurrentStockUpdateStatusDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.CurrentStockStatus;
import uz.uzkassa.horeca.filters.MenuItemFilter;
import uz.uzkassa.horeca.repository.CurrentStockRepository;
import uz.uzkassa.horeca.repository.MultiUnitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.11.2024
 */
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Slf4j
public class CurrentStockService extends BaseService {

    CurrentStockRepository currentStockRepository;
    MultiUnitRepository multiUnitRepository;

    private void validate(CurrentStockDTO currentStockDTO) {
        if (currentStockDTO.getMenuItemId() == null) {
            throw badRequestExceptionThrow("Menu item is required").get();
        }
    }

    @Transactional
    public Long create(CurrentStockDTO currentStockDTO) {
        this.validate(currentStockDTO);
        CurrentStock currentStock = currentStockRepository.findFirstByMenuItemIdAndStatusAndDeletedIsFalse(currentStockDTO.getMenuItemId(), CurrentStockStatus.OPEN).orElseGet(CurrentStock::new);
        if (currentStock.getId() == null) {
            currentStock.setMenuItemId(currentStockDTO.getMenuItemId());
        }
        currentStock.setQty(currentStockDTO.getQty());
        currentStock.setRecurring(currentStockDTO.isRecurring());
        currentStock.setTransferPreviousQty(currentStockDTO.isTransferPreviousQty());
        currentStockRepository.save(currentStock);
        return currentStock.getId();
    }

    @Transactional
    public void create(List<CurrentStockDTO> currentStockList) {
        for (CurrentStockDTO currentStockDTO : currentStockList) {
            this.create(currentStockDTO);
        }
    }

    @Transactional
    public void delete(Long id) {
        currentStockRepository.findById(id)
                .ifPresent(currentStock -> {
                    if (CurrentStockStatus.CLOSED.equals(currentStock.getStatus())) {
                        throw badRequestExceptionThrow("Closed stock can not be deleted").get();
                    }
                    currentStock.setDeleted(true);
                    currentStockRepository.save(currentStock);
                });
    }

    @Transactional
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            this.delete(id);
        }
    }

    @Transactional
    public void updateStatus(List<Long> ids, CurrentStockStatus status) {
        ids.forEach(id -> this.updateStatus(id, status));
    }

    @Transactional
    public void updateStatus(Long id, CurrentStockStatus status) {
        currentStockRepository.findById(id)
                .ifPresent(currentStock -> {
                    if (CurrentStockStatus.CLOSED.equals(currentStock.getStatus())) {
                        throw badRequestExceptionThrow("Closed stock can not be updated").get();
                    }
                    if (CurrentStockStatus.CLOSED.equals(status)) {
                        List<CurrentStock> list = new ArrayList<>();
                        currentStock.setStatus(status);
                        list.add(currentStock);
                        CurrentStock newStock = new CurrentStock();
                        newStock.setMenuItemId(currentStock.getMenuItemId());
                        newStock.setRecurring(currentStock.isRecurring());
                        newStock.setTransferPreviousQty(currentStock.isTransferPreviousQty());
                        if (currentStock.isRecurring()) {
                            newStock.setQty(currentStock.getQty());
                        }
                        if (currentStock.isTransferPreviousQty()) {
                            newStock.setQty(newStock.getQty().add(currentStock.getQty().subtract(currentStock.getSoldQty())));
                        }
                        list.add(newStock);
                        currentStockRepository.saveAll(list);
                    } else {
                        currentStock.setStatus(status);
                        currentStockRepository.save(currentStock);
                    }
                });
    }

    public Page<CurrentStockListDTO> getList(MenuItemFilter filter) {
        ResultList<CurrentStock> resultList = currentStockRepository.getResultList(filter);
        List<CurrentStockListDTO> result = new ArrayList<>();
        resultList.getList().forEach(currentStock -> {
            CurrentStockListDTO currentStockListDTO = new CurrentStockListDTO();
            currentStockListDTO.setId(currentStock.getId());
            currentStockListDTO.setMenuItem(currentStock.getMenuItem().toCommonDTO());
            if (currentStock.getMenuItem().getProduct().getDepartment() != null) {
                currentStockListDTO.setDepartment(currentStock.getMenuItem().getProduct().getDepartment().toCommonDTO());
            }
            currentStockListDTO.setUnits(multiUnitRepository.findAllByMenuItemIdAndDeletedIsFalse(currentStock.getMenuItemId())
                    .stream()
                    .map(this::toMultiUnitListDTO).collect(Collectors.toList()));
            currentStockListDTO.setQty(currentStock.getQty());
            currentStockListDTO.setSoldQty(currentStock.getSoldQty());
            currentStockListDTO.setRecurring(currentStock.isRecurring());
            currentStockListDTO.setTransferPreviousQty(currentStock.isTransferPreviousQty());
            currentStockListDTO.setStatus(currentStock.getStatus());
            result.add(currentStockListDTO);
        });
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void updateStatus(CurrentStockUpdateStatusDTO statusDTO) {
        this.updateStatus(statusDTO.getIds(), statusDTO.getStatus());
    }

    public CurrentStock getCurrentStockByMenuItemId(Long menuItemId) {
        return currentStockRepository.findFirstByMenuItemIdAndDeletedIsFalseOrderByCreatedDateDesc(menuItemId).orElse(null);
    }
}