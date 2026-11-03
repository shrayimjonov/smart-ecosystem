package uz.uzkassa.smartpos.domain.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.smartpos.enums.EPSMethodType;
import uz.uzkassa.smartpos.enums.EPSServicesStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "eps_services")
public class EpsServices {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @NotBlank
    private String name;
    private String nameRu;
    private String nameUz;
    private String nameCyrillic;
    @NotBlank
    private String type;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EPSServicesStatusEnum status = EPSServicesStatusEnum.ACTIVE;
    private String logoId;
    private String logoUrl;
    @Enumerated(EnumType.STRING)
    private EPSMethodType qrMethod;
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    private LocalDateTime createdDate;
    @LastModifiedBy
    private String updatedBy;
    private LocalDateTime updatedDate;
    @Column(name = "file_id")
    private Long fileId;
    @ManyToOne
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    private FileUpload file;
    private boolean fromApay;
    private boolean defaultService = false;
    private Short position;

}
