package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Excise;
import uz.uzkassa.silen.dto.ExciseDTO;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.repository.pg.ExciseRepository;

import java.util.Optional;

/**
 * Service Implementation for managing {@link uz.uzkassa.silen.domain.Excise}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExciseService extends BaseService {
    private final ExciseRepository exciseRepository;

    @Transactional
    public ExciseDTO save(ExciseDTO dto) {
        Optional<Excise> exciseOptional = exciseRepository.findOneByType(dto.getType());
        if(exciseOptional.isPresent() && (dto.getId() == null || !dto.getId().equals(exciseOptional.get().getId()))){
            throw badRequest("Excise for this type already exists").get();
        }
        Excise excise = Optional.ofNullable(dto.getId())
            .flatMap(exciseRepository::findById)
            .orElseGet(Excise::new);
        excise.setType(dto.getType());
        excise.setAmount(dto.getAmount());
        excise = exciseRepository.save(excise);
        return excise.toDto();
    }

    public Page<ExciseDTO> findAll(Filter filter) {
      return exciseRepository.findAll(filter.getPageable())
          .map(Excise::toDto);
    }

    public ExciseDTO findOne(Long id) {
        return exciseRepository.findById(id)
            .map(Excise::toDto)
            .orElseThrow(notFound());
    }

    @Transactional
    public void delete(Long id) {
        exciseRepository.deleteById(id);
    }
}
