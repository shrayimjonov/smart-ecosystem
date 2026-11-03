package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileSizeType;

import java.util.List;

@Repository
public interface FileUploadRepository extends BaseRepository<FileUpload, Long> {

    List<FileUpload> findAllByEntityIdAndEntityTypeAndFileSizeTypeAndDeletedIsFalse(Long entityId, EntityTypeEnum entityType, FileSizeType fileSizeType);

    @EntityGraph(attributePaths = {"children"})
    List<FileUpload> findAllByEntityIdAndEntityTypeAndParentIsNullAndDeletedIsFalse(Long entityId, EntityTypeEnum entityType);
}
