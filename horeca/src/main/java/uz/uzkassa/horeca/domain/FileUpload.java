package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.FileType;
import uz.uzkassa.horeca.enums.FolderType;
import uz.uzkassa.horeca.enums.PhotoType;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "file_upload")
@SQLDelete(sql = "UPDATE file_upload set deleted='true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileUpload extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 31125345L;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "content_type")
    String contentType;

    @Column(name = "file_size")
    Long fileSize;

    @Column(name = "extension")
    String extension;

    @Column(nullable = false, unique = true)
    String key;

    @Column(nullable = false)
    String url;

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
    PhotoType photoType;

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<FileUpload> children = new ArrayList<>();

    public void addChild(FileUpload child) {
        children.add(child);
    }

    @Override
    public String getName() {
        return this.fileName;
    }

    public FileDTO toFileDTO() {
        return new FileDTO(id, getName(), url);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileUpload)) {
            return false;
        }
        return id != null && id.equals(((FileUpload) o).id);
    }

    public Long getEntityId() {
        return entityId;
    }

    public EntityTypeEnum getEntityType() {
        return entityType;
    }
}
