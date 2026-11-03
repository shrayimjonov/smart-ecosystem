package uz.uzkassa.horeca.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.PostStatus;
import uz.uzkassa.horeca.enums.PostType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "post")
@Entity
@SQLDelete(sql = "UPDATE post SET deleted='true' WHERE id=?")
public class Post extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 558745126L;

    @Column(name = "title")
    String title;

    @Column(name = "post_date")
    LocalDateTime postDate;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    PostType postType;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_status")
    PostStatus status = PostStatus.DRAFT;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        return id != null && id.equals(((Post) o).id);
    }

    @Override
    public String getName() {
        return getTitle();
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.POST;
    }
}
