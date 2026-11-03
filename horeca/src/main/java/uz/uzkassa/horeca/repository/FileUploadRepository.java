package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.enums.EntityTypeEnum;

import java.util.List;

public interface FileUploadRepository extends BaseRepository<FileUpload, Long> {
    List<FileUpload> findAllByEntityIdAndEntityType(Long entityId, EntityTypeEnum entityType);
}
