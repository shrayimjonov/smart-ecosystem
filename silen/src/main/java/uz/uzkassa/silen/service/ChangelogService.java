package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.domain.Changelog;
import uz.uzkassa.silen.dto.ChangelogDTO;
import uz.uzkassa.silen.dto.filter.ChangelogFilter;
import uz.uzkassa.silen.repository.pg.ChangelogRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChangelogService extends BaseService {
    ChangelogRepository changelogRepository;
    ExportReportUploadService exportReportUploadService;

    public Long create(ChangelogDTO changelogDTO) {
        Changelog changelog = new Changelog();
        changelog.setVersionId(changelogDTO.getVersionId());
        changelog.setContent(changelogDTO.getContent());
        changelog.setImagePath(changelogDTO.getImagePath());
        changelogRepository.save(changelog);
        return changelog.getId();
    }

    public Page<ChangelogDTO> findAllByFilter(ChangelogFilter filter) {
        return changelogRepository.findAllByVersionId(filter.getVersionId(), filter.getPageable())
            .map(Changelog::toDTO);
    }

    public ChangelogDTO update(Long id, ChangelogDTO changelogDTO) {
        Changelog changelog = changelogRepository.findById(id).orElseThrow(notFound());
        changelog.setVersionId(changelogDTO.getVersionId());
        changelog.setContent(changelogDTO.getContent());
        changelog.setImagePath(changelogDTO.getImagePath());
        changelogRepository.save(changelog);
        return changelog.toDTO();
    }

    public void delete(Long id) {
        changelogRepository.deleteById(id);
    }

    public ChangelogDTO get(Long id) {
        return changelogRepository.findById(id).orElseThrow(notFound()).toDTO();
    }

    public String upload(MultipartFile file) {
        return exportReportUploadService.fileUpload(file, System.currentTimeMillis() + "." + FilenameUtils.getExtension(file.getOriginalFilename()), "changelog");
    }

    public ResponseEntity<?> download(String id) {
        return exportReportUploadService.downloadById(id);
    }
}
