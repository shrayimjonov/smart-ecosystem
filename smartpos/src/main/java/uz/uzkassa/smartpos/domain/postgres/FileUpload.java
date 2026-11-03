package uz.uzkassa.smartpos.domain.postgres;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.EntityType;

import java.io.Serializable;


@Entity
@Table(name = "file_upload")
@Getter
@Setter
public class FileUpload implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "object_name")
    private String objectName;

    @Column(name = "url", columnDefinition = "varchar")
    private String url;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityType entityType;

    @Column(name = "entity_id")
    Long entityId;

    @Override
    public int hashCode() {
        return getClass().hashCode();
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
}
