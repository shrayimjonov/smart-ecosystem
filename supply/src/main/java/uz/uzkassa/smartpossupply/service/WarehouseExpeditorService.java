package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.product.WarehouseExpeditorMapDTO;
import uz.uzkassa.smartpossupply.dto.warehouseexpeditor.WarehouseExpeditorCarListDTO;
import uz.uzkassa.smartpossupply.dto.warehouseexpeditor.WarehouseExpeditorListDTO;
import uz.uzkassa.smartpossupply.dto.warehouseexpeditor.WarehouseExpeditorStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.PlanRouteTemplateRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseExpeditorRepository;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.11.2024
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class WarehouseExpeditorService extends BaseService {

    WarehouseExpeditorRepository warehouseExpeditorRepository;
    PlanRouteTemplateRepository planRouteTemplateRepository;

    public WarehouseExpeditorStatisticsDTO getStatistics(BaseFilter filter) {
        return warehouseExpeditorRepository.getStatistics(filter);
    }

    public Page<WarehouseExpeditorListDTO> getList(BaseFilter filter) {
        ResultList<WarehouseExpeditorMapDTO> resultList = warehouseExpeditorRepository.getList(filter);
        List<WarehouseExpeditorListDTO> result = new ArrayList<>();
        for (WarehouseExpeditorMapDTO warehouseExpeditorMapDTO : resultList.getList()) {
            WarehouseExpeditorListDTO warehouseExpeditorListDTO = new WarehouseExpeditorListDTO();
            warehouseExpeditorListDTO.setExpeditor(new CommonDTO(warehouseExpeditorMapDTO.getExpeditorId(), warehouseExpeditorMapDTO.getExpeditorFirstName().concat(" ").concat(warehouseExpeditorMapDTO.getExpeditorLastName())));
            warehouseExpeditorListDTO.setTotalQty(warehouseExpeditorMapDTO.getTotalQty());
            warehouseExpeditorListDTO.setTotalOrderCount(warehouseExpeditorMapDTO.getTotalOrderCount());
            warehouseExpeditorListDTO.setTotalOrderCount(warehouseExpeditorMapDTO.getTotalOrderCount());
            warehouseExpeditorListDTO.setTotalSoldQty(warehouseExpeditorMapDTO.getTotalSoldQty());
            warehouseExpeditorListDTO.setTotalCompletedOrderCount(warehouseExpeditorMapDTO.getTotalCompletedOrderCount());
            warehouseExpeditorListDTO.setPlanRouteTemplates(planRouteTemplateRepository.getPlanRouteTemplates(warehouseExpeditorMapDTO.getExpeditorId()).stream().map(SimpleEntity::toCommonDTO).toList());
            result.add(warehouseExpeditorListDTO);
        }
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<WarehouseExpeditorCarListDTO> getCarList(BaseFilter filter) {
        ResultList<WarehouseExpeditorMapDTO> resultList = warehouseExpeditorRepository.getCarList(filter);
        List<WarehouseExpeditorCarListDTO> result = new ArrayList<>();
        for (WarehouseExpeditorMapDTO warehouseExpeditorMapDTO : resultList.getList()) {
            WarehouseExpeditorCarListDTO warehouseExpeditorListDTO = new WarehouseExpeditorCarListDTO();
            warehouseExpeditorListDTO.setCar(new CommonCodeDTO(warehouseExpeditorMapDTO.getCarId(), warehouseExpeditorMapDTO.getCarRegNo(), warehouseExpeditorMapDTO.getCarModel()));
            warehouseExpeditorListDTO.setTotalQty(warehouseExpeditorMapDTO.getTotalQty());
            warehouseExpeditorListDTO.setTotalSoldQty(warehouseExpeditorMapDTO.getTotalSoldQty());
            result.add(warehouseExpeditorListDTO);
        }
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }
}