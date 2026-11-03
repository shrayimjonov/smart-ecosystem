package uz.uzkassa.smartpossupply.service;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.address.Country;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.Continent;
import uz.uzkassa.smartpossupply.repository.CountryRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CountryService {

    CountryRepository countryRepository;

    public List<CommonDTO> getItems(Continent continent) {
        if (continent != null) {
            return countryRepository.findAllByContinent(continent).stream().map(Country::toCommonDTO).toList();
        }
        return countryRepository.findAll().stream().map(Country::toCommonDTO).toList();
    }
}
