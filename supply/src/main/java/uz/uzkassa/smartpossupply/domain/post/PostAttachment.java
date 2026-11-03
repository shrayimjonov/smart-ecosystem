package uz.uzkassa.smartpossupply.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.enums.AttachmentType;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "post_attachment",indexes = {
        @Index(columnList = "post_id", name = "post_attachment_post_id_idx"),
})
@Entity
public class PostAttachment extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8948593832L;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false, insertable = false)
    FileUpload photo;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "post_text")
    String postText;

    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type")
    AttachmentType attachmentType;

    @Column(name = "sorder")
    Integer sorder;

    @OneToMany(mappedBy = "postAttachment", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("structure")
    Set<PostPhotoGallery> photoGalleries = new HashSet<>();

    @Column(name = "post_id")
    Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", updatable = false, insertable = false)
    Post post;

    public void addPhotoGallery(PostPhotoGallery postPhotoGallery) {
        postPhotoGallery.setPostAttachment(this);
        postPhotoGallery.setPostAttachmentId(this.id);
        photoGalleries.add(postPhotoGallery);
    }
}
