package uz.uzkassa.silen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import uz.uzkassa.silen.dto.BaseUuidDTO;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    protected String id;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    private boolean deleted = false;

    <T extends BaseUuidDTO> T setAuditValues(T baseDTO) {
        baseDTO = super.setAuditValues(baseDTO);
        baseDTO.setId(getId());
        baseDTO.setDeleted(isDeleted());
        return baseDTO;
    }
}
