package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

@Entity
@Table(name = "document")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE document SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class Document extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 7563395970L;

    @Column(name = "name")
    String name;

    @Column(name = "content_type")
    String contentType;

    @Column(name = "size")
    Long size;

    @Column(name = "path")
    String path;

    @Column(name = "file_id")
    Long fileId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", updatable = false, insertable = false)
    FileUpload file;

    @Column(name = "folder_id", nullable = false)
    Long folderId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", updatable = false, insertable = false)
    Folder folder;
}
