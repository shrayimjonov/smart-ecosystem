package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileSizeType;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.FolderType;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "file_upload")
@SQLDelete(sql = "UPDATE file_upload SET deleted = true WHERE id=?")
//@SQLRestriction(value = "deleted = false")
public class FileUpload extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 12L;

    @Column(name = "name")
    String name;

    @Column(name = "object_name")
    String objectName;

    @Column(name = "content_type")
    String contentType;

    @Column(name = "file_id")
    String fileId;

    @Column(name = "url")
    String url;

    @Column(name = "path")
    String path;

    @Column(name = "extension")
    String extension;

    @Column(name = "key", unique = true)
    String key;

    @Column(name = "size")
    Long size;

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "file_type")
    @Enumerated(EnumType.STRING)
    FileType fileType;

    @Column(name = "folder_type")
    @Enumerated(EnumType.STRING)
    FolderType folderType;

    @Column(name = "photo_type")
    @Enumerated(EnumType.STRING)
    FileSizeType fileSizeType;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    FileUpload parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<FileUpload> children = new HashSet<>();

    @Column(name = "is_public", columnDefinition = "boolean default false")
    boolean isPublic = false;

    public FileDTO toFileDTO() {
        return new FileDTO(getId(), getName(), getUrl());
    }
}
