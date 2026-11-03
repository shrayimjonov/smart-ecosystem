package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.SocialAddress;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressListDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.repository.SocialAddressRepository;
import uz.uzkassa.cts.service.SocialAddressService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class SocialAddressServiceImpl extends BaseServiceImpl<SocialAddress, Long> implements SocialAddressService {

    private final SocialAddressRepository socialAddressRepository;

    public SocialAddressServiceImpl(SocialAddressRepository socialAddressRepository) {
        super(socialAddressRepository);
        this.socialAddressRepository = socialAddressRepository;
    }

    @Override
    public List<SocialAddressListDTO> getByEntityTypeAndEntityId(EntityTypeEnum entityType, Long entityId) {
        return socialAddressRepository.findAllByEntityTypeAndEntityId(entityType, entityId).stream().map(socialAddress -> {
            SocialAddressListDTO socialAddressListDTO = new SocialAddressListDTO();
            socialAddressListDTO.setId(socialAddress.getId());
            socialAddressListDTO.setName(socialAddress.getName());
            socialAddressListDTO.setSocialType(socialAddress.getSocialType().toCommonCodeDTO());
            return socialAddressListDTO;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void createBatch(EntityTypeEnum entityType, Long entityId, List<SocialAddressDTO> socialAddressDTOList) {
        socialAddressRepository.saveAll(socialAddressDTOList.stream().map(socialAddressDTO -> {
            SocialAddress socialAddress = new SocialAddress();
            socialAddress.setName(socialAddressDTO.getName());
            socialAddress.setSocialTypeId(socialAddressDTO.getSocialTypeId());
            socialAddress.setEntityId(entityId);
            socialAddress.setEntityType(entityType);
            return socialAddress;
        }).collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public void updateBatch(EntityTypeEnum entityType, Long entityId, List<SocialAddressDTO> socialAddressDTOList) {
        socialAddressRepository.deleteAllByEntityTypeAndEntityId(entityType, entityId);
        this.createBatch(entityType, entityId, socialAddressDTOList);
    }
}
