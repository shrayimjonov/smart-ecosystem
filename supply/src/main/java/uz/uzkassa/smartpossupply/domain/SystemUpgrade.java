package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.post.Post;
import uz.uzkassa.smartpossupply.enums.PlatformType;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * Powered by Shuxratjon Rayimjonov
 * Date: 26.11.2024
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "system_upgrade")
public class SystemUpgrade extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 84984654L;

    @Column(name = "version")
    String version;

    @Column(name = "description")
    String description;

    @Column(name = "available", columnDefinition = "boolean default true")
    boolean available = true;

    @Column(name = "force_update", columnDefinition = "boolean default false")
    boolean forceUpdate = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform_type")
    PlatformType platformType;

    @Column(name = "release_date")
    LocalDateTime releaseDate;

    @Column(name = "post_id")
    Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", updatable = false, insertable = false)
    Post post;

    @Override
    public String getName() {
        return getVersion();
    }
}
