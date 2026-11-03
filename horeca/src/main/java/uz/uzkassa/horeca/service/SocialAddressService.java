package uz.uzkassa.horeca.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import uz.uzkassa.horeca.domain.address.SocialAddress;
import uz.uzkassa.horeca.dto.socialaddress.SocialAddressDTO;
import uz.uzkassa.horeca.dto.socialaddress.SocialAddressDetailDTO;
import uz.uzkassa.horeca.repository.SocialAddressRepository;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocialAddressService extends BaseService {
    SocialAddressRepository socialAddressRepository;

    public void validate(SocialAddressDTO socialAddressDTO) {
        socialAddressDTO.setCompanyId(Optional.ofNullable(socialAddressDTO.getCompanyId()).orElse(HeaderUtils.getCurrentCompanyId()));
        if (socialAddressRepository.existsByCompanyIdAndType(socialAddressDTO.getCompanyId(), socialAddressDTO.getType())) {
            throw badRequestExceptionThrow("Социальная страница уже добавлена").get();
        }
        if (socialAddressRepository.existsByCompanyIdAndTypeAndIdNot(socialAddressDTO.getCompanyId(), socialAddressDTO.getType(), socialAddressDTO.getId())) {
            throw badRequestExceptionThrow("Социальная страница уже добавлена").get();
        }
    }

    public void create(SocialAddressDTO socialAddressDTO) {
        this.validate(socialAddressDTO);
        SocialAddress socialAddress = new SocialAddress();
        socialAddress.setPageUri(socialAddressDTO.getUri());
        socialAddress.setCompanyId(socialAddressDTO.getCompanyId());
        socialAddress.setType(socialAddressDTO.getType());
        socialAddressRepository.save(socialAddress);
    }

    public void update(Long id, SocialAddressDTO socialAddressDTO) {
        this.validate(socialAddressDTO);
        SocialAddress socialAddress = socialAddressRepository.findById(id).orElseThrow(notFoundExceptionThrow(SocialAddress.class.getSimpleName(), "id", id));
        socialAddress.setPageUri(socialAddressDTO.getUri());
        socialAddress.setCompanyId(socialAddressDTO.getCompanyId());
        socialAddress.setType(socialAddressDTO.getType());
        socialAddressRepository.save(socialAddress);
    }

    public List<SocialAddressDetailDTO> getList() {
        return socialAddressRepository.findAllByCompanyIdOrderByIdDesc(HeaderUtils.getCurrentCompanyId()).stream().map(SocialAddress::toDTO).toList();
    }

    public void delete(Long id) {
        socialAddressRepository.deleteById(id);
    }
}
