package uz.uzkassa.cts.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.Vat;
import uz.uzkassa.cts.dto.vat.VatItemDTO;
import uz.uzkassa.cts.dto.vat.admin.VatAdminDTO;
import uz.uzkassa.cts.dto.vat.admin.VatDetailAdminDTO;
import uz.uzkassa.cts.dto.vat.admin.VatListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.repository.VatRepository;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.VatService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class VatServiceImpl extends BaseServiceImpl<Vat, Long> implements VatService, CacheConstants {

    private final VatRepository vatRepository;
    private final CacheService cacheService;
    private final ValidationContext validationContext;

    public VatServiceImpl(VatRepository vatRepository, CacheService cacheService, ValidationContext validationContext) {
        super(vatRepository);
        this.vatRepository = vatRepository;
        this.cacheService = cacheService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(VatAdminDTO vatDTO) {
        validationContext.validate(vatDTO);
        Vat vat = new Vat();
        vat.setNameUzLatin(vatDTO.getNameUzLatin());
        vat.setNameUzCyrillic(vatDTO.getNameUzCyrillic());
        vat.setNameRu(vatDTO.getNameRu());
        if (vatDTO.getPercent() != null) {
            vat.setPercent(vatDTO.getPercent().setScale(2, RoundingMode.HALF_UP));
        }
        cacheService.clearByEntityType(vat.getEntityType());
        vatRepository.save(vat);
        return vat.getId();
    }

    @Override
    public Vat getActiveVat() {
        return vatRepository.findFirstByStatus(CommonStatus.ACTIVE)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(VAT));
    }

    @Override
    @Transactional
    public Long update(Long id, VatAdminDTO vatDTO) {
        vatDTO.setId(id);
        validationContext.validate(vatDTO);
        return vatRepository.findById(vatDTO.getId()).map(vat -> {
            vat.setNameUzLatin(vatDTO.getNameUzLatin());
            vat.setNameUzCyrillic(vatDTO.getNameUzCyrillic());
            vat.setNameRu(vatDTO.getNameRu());
            if (vatDTO.getPercent() != null) {
                vat.setPercent(vatDTO.getPercent().setScale(2, RoundingMode.HALF_UP));
            }
            cacheService.clearByEntityType(vat.getEntityType());
            return vat.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(VAT));

    }

    @Override
    public VatDetailAdminDTO get(Long id) {
        return vatRepository.findById(id).map(vat -> {
            VatDetailAdminDTO vatDetailDTO = new VatDetailAdminDTO();
            vatDetailDTO.setId(vat.getId());
            vatDetailDTO.setNameUzLatin(vat.getNameUzLatin());
            vatDetailDTO.setNameUzCyrillic(vat.getNameUzCyrillic());
            vatDetailDTO.setNameRu(vat.getNameRu());
            vatDetailDTO.setPercent(vat.getPercent());
            vatDetailDTO.setStatus(vat.getStatus());
            return vatDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(VAT));
    }

    @Override
    @Cacheable(cacheNames = CacheConstants.VAT_LIST_CACHE)
    public List<VatListAdminDTO> getList() {
        return vatRepository.findAllByDeletedIsFalse().stream().map(vat -> {
            VatListAdminDTO vatListDTO = new VatListAdminDTO();
            vatListDTO.setId(vat.getId());
            vatListDTO.setNameUzLatin(vat.getNameUzLatin());
            vatListDTO.setNameUzCyrillic(vat.getNameUzCyrillic());
            vatListDTO.setNameRu(vat.getNameRu());
            vatListDTO.setPercent(vat.getPercent());
            vatListDTO.setStatus(vat.getStatus());
            return vatListDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        vatRepository
                .findById(id)
                .ifPresentOrElse(vat -> {
                    vat.setStatus(status);
                    vatRepository.save(vat);
                    if (CommonStatus.ACTIVE == status) {
                        vatRepository.updateStatus(id, CommonStatus.IN_ACTIVE);
                    }
                    cacheService.clearByEntityType(vat.getEntityType());
                }, () -> validationService.entityNotFoundExceptionThrow(VAT).get());
    }

    @Override
    @Cacheable(cacheNames = CacheConstants.VAT_ITEMS_CACHE)
    public List<VatItemDTO> getItems() {
        List<VatItemDTO> result = vatRepository.findAllByStatusAndDeletedIsFalse(CommonStatus.ACTIVE).stream().map(vat -> {
            VatItemDTO vatItemDTO = new VatItemDTO();
            vatItemDTO.setId(vat.getId());
            vatItemDTO.setName(vat.getName());
            vatItemDTO.setPercent(vat.getPercent());
            return vatItemDTO;
        }).collect(Collectors.toList());

        result.addFirst(new VatItemDTO(-1L, "Без НДС", null));
        return result;
    }
}
