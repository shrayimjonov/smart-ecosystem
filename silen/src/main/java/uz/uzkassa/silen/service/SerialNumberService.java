package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.SerialNumber;
import uz.uzkassa.silen.dto.SerialNumberDTO;
import uz.uzkassa.silen.dto.SerialNumberListDTO;
import uz.uzkassa.silen.dto.SerialSelectItemDTO;
import uz.uzkassa.silen.dto.filter.SerialNumberFilter;
import uz.uzkassa.silen.enumeration.SerialNumberStatus;
import uz.uzkassa.silen.repository.pg.SerialNumberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SerialNumberService extends BaseService {
    SerialNumberRepository serialNumberRepository;

    @Transactional
    public SerialNumberListDTO save(SerialNumberDTO serialNumberDTO) {
        SerialNumber serialNumber = new SerialNumber();
        serialNumber.setSerialNumber(serialNumberDTO.getSerialNumber());
        serialNumber.setProductId(serialNumberDTO.getProductId());
        serialNumber.setStatus(SerialNumberStatus.CREATED);
        serialNumber.setDescription(serialNumberDTO.getDescription());
        serialNumber.setOrganizationId(getCurrentOrganizationId());
        serialNumber.setProductionDate(serialNumberDTO.getProductionDate());
        serialNumber.setStartQuantity(serialNumberDTO.getStartQuantity());
        serialNumberRepository.save(serialNumber);
        return null;
    }

    public Page<SerialNumberListDTO> findAll(SerialNumberFilter serialNumberFilter) {
        return serialNumberRepository.findAllByFilter(serialNumberFilter);
    }

    public List<SerialSelectItemDTO> selectItems(SerialNumberFilter serialNumberFilter) {
        return serialNumberRepository.getItems(serialNumberFilter);
    }

    public Long findStatsByFilter(SerialNumberFilter serialNumberFilter) {
        return serialNumberRepository.getStats(serialNumberFilter);
    }

    public SerialNumberListDTO findOne(String serialNumberId) {
        return serialNumberRepository.findById(serialNumberId)
            .map(SerialNumber::toDto).orElseThrow(notFound());
    }

    @Transactional
    public SerialNumberDTO update(String serialNumberId, SerialNumberDTO serialNumberDTO) {
        SerialNumber serialNumber = serialNumberRepository.findById(serialNumberId).orElseThrow(notFound());
        serialNumber.setSerialNumber(serialNumberDTO.getSerialNumber());
        serialNumber.setProductId(serialNumberDTO.getProductId());
        serialNumber.setProductionDate(serialNumberDTO.getProductionDate());
        serialNumber.setStartQuantity(serialNumberDTO.getStartQuantity());
        serialNumber.setDescription(serialNumberDTO.getDescription());
        serialNumberRepository.save(serialNumber);
        return serialNumberDTO;
    }

    @Transactional
    public void delete(String id) {
        SerialNumber serialNumber = serialNumberRepository.findById(id).orElseThrow(notFound());
        if (SerialNumberStatus.CREATED.equals(serialNumber.getStatus()))
            serialNumberRepository.delete(serialNumber);
        else badRequest("Серия статус уже " + serialNumber.getStatus().getNameRu()).get();
    }

    public SerialNumberStatus updateStatus(String serialNumberId, SerialNumberStatus serialNumberStatus) {
        SerialNumber serialNumber = serialNumberRepository.findByIdAndOrganizationId(serialNumberId, getCurrentOrganizationId()).orElseThrow(notFound());
        if (SerialNumberStatus.PRODUCED.equals(serialNumberStatus) && !SerialNumberStatus.CREATED.equals(serialNumber.getStatus())
            || SerialNumberStatus.SHIPPED.equals(serialNumberStatus) && !SerialNumberStatus.PRODUCED.equals(serialNumber.getStatus())) {
            badRequest("Серия статус еще " + serialNumber.getStatus().getNameRu()).get();
        }
        serialNumber.setStatus(serialNumberStatus);
        serialNumberRepository.save(serialNumber);
        return serialNumber.getStatus();
    }
}
