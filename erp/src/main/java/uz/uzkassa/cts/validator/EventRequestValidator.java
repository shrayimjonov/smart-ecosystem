package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.eventrequest.EventRequestDTO;
import uz.uzkassa.cts.repository.ProductRepository;
import uz.uzkassa.cts.service.ValidationService;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventRequestValidator extends BaseValidator implements ValidatorStrategy<EventRequestDTO> {

    ProductRepository productRepository;
    ValidationService validationService;

    public EventRequestValidator(LocalizationMessage localizationMessage, ProductRepository productRepository, ValidationService validationService) {
        super(localizationMessage);
        this.productRepository = productRepository;
        this.validationService = validationService;
    }

    @Override
    public void validate(EventRequestDTO dto) {
        validateRequiredField(dto.getSubject(), SUBJECT);
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getFromDate(), DATE);
        validateRequiredField(dto.getToDate(), DATE);
        validateRequiredField(dto.getApproverId(), APPROVER);
        validateRequiredField(dto.getMemberCount(), MEMBER_COUNT);
        validateRequiredField(dto.getRoom(), ROOM);
        validateRequiredField(dto.getItems(), ITEMS);
        dto.getItems().forEach(eventRequestItemDTO -> {
            validateRequiredField(eventRequestItemDTO.getProductId(), PRODUCT);
            validateRequiredField(eventRequestItemDTO.getQuantity(), QTY);
            if (!productRepository.existsById(eventRequestItemDTO.getProductId())) {
                throw validationService.entityNotFoundExceptionThrow(PRODUCT, ID, eventRequestItemDTO.getProductId().toString()).get();
            }
        });
    }
}
