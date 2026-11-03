package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.warehouse.BasketItem;
import uz.uzkassa.smartpos.dto.BasketDTO;
import uz.uzkassa.smartpos.dto.BasketItemDTO;
import uz.uzkassa.smartpos.dto.BasketItemUpdateDTO;
import uz.uzkassa.smartpos.mappers.BasketItemMapper;
import uz.uzkassa.smartpos.repository.postgres.warehouse.BasketItemsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasketService extends SmartposService {
    private final BasketItemsRepository basketItemsRepository;
    private final BasketItemMapper basketItemMapper;


    @Transactional
    public BasketItemDTO addProduct(BasketItemDTO dto) {
        BasketItem basketItem = basketItemMapper.toEntity(dto);
        basketItemsRepository.save(basketItem);
        return basketItemMapper.toDto(basketItem);
    }

    @Transactional
    public BasketItemDTO updateProduct(BasketItemUpdateDTO dto) {
        BasketItem basketItem = basketItemsRepository.findBySupplierProductIdAndCompanyId(dto.getSupplierProductId(), dto.getCompanyId()).orElseGet(BasketItem::new);
        basketItem.setQty(basketItem.getQty().add(dto.getAmount()));
        basketItem.setSelected(dto.isSelected());
        basketItemsRepository.save(basketItem);
        return basketItemMapper.toDto(basketItem);
    }


    @Transactional
    public void removeProduct(Long supplierProductId, Long companyId) {
        companyId = companyId != null ? companyId : getCurrentCompanyId();
        basketItemsRepository.deleteBySupplierProductIdAndCompanyId(supplierProductId, companyId);
    }

    @Transactional
    public void removeBasket(Long companyId) {
        companyId = companyId != null ? companyId : getCurrentCompanyId();
        basketItemsRepository.deleteByCompanyId(companyId);
    }


    public List<BasketDTO> getBasket(Long companyId, Long supplyId) {
        companyId = companyId != null ? companyId : getCurrentCompanyId();
        List<BasketItem> items;
        if (supplyId != null) {
            items = basketItemsRepository.findByCompanyIdAndSupplyId(companyId, supplyId);
        } else {
            items = basketItemsRepository.findByCompanyId(companyId);
        }
        if (CollectionUtils.isEmpty(items)) {
            return null;
        }
        Map<Long, List<BasketItem>> supplyItems = items.stream().collect(Collectors.groupingBy(BasketItem::getSupplyId));
        List<BasketDTO> result = new ArrayList<>();
        supplyItems.forEach((id, products) -> result.add(new BasketDTO(
                id,
                products.getFirst().getSupplyTin(),
                products.getFirst().getSupplyName(),
                products.stream().map(basketItemMapper::toDto).collect(Collectors.toList())
        )));
        return result;
    }

}
