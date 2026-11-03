package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.Lgota;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaDTO;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaListDTO;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaLookUpDTO;
import uz.uzkassa.smartpossupply.dto.payload.LgotaSyncPayload;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.repository.LgotaRepository;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.07.2023
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LgotaService extends BaseService {

    LgotaRepository lgotaRepository;
    FacturaRestClient facturaRestClient;

    private void validate(LgotaDTO lgotaDTO) {
        if (lgotaRepository.existsByCatalogCode(lgotaDTO.getCatalogCode())) {
            throw validationService.conflictExceptionThrow("Lgota exists by catalog code:" + lgotaDTO.getCatalogCode()).get();
        }
    }

    @Transactional
    public Long create(LgotaDTO lgotaDTO) {
        this.validate(lgotaDTO);
        return this.createEntity(lgotaDTO).getId();
    }

    private Lgota createEntity(LgotaDTO lgotaDTO) {
        Lgota lgota = new Lgota();
        lgota.setLgotaId(lgotaDTO.getLgotaId());
        lgota.setNewLgotaId(lgotaDTO.getNewLgotaId());
        lgota.setName(lgotaDTO.getName());
        lgota.setType(lgotaDTO.getType());
        lgota.setCatalogCode(lgotaDTO.getCatalogCode());
        lgotaRepository.save(lgota);
        return lgota;
    }

    @Transactional
    public Lgota createOrGet(LgotaDTO lgotaDTO) {
        Lgota lgota = lgotaRepository.findFirstByCatalogCode(lgotaDTO.getCatalogCode()).orElse(null);
        if (lgota == null) {
            lgota = this.createEntity(lgotaDTO);
        }
        return lgota;
    }

    @Transactional
    public void sync(LgotaSyncPayload payload) {
        List<Lgota> lgotaList = facturaRestClient.getLgotaByWithoutVatProducts(payload.getCatalogCode(), false)
            .stream()
            .map(lgotaProduct -> {
                Lgota lgota = lgotaRepository.findFirstByCatalogCode(lgotaProduct.getCatalogCode()).orElse(null);
                if (lgota == null) {
                    lgota = new Lgota();
                }
                lgota.setLgotaId(Optional.ofNullable(lgotaProduct.getNewLgotaId()).orElse(lgotaProduct.getLgotaId()));
                lgota.setNewLgotaId(lgotaProduct.getNewLgotaId());
                lgota.setName(lgotaProduct.getLgotaName());
                lgota.setType(1);
                lgota.setCatalogCode(lgotaProduct.getCatalogCode());
                return lgota;
            }).toList();

        if (!CollectionUtils.isEmpty(lgotaList)) {
            lgotaRepository.saveAll(lgotaList);
        }
    }

    public Page<LgotaListDTO> getList(BaseFilter filter) {
        ResultList<Lgota> resultList = lgotaRepository.getResultList(filter);
        List<LgotaListDTO> result =
            resultList
                .getList()
                .stream()
                .map(lgota -> {
                    LgotaListDTO lgotaListDTO = new LgotaListDTO();
                    lgotaListDTO.setId(lgota.getId());
                    lgotaListDTO.setLgotaId(lgota.getLgotaId());
                    lgotaListDTO.setNewLgotaId(lgota.getNewLgotaId());
                    lgotaListDTO.setName(lgota.getName());
                    lgotaListDTO.setType(lgota.getType());
                    lgotaListDTO.setCatalogCode(lgota.getCatalogCode());
                    return lgotaListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<LgotaLookUpDTO> lookup(BaseFilter filter) {
        return lgotaRepository
            .getResultList(filter)
            .getList()
            .stream()
            .map(Lgota::toLookUpDTO)
            .toList();
    }
}
