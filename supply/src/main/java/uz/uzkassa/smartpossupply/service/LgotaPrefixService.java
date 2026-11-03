package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.LgotaPrefix;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaPrefixDTO;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.repository.LgotaPrefixRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LgotaPrefixService extends BaseService {

    LgotaPrefixRepository lgotaPrefixRepository;
    FacturaRestClient facturaRestClient;

    @Transactional
    public void sync() {
        LgotaPrefixDTO[] lgotaPrefixDTOS = facturaRestClient.getLgotaPrefix();
        List<LgotaPrefix> lgotaPrefixes = new ArrayList<>();
        for (LgotaPrefixDTO lgotaPrefixDTO : lgotaPrefixDTOS) {
            if (lgotaPrefixRepository.findDistinctTopByPrefixIgnoreCaseAndNameIgnoreCase(lgotaPrefixDTO.getPrefix(), lgotaPrefixDTO.getName()).isPresent()) {
                continue;
            }
            LgotaPrefix lgotaPrefix = new LgotaPrefix();
            lgotaPrefix.setPrefix(lgotaPrefixDTO.getPrefix());
            lgotaPrefix.setName(lgotaPrefixDTO.getName());
            lgotaPrefixes.add(lgotaPrefix);
        }
        if (CollectionUtils.isNotEmpty(lgotaPrefixes)) {
            lgotaPrefixRepository.saveAll(lgotaPrefixes);
        }
    }

    public List<LgotaPrefixDTO> getItems() {
        return lgotaPrefixRepository.findAllByOrderByPrefixAsc().stream().map(LgotaPrefix::toDTO).collect(Collectors.toList());
    }
}
