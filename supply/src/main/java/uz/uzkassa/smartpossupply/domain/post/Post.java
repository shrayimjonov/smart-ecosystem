package uz.uzkassa.smartpossupply.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.PostPosition;
import uz.uzkassa.smartpossupply.enums.PostStatus;
import uz.uzkassa.smartpossupply.enums.PostType;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "post", indexes = {
        @Index(columnList = "company_id", name = "post_company_id_idx"),
})
@Entity
@SQLDelete(sql = "UPDATE post SET deleted='true' WHERE id=?")
public class Post extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 6895848341L;

    @Column(name = "title")
    String title;

    @Column(name = "post_date")
    LocalDateTime postDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    PostType postType;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_status")
    PostStatus status = PostStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_position")
    PostPosition position;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Override
    public String getName() {
        return this.getTitle();
    }
}
