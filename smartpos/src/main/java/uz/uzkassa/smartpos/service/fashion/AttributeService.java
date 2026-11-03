package uz.uzkassa.smartpos.service.fashion;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.catalog.Attribute;
import uz.uzkassa.smartpos.dto.product.AttributeDTO;
import uz.uzkassa.smartpos.repository.postgres.catalog.AttributeRepository;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static uz.uzkassa.smartpos.security.SecurityUtils.getCurrentCompanyId;

@Service
@AllArgsConstructor
public class AttributeService {
    private final AttributeRepository attributeRepository;

    public AttributeDTO create(final AttributeDTO dto) {
        final Attribute attribute = new Attribute();
        attribute.setKey(dto.getKey());
        attribute.setCode(dto.getCode());
        attribute.setNameEn(dto.getNameEn());
        attribute.setNameRu(dto.getNameRu());
        attribute.setNameUz(dto.getNameUz());
        attribute.setSOrder(dto.getSOrder());
        attributeRepository.save(attribute);
        return attribute.toAdminDTO();
    }

    public AttributeDTO update(final Long id, final AttributeDTO dto) {
        final Attribute attribute = attributeRepository.getReferenceById(id);
        if (Objects.equals(attribute.getKey(), "COLOUR")) {
            attribute.setCode(dto.getCode());
        }
        attribute.setNameEn(dto.getNameEn());
        attribute.setNameRu(dto.getNameRu());
        attribute.setNameUz(dto.getNameUz());
        attribute.setSOrder(dto.getSOrder());
        attributeRepository.save(attribute);
        return attribute.toAdminDTO();
    }

    public AttributeDTO get(final Long id) {
        return attributeRepository.findById(id)
                .map(Attribute::toAdminDTO)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found"));
    }

    public void delete(final Long id) {
        attributeRepository.deleteById(id);
    }

    public List<AttributeDTO> getAllAttributes() {
        return attributeRepository.findAllByCompanyId(getCurrentCompanyId())
                .stream()
                .map(Attribute::toAdminDTO)
                .collect(Collectors.toList());
    }

    public List<AttributeDTO> getAttributes(final String key) {
        return attributeRepository.findAllByKey(key)
                .stream()
                .map(Attribute::toAdminDTO)
                .collect(Collectors.toList());
    }

    public List<AttributeDTO> getAttributesForCabinet(final String key) {
        return attributeRepository.findAllByKeyAndCompanyId(key, getCurrentCompanyId())
                .stream()
                .map(a -> a.toDTO(LanguageUtils.getLanguage()))
                .sorted(Comparator.comparing(AttributeDTO::getName))
                .collect(Collectors.toList());
    }
}
