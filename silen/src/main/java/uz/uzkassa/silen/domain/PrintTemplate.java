package uz.uzkassa.silen.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.PrintTemplateDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.enumeration.CodeType;

@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "print_template",
    indexes = {
        @Index(columnList = "product_id", name = "print_template_product_id_idx")
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NoArgsConstructor
public class PrintTemplate extends BaseOrganizationEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "product_id")
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "code_type")
    private CodeType codeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "template", columnDefinition = "TEXT")
    private String template;

    public PrintTemplateDTO toDTO(boolean isAdmin) {
        PrintTemplateDTO dto = new PrintTemplateDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setProductId(getProductId());
        if (getProduct() != null) {
            dto.setProduct(getProduct().toCommonDTO());
        }
        dto.setTemplate(getTemplate());
        dto.setCodeType(getCodeType());
        if (isAdmin && getOrganization() != null) {
            dto.setOrganization(getOrganization().toSelectItem());
        }
        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(getId(), getName());
    }
}
