package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.SystemUpgrade;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.dto.SystemUpgradeDTO;
import uz.uzkassa.horeca.dto.SystemUpgradeDetailDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.factura.PlatformType;
import uz.uzkassa.horeca.filters.SystemUpgradeFilter;
import uz.uzkassa.horeca.repository.SystemUpgradeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SystemUpgradeService extends BaseService {

    SystemUpgradeRepository systemUpgradeRepository;

    private void validate(SystemUpgradeDTO systemUpgradeDTO) {
        if (StringUtils.isEmpty(systemUpgradeDTO.getName())) {
            throw badRequestExceptionThrow("Version is required").get();
        }
        if (systemUpgradeDTO.getPlatformType() == null) {
            throw badRequestExceptionThrow("Platform type is required").get();
        }
        if (
                systemUpgradeDTO.getId() == null &&
                        systemUpgradeRepository.existsByVersionAndPlatformType(systemUpgradeDTO.getName().trim(), systemUpgradeDTO.getPlatformType())
        ) {
            throw conflictExceptionThrow("Version с таким названием " + systemUpgradeDTO.getName() + " уже существует.").get();
        }
        if (
                systemUpgradeDTO.getId() != null &&
                        systemUpgradeRepository.existsByVersionAndPlatformTypeAndIdNot(
                                systemUpgradeDTO.getName().trim(),
                                systemUpgradeDTO.getPlatformType(),
                                systemUpgradeDTO.getId()
                        )
        ) {
            throw conflictExceptionThrow("Version с таким названием " + systemUpgradeDTO.getName() + " уже существует.").get();
        }
    }

    @Transactional
    public Long create(SystemUpgradeDTO systemUpgradeDTO) {
        this.validate(systemUpgradeDTO);
        SystemUpgrade systemUpgrade = new SystemUpgrade();
        systemUpgrade.setVersion(systemUpgradeDTO.getName());
        systemUpgrade.setDescription(systemUpgradeDTO.getDescription());
        systemUpgrade.setAvailable(systemUpgradeDTO.isAvailable());
        systemUpgrade.setForceUpdate(systemUpgradeDTO.isForceUpdate());
        systemUpgrade.setPlatformType(systemUpgradeDTO.getPlatformType());
        systemUpgrade.setReleaseDate(Optional.ofNullable(systemUpgradeDTO.getReleaseDate()).orElse(LocalDateTime.now()));
        systemUpgradeRepository.save(systemUpgrade);
        return systemUpgrade.getId();
    }

    public Page<SystemUpgradeDTO> getList(SystemUpgradeFilter filter) {
        ResultList<SystemUpgrade> resultList = systemUpgradeRepository.getResultList(filter);
        List<SystemUpgradeDTO> result = resultList
                .getList()
                .stream()
                .map(systemUpgrade -> {
                    SystemUpgradeDTO systemUpgradeDTO = new SystemUpgradeDTO();
                    systemUpgradeDTO.setId(systemUpgrade.getId());
                    systemUpgradeDTO.setName(systemUpgrade.getName());
                    systemUpgradeDTO.setDescription(systemUpgrade.getDescription());
                    systemUpgradeDTO.setAvailable(systemUpgrade.isAvailable());
                    systemUpgradeDTO.setReleaseDate(systemUpgrade.getReleaseDate());
                    systemUpgradeDTO.setForceUpdate(systemUpgrade.isForceUpdate());
                    systemUpgradeDTO.setPlatformType(systemUpgrade.getPlatformType());
                    return systemUpgradeDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }


    public List<SystemUpgradeDetailDTO> getAvailableVersions(SystemUpgradeFilter filter) {
        return this.getList(filter).stream().map(systemUpgradeDTO -> new SystemUpgradeDetailDTO(systemUpgradeDTO.getId(), systemUpgradeDTO.getName(), systemUpgradeDTO.getReleaseDate(), systemUpgradeDTO.isForceUpdate())).collect(Collectors.toList());
    }

    @Transactional
    public Long update(Long id, SystemUpgradeDTO systemUpgradeDTO) {
        return systemUpgradeRepository
                .findById(id)
                .map(systemUpgrade -> {
                    systemUpgradeDTO.setId(id);
                    this.validate(systemUpgradeDTO);
                    systemUpgrade.setVersion(systemUpgradeDTO.getName());
                    systemUpgrade.setDescription(systemUpgradeDTO.getDescription());
                    systemUpgrade.setAvailable(systemUpgradeDTO.isAvailable());
                    systemUpgrade.setPlatformType(systemUpgradeDTO.getPlatformType());
                    systemUpgrade.setForceUpdate(systemUpgradeDTO.isForceUpdate());
                    systemUpgrade.setReleaseDate(Optional.ofNullable(systemUpgradeDTO.getReleaseDate()).orElse(LocalDateTime.now()));
                    return systemUpgrade.getId();
                })
                .orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long updateStatus(Long id) {
        return systemUpgradeRepository
                .findById(id)
                .map(systemUpgrade -> {
                    systemUpgrade.setAvailable(!systemUpgrade.isAvailable());
                    systemUpgradeRepository.save(systemUpgrade);
                    return systemUpgrade.getId();
                })
                .orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", id));
    }
}
