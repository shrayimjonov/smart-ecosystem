package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.SaleAccessory;
import uz.uzkassa.cts.dto.sale.accessory.SaleAccessoryListDTO;

import java.util.List;

public interface SaleAccessoryService extends BaseService<SaleAccessory, Long> {

    void create(SaleAccessoryListDTO saleAccessoryListDTO);

    List<SaleAccessory> getByContractId(Long contractId);

    List<SaleAccessory> getByActId(Long actId);
}
