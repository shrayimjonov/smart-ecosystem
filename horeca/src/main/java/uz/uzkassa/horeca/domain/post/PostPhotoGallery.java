package uz.uzkassa.horeca.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.base.BaseEntity;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "post_photo_gallery")
public class PostPhotoGallery extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 15158132132L;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false, insertable = false)
    FileUpload photo;

    @Column(name = "structure")
    Integer structure;

    @Column(name = "post_attachment_id")
    Long postAttachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_attachment_id", updatable = false, insertable = false)
    PostAttachment postAttachment;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostPhotoGallery)) {
            return false;
        }
        return id != null && id.equals(((PostPhotoGallery) o).id);
    }
}
