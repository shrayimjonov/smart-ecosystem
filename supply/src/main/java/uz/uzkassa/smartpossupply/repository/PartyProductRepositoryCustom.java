package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.dto.party.PartyProductListDTO;

import java.util.List;

@Repository
public interface PartyProductRepositoryCustom {
    List<PartyProductListDTO> findAllByFilter(Long filter);
}
