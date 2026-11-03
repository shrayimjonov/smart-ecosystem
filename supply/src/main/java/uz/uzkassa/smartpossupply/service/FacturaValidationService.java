package uz.uzkassa.smartpossupply.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.factura.FacturaValidation;
import uz.uzkassa.smartpossupply.dto.factura.FacturaSendDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaValidateResponseDTO;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.repository.FacturaValidationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FacturaValidationService {


    FacturaRestClient facturaRestClient;
    FacturaValidationRepository facturaValidationRepository;
    ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FacturaValidation create(Long id, String tin, FacturaSendDTO dto) {
        FacturaValidateResponseDTO response = facturaRestClient.validateFactura(tin, dto);
        FacturaValidation facturaValidation = new FacturaValidation();
        facturaValidation.setFacturaId(id);
        facturaValidation.setValid(response.getValid());
        facturaValidation.setError(response.getError());
        facturaValidation.setWarning(response.getWarning());
        facturaValidation.setValidationIssues(writeJson(response.getValidationIssues()));
        facturaValidation.setContextData(writeJson(response.getContextData()));
        facturaValidation.setRawResponse(writeJson(response));
        return facturaValidationRepository.save(facturaValidation);
    }

    private String writeJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize validation field: {}", e.getMessage());
            return String.valueOf(value);
        }
    }
}