package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Store;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.StoreDTO;
import uz.uzkassa.silen.dto.filter.StoreFilter;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.repository.pg.StoreRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Store}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService extends BaseService {
    private final StoreRepository storeRepository;

    @Transactional
    public StoreDTO save(StoreDTO storeDTO) {
        final String organizationId = getCurrentOrganizationId();
        Optional<Store> isExists = storeRepository.findFirstByNameAndOrganizationIdAndDeletedIsFalse(storeDTO.getName(), organizationId);
        if (isExists.isPresent() && !Objects.equals(storeDTO.getId(), isExists.get().getId())) {
            throw new BadRequestException("Склад с таким именем уже существует");
        }
        Store store = Optional.ofNullable(storeDTO.getId())
            .flatMap(storeRepository::findById)
            .orElseGet(Store::new);
        if (store.getId() == null) {
            store.setOrganizationId(organizationId);
        }
        if (storeDTO.isMain()) {
            List<Store> allMains = storeRepository.findAllByOrganizationIdAndDeletedIsFalseAndMainIsFalse(organizationId);
            for (Store storeItem : allMains) {
                storeItem.setMain(false);
                storeRepository.save(storeItem);
            }
        }
        store.setBranchCode(storeDTO.getBranchCode());
        store.setAddress(storeDTO.getAddress());
        store.setDistrictId(storeDTO.getDistrictId());
        store.setName(storeDTO.getName());
        store.setStatus(storeDTO.getStatus());
        store.setPersonal(storeDTO.getPersonal());
        store = storeRepository.save(store);

        return store.toDTO();
    }

    public Page<StoreDTO> findAll(StoreFilter filter) {
        return storeRepository.findAllByFilter(filter)
            .map(Store::toDTO);
    }

    public List<SelectItem> getItems(StoreFilter filter) {
        if (getCurrentOrganizationId() == null) {
            throw new BadRequestException("Идентификатор организации не найден");
        }
        return storeRepository.findAllByFilter(filter)
            .map(Store::toSelectItem).getContent();
    }

    public StoreDTO findOne(Long id) {
        return storeRepository.findById(id)
            .map(Store::toDTO)
            .orElseThrow(notFound());
    }

    @Transactional
    public void delete(Long id) {
        storeRepository.deleteById(id);
    }
}
