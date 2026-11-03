package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.repository.custom.FileUploadRepositoryCustom;

import java.util.List;

@Repository
public interface FileUploadRepository extends BaseRepository<FileUpload, Long>, FileUploadRepositoryCustom {

    List<FileUpload> findAllByDocumentTypeAndEntityTypeAndEntityIdAndContentType(DocumentType documentType, EntityTypeEnum entityType, Long entityId, String contentType);

    List<FileUpload> findAllByEntityTypeAndEntityIdAndDeletedIsFalse(EntityTypeEnum entityType, Long entityId);

    FileUpload findFirstByDocumentTypeAndEntityTypeAndEntityId(DocumentType documentType, EntityTypeEnum entityType, Long entityId);

}
