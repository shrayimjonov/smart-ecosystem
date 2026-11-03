package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.SocialAddress;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressListDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

public interface SocialAddressService extends BaseService<SocialAddress, Long> {

    List<SocialAddressListDTO> getByEntityTypeAndEntityId(EntityTypeEnum entityType, Long entityId);

    void createBatch(EntityTypeEnum entityType, Long entityId, List<SocialAddressDTO> socialAddressDTOList);

    void updateBatch(EntityTypeEnum entityType, Long entityId, List<SocialAddressDTO> socialAddressDTOList);
}
