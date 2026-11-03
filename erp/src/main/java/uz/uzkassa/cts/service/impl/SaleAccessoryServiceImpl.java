package uz.uzkassa.cts.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.SaleAccessory;
import uz.uzkassa.cts.dto.sale.accessory.SaleAccessoryListDTO;
import uz.uzkassa.cts.repository.SaleAccessoryRepository;
import uz.uzkassa.cts.service.SaleAccessoryService;

import java.util.List;
import java.util.Objects;

@Service
public class SaleAccessoryServiceImpl extends BaseServiceImpl<SaleAccessory, Long> implements SaleAccessoryService {

    private final SaleAccessoryRepository saleAccessoryRepository;


    public SaleAccessoryServiceImpl(SaleAccessoryRepository saleAccessoryRepository) {
        super(saleAccessoryRepository);
        this.saleAccessoryRepository = saleAccessoryRepository;
    }

    @Override
    public void create(SaleAccessoryListDTO accessoryListDTO) {
        if (CollectionUtils.isEmpty(accessoryListDTO.getAccessories()) || Objects.isNull(accessoryListDTO.getSaleContractId()))
            return;
        accessoryListDTO.getAccessories().forEach(accessoryDTO -> {
            SaleAccessory accessory = new SaleAccessory();
            accessory.setSaleContractId(accessoryListDTO.getSaleContractId());
            accessory.setAccessoryId(accessoryDTO.getAccessoryId());
            accessory.setQty(accessoryDTO.getQty());
            saleAccessoryRepository.save(accessory);
        });
    }

    @Override
    public List<SaleAccessory> getByContractId(Long contractId) {
        return saleAccessoryRepository.findBySaleContractIdAndDeletedIsFalse(contractId);
    }

    @Override
    public List<SaleAccessory> getByActId(Long actId) {
        return saleAccessoryRepository.findBySaleActIdAndDeletedIsFalse(actId);
    }
}
