package uz.uzkassa.smartpos.domain.postgres.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.product.AttributeDTO;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "attribute")
public class Attribute implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key")
    private String key;
    @Column(name = "code")
    private String code;
    @Column(name = "name_ru")
    private String nameRu;
    @Column(name = "name_en")
    private String nameEn;
    @Column(name = "name_uz")
    private String nameUz;
    @Column(name = "s_order", columnDefinition = "int4 default 0")
    private int sOrder;

    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    public AttributeDTO toDTO(final LanguageEnum locale) {
        final AttributeDTO dto = new AttributeDTO();
        dto.setId(getId());
        dto.setKey(getKey());
        dto.setCode(getCode());
        dto.setNameEn(getNameEn());
        dto.setNameRu(getNameRu());
        dto.setNameUz(getNameUz());
        dto.setName(getName(locale));
        dto.setSOrder(getSOrder());
        return dto;
    }

    public AttributeDTO toAdminDTO() {
        final AttributeDTO dto = new AttributeDTO();
        dto.setId(getId());
        dto.setKey(getKey());
        dto.setCode(getCode());
        dto.setNameEn(getNameEn());
        dto.setNameRu(getNameRu());
        dto.setNameUz(getNameUz());
        dto.setSOrder(getSOrder());
        return dto;
    }

    private String getName(final LanguageEnum locale){
        if (locale == LanguageEnum.uz) {
            return getNameUz();
        }
        return getNameRu();
    }
}
