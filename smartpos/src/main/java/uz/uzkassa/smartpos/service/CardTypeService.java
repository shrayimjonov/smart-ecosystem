package uz.uzkassa.smartpos.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.CardType;
import uz.uzkassa.smartpos.dto.CardTypeDto;
import uz.uzkassa.smartpos.repository.postgres.CardTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CardTypeService {
    private final CardTypeRepository cardTypeRepository;

    public CardTypeDto create(CardTypeDto dto) {
        CardType cardType = new CardType(dto.getPrefix());
        cardTypeRepository.save(cardType);
        return dto;
    }

    public void batchCreate(List<String> prefixes) {
        cardTypeRepository.saveAll(prefixes
            .stream().map(CardType::new).collect(Collectors.toList()));
    }

    public void delete(String prefix) {
        cardTypeRepository.deleteById(prefix);
    }

    public List<CardTypeDto> findAll() {
        return cardTypeRepository.findAll()
            .stream().map(cardType -> new CardTypeDto(cardType.getPrefix()))
            .collect(Collectors.toList());
    }
}
