package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.file.FileDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.utils.FileUtils;

import java.io.Serial;

@Entity
@Table(name = "file_upload")
@Getter
@Setter
@SQLDelete(sql = "UPDATE file_upload SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileUpload extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 7417287147L;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "original_name")
    String originalName;

    @Column(name = "content_type")
    String contentType;

    @Column(name = "size")
    Long size;

    @Column(name = "file_id")
    String fileId;

    @Column(name = "download_url", length = 500)
    String downloadUrl;

    @Column(name = "path", length = 500)
    String path;

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "document_type")
    @Enumerated(EnumType.STRING)
    DocumentType documentType;

    @Column(name = "is_public", columnDefinition = "boolean default false")
    boolean isPublic = false;

    @Override
    public String getName() {
        return getFileName();
    }

    @Override
    public String toString() {
        return "FileUpload{" +
                "fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", entityId=" + entityId +
                ", entityType=" + entityType +
                ", documentType=" + documentType +
                '}';
    }

    public FileDTO toFileDTO() {
        return new FileDTO(getId(), getName(), getDownloadUrl());
    }

    public UploadFileResponse getDTO() {
        UploadFileResponse fileResponse = new UploadFileResponse();
        fileResponse.setId(getId());
        fileResponse.setFileName(getFileName());
        fileResponse.setName(FilenameUtils.getName(getFileName()));
        fileResponse.setContentType(getContentType());
        fileResponse.setFileDownloadUri(getDownloadUrl());
        fileResponse.setSize(getSize());
        fileResponse.setFileSize(FileUtils.getFileNameSizeAsString(getSize()));
        fileResponse.setDocumentType(getDocumentType());
        fileResponse.setCreatedDate(getCreatedDate());
        if (getCreator() != null) {
            fileResponse.setCreatedBy(getCreator().getName());
        }

        return fileResponse;
    }

}
