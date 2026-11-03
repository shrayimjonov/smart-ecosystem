package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.dto.factura.PlatformType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Powered by Shuxratjon Rayimjonov
 * Date: 03.10.2024
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "system_upgrade")
public class SystemUpgrade extends SimpleEntity implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemUpgrade)) {
            return false;
        }
        return id != null && id.equals(((SystemUpgrade) o).id);
    }
}
