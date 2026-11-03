package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.Address;
import uz.uzkassa.cts.dto.address.AddressDTO;
import uz.uzkassa.cts.dto.address.AddressDetailDTO;
import uz.uzkassa.cts.dto.address.AddressListDTO;
import uz.uzkassa.cts.enums.AddressType;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

public interface AddressService extends BaseService<Address, Long> {

    Long create(EntityTypeEnum entityType, Long entityId, AddressDTO addressDTO);

    Long update(EntityTypeEnum entityType, Long entityId, AddressDTO addressDTO);

    void createBatch(EntityTypeEnum entityType, Long entityId, List<AddressDTO> addressDTOList);

    void updateBatch(EntityTypeEnum entityType, Long entityId, List<AddressDTO> addressDTOList);

    List<AddressListDTO> getList(EntityTypeEnum entityType, Long entityId);

    Address getLegalAddress(Long entityId, EntityTypeEnum entityType);

    Address getPermanentAddress(Long entityId, EntityTypeEnum entityType);

    Address getAddressByType(Long entityId, EntityTypeEnum entityType, AddressType type);
}
