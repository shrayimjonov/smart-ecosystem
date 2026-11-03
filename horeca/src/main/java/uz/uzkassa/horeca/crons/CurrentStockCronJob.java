package uz.uzkassa.horeca.crons;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.domain.stock.CurrentStock;
import uz.uzkassa.horeca.dto.stock.CurrentStockDTO;
import uz.uzkassa.horeca.enums.CurrentStockStatus;
import uz.uzkassa.horeca.repository.CurrentStockRepository;
import uz.uzkassa.horeca.service.CurrentStockService;
import uz.uzkassa.horeca.service.MenuItemService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 15.11.2024
 */
@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
//@Profile("prod")
public class CurrentStockCronJob {

    CurrentStockService currentStockService;
    MenuItemService menuItemService;
    CurrentStockRepository currentStockRepository;


    public CurrentStockCronJob(CurrentStockService currentStockService, MenuItemService menuItemService, CurrentStockRepository currentStockRepository) {
        this.currentStockService = currentStockService;
        this.menuItemService = menuItemService;
        this.currentStockRepository = currentStockRepository;
    }

    //Fire in every day at 00:01
    @Scheduled(cron = "0 1 0 * * ?")
    public void updateStatusAndCreate() {
        List<CurrentStock> currentStockList = new ArrayList<>();
        List<CurrentStockDTO> currentStockCreateList = new ArrayList<>();
        log.info("==========================================Cron job started==========================================");
        log.info("==========================================Time: {}==========================================", LocalDateTime.now());
        menuItemService.getListForCronJob().forEach(menuItem -> {
            CurrentStock currentStock = currentStockService.getCurrentStockByMenuItemId(menuItem.getId());
            CurrentStockDTO currentStockDTO = new CurrentStockDTO();
            currentStockDTO.setMenuItemId(menuItem.getId());
            if (currentStock != null) {
                if (currentStock.isRecurring()) {
                    currentStockDTO.setQty(currentStock.getQty());
                }
                if (currentStock.isTransferPreviousQty() && currentStock.getQty().subtract(currentStock.getSoldQty()).compareTo(BigDecimal.ZERO) >= 0) {
                    currentStockDTO.setQty(currentStockDTO.getQty().add(currentStock.getQty().subtract(currentStock.getSoldQty())));
                }
                currentStockDTO.setRecurring(currentStock.isRecurring());
                currentStockDTO.setTransferPreviousQty(currentStock.isTransferPreviousQty());
                currentStock.setStatus(CurrentStockStatus.CLOSED);
                currentStockList.add(currentStock);
            }
            currentStockCreateList.add(currentStockDTO);
        });
        currentStockService.create(currentStockCreateList);
        currentStockRepository.saveAllAndFlush(currentStockList);
    }
}