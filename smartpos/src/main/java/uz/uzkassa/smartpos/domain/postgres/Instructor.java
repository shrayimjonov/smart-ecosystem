package uz.uzkassa.smartpos.domain.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.dto.Links;
import uz.uzkassa.smartpos.enums.InstructorType;

import java.util.List;

@Getter
@Setter
@Table(name = "instructor")
@Entity
public class Instructor extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "description")
    String description;

    @NotNull
    @Column(name = "file_id")
    Long fileId;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    FileUpload file;

    @Column(name = "image_id")
    Long imageId;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "image_id", insertable = false, updatable = false)
    FileUpload image;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    List<Links> links;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    InstructorType type;

    @NotNull
    String parentCode;
}

