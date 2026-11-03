package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.Version;
import uz.uzkassa.silen.dto.VersionDTO;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.repository.pg.VersionRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VersionService extends BaseService {
    VersionRepository versionRepository;

    public Long create(VersionDTO versionDTO) {
        Optional<Version> optional = versionRepository.findFirstByMajorAndMinorAndPatch(versionDTO.getMajor(), versionDTO.getMinor(), versionDTO.getPatch());
        if (optional.isPresent()) {
            throw badRequest("Версия уже создана").get();
        }
        if (versionDTO.isActive()) {
            versionRepository.updateAllByActiveIsTrue();
        }
        Version version = new Version();
        version.setMajor(versionDTO.getMajor());
        version.setMinor(versionDTO.getMinor());
        version.setPatch(versionDTO.getPatch());
        version.setActive(version.isActive());
        versionRepository.save(version);
        return version.getId();
    }

    public Page<VersionDTO> findAllByFilter(Filter filter) {
        return versionRepository.findAll(filter.getPageable())
            .map(Version::toDTO);
    }

    public VersionDTO update(Long id, VersionDTO versionDTO) {
        Optional<Version> optional = versionRepository.findFirstByIdIsNotAndMajorAndMinorAndPatch(id, versionDTO.getMajor(), versionDTO.getMinor(), versionDTO.getPatch());
        if (optional.isPresent()) {
            throw badRequest("Версия уже создана").get();
        }
        Version version = versionRepository.findById(id).orElseThrow(notFound());
        if (versionDTO.isActive()) {
            versionRepository.updateAllByActiveIsTrue();
        }
        version.setMajor(versionDTO.getMajor());
        version.setMinor(versionDTO.getMinor());
        version.setPatch(versionDTO.getPatch());
        version.setActive(version.isActive());
        versionRepository.save(version);
        return version.toDTO();
    }


    public void delete(Long id) {
        versionRepository.deleteById(id);
    }

    public VersionDTO get(Long id) {
        return versionRepository.findById(id).orElseThrow(notFound()).toDTO();
    }
}
