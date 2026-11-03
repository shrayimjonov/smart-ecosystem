package uz.uzkassa.horeca.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import uz.uzkassa.horeca.enums.terminal.EpsMethodType;
import uz.uzkassa.horeca.enums.terminal.EpsServiceStatusEnum;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "eps_service")
public class EpsServiceDomain {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name_uz")
    String nameUz;

    @Column(name = "name_cyrillic")
    String nameCyrillic;

    @Column(name = "name_ru")
    String nameRu;

    @NotBlank
    private String type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EpsServiceStatusEnum status = EpsServiceStatusEnum.ACTIVE;

    private String logoId;
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    private EpsMethodType qrMethod;

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
    @Column(name = "for_cabinet", columnDefinition = "boolean default true")
    private boolean forCabinet=true;
    private boolean defaultService = false;
    private Short position;

    public String getName() {
        return switch (HeaderUtils.getCurrentRequestLanguageEnum()) {
            case uz -> nameUz;
            case cyrillic -> nameCyrillic;
            default -> nameRu;
        };
    }
}
