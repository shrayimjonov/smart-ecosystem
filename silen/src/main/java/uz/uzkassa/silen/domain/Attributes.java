package uz.uzkassa.silen.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.AttributesListDTO;
import uz.uzkassa.silen.enumeration.ProductType;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "attributes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "postfix", "prefix", "deleted"}),
        @UniqueConstraint(columnNames = "sorter")
    }, indexes = {@Index(name = "attributes_parent_id_idx", columnList = "parent_id")})
@SQLDelete(sql = "UPDATE attributes SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLRestriction(" deleted=false ")
public class Attributes extends BaseIdentityEntity {

    @Column(name = "parent_id")
    private Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Attributes parent;

    @OneToMany(mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    Set<Attributes> children = new HashSet<>();

    @Column(name = "name")
    private String name;

    @Column(name = "prefix", length = 15)
    private String prefix;

    @Column(name = "postfix", length = 15)
    private String postfix;

    @Column(name = "visible", columnDefinition = "boolean default false")
    private boolean visible;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "additional")
    private HashMap<String, Object> additional;

    @OneToMany(mappedBy = "attribute", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<ProductAttributes> products = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "attributes_product_type", joinColumns = @JoinColumn(name = "attribute_id"))
    @Enumerated(EnumType.STRING)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ProductType> productTypes = new HashSet<>();

    @Column(name = "sorter")
    private int sorter;

    public AttributesListDTO toDto() {
        AttributesListDTO dto = new AttributesListDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setParentId(getParentId());
        dto.setPrefix(getPrefix());
        dto.setPostfix(getPostfix());
        dto.setVisible(isVisible());
        dto.setSorter(getSorter());
        dto.setProductTypes(getProductTypes());
        dto.setProductTypeNames(getProductTypes().stream().map(ProductType::toSelectItem).collect(Collectors.toSet()));
        dto.setAdditional(getAdditional());
        if (CollectionUtils.isNotEmpty(getChildren())) {
            dto.setChildren(getChildren().stream().map(Attributes::toDto).collect(Collectors.toSet()));
        }
        return dto;
    }
}
