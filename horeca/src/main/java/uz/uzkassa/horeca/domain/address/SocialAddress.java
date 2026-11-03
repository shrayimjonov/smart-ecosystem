package uz.uzkassa.horeca.domain.address;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.socialaddress.SocialAddressDetailDTO;
import uz.uzkassa.horeca.enums.SocialAddressType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "social_address")
@Entity
@SQLDelete(sql = "UPDATE social_address SET deleted='true' WHERE id=?")
public class SocialAddress extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4368L;

    @NotNull
    @Column(name = "page_uri", nullable = false)
    String pageUri;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    SocialAddressType type;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    public SocialAddressDetailDTO toDTO() {
        SocialAddressDetailDTO socialPageDTO = new SocialAddressDetailDTO();
        socialPageDTO.setId(getId());
        socialPageDTO.setPageUri(getPageUri());
        socialPageDTO.setType(getType());
        return socialPageDTO;
    }
}
