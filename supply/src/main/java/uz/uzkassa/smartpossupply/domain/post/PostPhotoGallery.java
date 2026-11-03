package uz.uzkassa.smartpossupply.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "post_photo_gallery", indexes = {
        @Index(columnList = "post_attachment_id", name = "post_photo_gallery_post_attachment_id_idx"),
})
public class PostPhotoGallery extends BaseEntity {

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
}
