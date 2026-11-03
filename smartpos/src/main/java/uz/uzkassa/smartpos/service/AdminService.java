package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.common.Excise;
import uz.uzkassa.smartpos.domain.postgres.common.Vat;
import uz.uzkassa.smartpos.dto.ExciseDTO;
import uz.uzkassa.smartpos.dto.VatDTO;
import uz.uzkassa.smartpos.repository.postgres.common.VatRepository;
import uz.uzkassa.smartpos.repository.postgres.company.ExciseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService extends SmartposService {
    private final ExciseRepository exciseRepository;
    private final VatRepository vatRepository;

    public List<ExciseDTO> getExciseList() {
        return exciseRepository.findAll().stream().map(ExciseDTO::new).collect(Collectors.toList());
    }

    public void clearExcisesCache() {
        clearCache(Excise.class.getName());
    }

    public List<VatDTO> getVatList() {
        return vatRepository.findAll().stream().map(Vat::toDTO).collect(Collectors.toList());
    }

    public VatDTO getVat(Long id) {
        return vatRepository.findById(id).map(Vat::toDTO)
            .orElseThrow(notFound(localizationService.getMessage("vat"), id));
    }

    public void saveVat(VatDTO dto) {
        Optional<Vat> vatOptional = vatRepository.findDistinctByNameIgnoreCaseAndPercent(dto.getName().toLowerCase(),
            dto.getPercent());
        if (vatOptional.isPresent() && (dto.getId() == null || !dto.getId().equals(vatOptional.get().getId()))) {
            throw badRequest(localizationService.getMessage("vat.already.exists")).get();
        }

        Vat vat = Optional.ofNullable(dto.getId()).flatMap(vatRepository::findById).orElseGet(Vat::new);
        vat.setName(dto.getName());
        vat.setPercent(dto.getPercent());
        vatRepository.save(vat);
    }

    public void deleteVat(Long id) {
        vatRepository.deleteById(id);
    }
}
