package uz.uzkassa.horeca.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.address.Address;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.enums.AttachmentType;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "post_attachment")
@Entity
public class PostAttachment extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 8948593832L;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false, insertable = false)
    FileUpload photo;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "video_url")
    String videoUrl;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Embedded
    Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type")
    AttachmentType attachmentType;

    @Column(name = "sorder")
    Integer sorder;

    @OneToMany(mappedBy = "postAttachment", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostAttachment)) {
            return false;
        }
        return id != null && id.equals(((PostAttachment) o).id);
    }
}
