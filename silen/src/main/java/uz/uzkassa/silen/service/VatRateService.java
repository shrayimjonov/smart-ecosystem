package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.VatRate;
import uz.uzkassa.silen.dto.VatRateDTO;
import uz.uzkassa.silen.dto.mq.VatRatePayload;
import uz.uzkassa.silen.exceptions.EntityNotFoundException;
import uz.uzkassa.silen.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.silen.repository.pg.VatRateRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VatRateService {
    VatRateRepository vatRateRepository;
    RabbitMqProducer rabbitMqProducer;

    @Transactional
    public VatRateDTO create(VatRateDTO vatRateDTO) {
        VatRate vatRate = new VatRate();
        vatRate.setCode(vatRateDTO.getCode());
        vatRate.setName(vatRateDTO.getName());
        vatRate.setAmount(vatRateDTO.getAmount());
        vatRateRepository.save(vatRate);
        return vatRate.toDTO();
    }

    public List<VatRateDTO> findAll() {
        return vatRateRepository.findAll().stream().map(VatRate::toDTO).collect(Collectors.toList());
    }

    public VatRateDTO get(Long id) {
        return vatRateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found")).toDTO();
    }

    @Transactional
    public VatRateDTO update(Long id, VatRateDTO vatRateDTO) {
        VatRate vatRate = vatRateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        VatRatePayload vatRatePayload = new VatRatePayload();
        vatRatePayload.setOldAmount(vatRate.getAmount());
        vatRatePayload.setNewAmount(vatRateDTO.getAmount());

        vatRate.setCode(vatRateDTO.getCode());
        vatRate.setName(vatRateDTO.getName());
        vatRate.setAmount(vatRateDTO.getAmount());
        vatRateRepository.save(vatRate);

        rabbitMqProducer.sendVatRateChanged(vatRatePayload);
        return vatRate.toDTO();
    }

    @Transactional
    public void delete(Long id) {
        VatRate vatRate = vatRateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        VatRatePayload vatRatePayload = new VatRatePayload();
        vatRatePayload.setOldAmount(vatRate.getAmount());
        vatRatePayload.setNewAmount(null);
        rabbitMqProducer.sendVatRateChanged(vatRatePayload);
        vatRateRepository.delete(vatRate);
    }
}
