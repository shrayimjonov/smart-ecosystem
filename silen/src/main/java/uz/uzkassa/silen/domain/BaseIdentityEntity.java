package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.dto.BaseIdentityDTO;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public class BaseIdentityEntity extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    private boolean deleted = false;

    <T extends BaseIdentityDTO> T setAuditValues(T baseDTO) {
        baseDTO = super.setAuditValues(baseDTO);
        baseDTO.setId(getId());
        baseDTO.setDeleted(isDeleted());
        return baseDTO;
    }
}
