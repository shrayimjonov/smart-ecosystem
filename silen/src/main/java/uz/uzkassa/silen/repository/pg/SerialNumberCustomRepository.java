package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.dto.SerialNumberListDTO;
import uz.uzkassa.silen.dto.SerialSelectItemDTO;
import uz.uzkassa.silen.dto.filter.SerialNumberFilter;

import java.util.List;

public interface SerialNumberCustomRepository {

    Page<SerialNumberListDTO> findAllByFilter(SerialNumberFilter serialNumberFilter);

    Long getStats(SerialNumberFilter serialNumberFilter);

    List<SerialSelectItemDTO> getItems(SerialNumberFilter serialNumberFilter);
}
