package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Reference;
import uz.uzkassa.silen.dto.ReferenceDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.ReferenceFilter;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.repository.pg.ReferenceRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Reference}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReferenceService extends BaseService {
    ReferenceRepository referenceRepository;

    @Transactional
    public ReferenceDTO save(ReferenceDTO referenceDTO) {
        Optional<Reference> referenceOptional = referenceRepository.findFirstByParentIdAndCodeAndNameAndDeletedIsFalse(referenceDTO.getParentId(), referenceDTO.getCode(), referenceDTO.getName());
        if (referenceOptional.isPresent() && (referenceDTO.getId() == null || !referenceDTO.getId().equals(referenceOptional.get().getId()))) {
            throw new BadRequestException("Такое наименование существует");
        }
        Reference reference = Optional.ofNullable(referenceDTO.getId())
            .flatMap(referenceRepository::findById)
            .orElseGet(Reference::new);
        reference.setParentId(referenceDTO.getParentId());
        reference.setName(referenceDTO.getName());
        reference.setCode(referenceDTO.getCode());
        reference = referenceRepository.save(reference);
        return reference.toDto();
    }

    public Page<ReferenceDTO> findAll(ReferenceFilter filter) {
        return referenceRepository.findAllByFilter(filter).map(Reference::toDto);
    }

    public List<SelectItem> getItems(ReferenceFilter filter) {
        return referenceRepository.getItems(filter);
    }

    public ReferenceDTO findOne(String id) {
        return referenceRepository.findById(id)
            .map(Reference::toDto)
            .orElseThrow(notFound());
    }

    @Transactional
    public void delete(String id) {
        referenceRepository.deleteById(id);
    }
}
