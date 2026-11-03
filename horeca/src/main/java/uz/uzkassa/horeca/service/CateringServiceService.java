package uz.uzkassa.horeca.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.domain.hall.Hall;
import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.dto.cateringsettings.CateringServiceBatchDTO;
import uz.uzkassa.horeca.dto.cateringsettings.CateringServiceDTO;
import uz.uzkassa.horeca.dto.cateringsettings.HallFeeListDTO;
import uz.uzkassa.horeca.dto.product.CateringServiceDetailDTO;
import uz.uzkassa.horeca.enums.CategoryType;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.CategoryRepository;
import uz.uzkassa.horeca.repository.HallItemRepository;
import uz.uzkassa.horeca.repository.HallRepository;
import uz.uzkassa.horeca.repository.ProductRepository;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CateringServiceService extends BaseService {
    CategoryRepository categoryRepository;
    ProductRepository productRepository;
    HallItemRepository hallItemRepository;
    HallRepository hallRepository;

    public Long createBatch(CateringServiceBatchDTO dto) {
        List<HallItem> hallItems = new ArrayList<>();
        if (dto.getHallId() != null) {
            Hall hall = hallRepository.findById(dto.getHallId()).orElseThrow(notFoundExceptionThrow(Hall.class.getSimpleName(), "id", dto.getHallId()));
            hall.setFeePercent(dto.getFeePercent());
            hallRepository.save(hall);
            hallItemRepository.findAllByHallIdAndDeletedIsFalseOrderByIdAsc(hall.getId()).forEach(table -> {
                table.setFeePercent(dto.getFeePercent());
                hallItems.add(table);
            });
        }
        if (!CollectionUtils.isEmpty(dto.getTables())) {
            Map<Long, BigDecimal> tablesFeeMap = dto.getTables().stream().collect(Collectors.toMap(CateringServiceDTO::getTableId, CateringServiceDTO::getFeePercent));
            hallItemRepository.findAllByIdIn(tablesFeeMap.keySet()).forEach(table -> {
                table.setFeePercent(tablesFeeMap.get(table.getId()));
                hallItems.add(table);
            });
        }
        hallItemRepository.saveAll(hallItems);
        return dto.getHallId();
    }

    public CateringServiceDetailDTO getCateringService(Long id) {
        final Long branchId = Optional.ofNullable(id).orElse(HeaderUtils.getCurrentBranchId());
        if (branchId == null) {
            throw badRequestExceptionThrow("branch.not.selected").get();
        }
        Category category = categoryRepository.findFirstByBranchIdAndCategoryTypeAndDeletedIsFalse(branchId, CategoryType.CATERING_SERVICE)
                .orElseThrow(notFoundExceptionThrow(Category.class.getSimpleName()));
        return productRepository.findFirstByBranchIdAndCategoryIdAndDeletedIsFalse(branchId, category.getId()).map(product -> {
            CateringServiceDetailDTO cateringServiceDetailDTO = new CateringServiceDetailDTO();
            cateringServiceDetailDTO.setId(product.getId());
            cateringServiceDetailDTO.setName(product.getName());
            cateringServiceDetailDTO.setCatalogCode(product.getCatalogCode());
            cateringServiceDetailDTO.setCatalogName(product.getCatalogName());
            cateringServiceDetailDTO.setBarcode(product.getBarcode());
            cateringServiceDetailDTO.setPackageCode(product.getPackageCode());
            cateringServiceDetailDTO.setPackageName(product.getPackageName());
            cateringServiceDetailDTO.setVat(product.getVat());
            cateringServiceDetailDTO.setOrigin(product.getOrigin());
            cateringServiceDetailDTO.setCategory(category.toCommonDTO());
            cateringServiceDetailDTO.setUnit(product.getUnit().toCommonDTO());
            return cateringServiceDetailDTO;
        }).orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName()));
    }


    public List<HallFeeListDTO> getHallsFee(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        filter.setSize(100);
        return hallRepository.getResultList(filter).getList().stream().map(hall -> {
            HallFeeListDTO hallFeeListDTO = new HallFeeListDTO();
            hallFeeListDTO.setId(hall.getId());
            hallFeeListDTO.setName(hall.getName());
            hallFeeListDTO.setFeePercent(hall.getFeePercent());
            return hallFeeListDTO;
        }).collect(Collectors.toList());
    }
}
