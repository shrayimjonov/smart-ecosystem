package uz.uzkassa.smartpos.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.dto.ReceiptLimitDto;
import uz.uzkassa.smartpos.mappers.ReceiptLimitMapper;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptLimitRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceiptLimitService {
    private final ReceiptLimitRepository repository;
    private final ReceiptLimitMapper receiptLimitMapper;
    private final LocalizationService localizationService;

    public ReceiptLimitDto createReceiptLimit(ReceiptLimitDto dto) {
        return receiptLimitMapper.toDto(repository.save(receiptLimitMapper.toEntity(dto)));
    }

    public ReceiptLimitDto updateReceiptLimit(ReceiptLimitDto dto) {
        return receiptLimitMapper.toDto(
                repository.save(
                        receiptLimitMapper.partialUpdate(
                                dto,
                                repository.findByCode(dto.getCode()).orElseThrow(() -> new EntityNotFoundException(localizationService.getMessage("receipt.limit.not.found")))
                        )
                )
        );
    }

    public ReceiptLimitDto getReceiptLimitByCode(String code) {
        return repository.findByCode(code)
                .map(receiptLimitMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(localizationService.getMessage("receipt.limit.not.found")));
    }

    public List<ReceiptLimitDto> getAllReceiptLimit() {
        return repository.findAll().stream().map(receiptLimitMapper::toDto).collect(Collectors.toList());
    }

    public ReceiptLimitDto getReceiptLimitForTerminal() {
        return repository.findByCode("COMMON_RECEIPT_LIMIT")
                .map(receiptLimitMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(localizationService.getMessage("receipt.limit.not.found")));
    }
}
