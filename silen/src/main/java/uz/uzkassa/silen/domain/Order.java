package uz.uzkassa.silen.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.marking.OrderDTO;
import uz.uzkassa.silen.enumeration.CreateMethodType;
import uz.uzkassa.silen.enumeration.ReleaseMethodType;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Order.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "orders",
indexes = {
    @Index(columnList = "last_modified_date",name = "orders_last_modified_date_idx"),
    @Index(columnList = "number",name = "orders_number_idx"),
    @Index(columnList = "organization_id",name = "orders_organization_id_idx"),
})
@SQLDelete(sql = "UPDATE orders SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Order extends BaseOrganizationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1123123L;

    @Column(name = "number")
    private String number;

    @Column(name = "int_number", columnDefinition = "int4 default 0", nullable = false)
    private int intNumber;

    @Column(name = "turonId")
    private String turonId;

    @Column(name = "expected_complete_timestamp")
    private Integer expectedCompleteTimestamp;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<OrderProduct> products = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "release_method_type")
    ReleaseMethodType releaseMethodType = ReleaseMethodType.PRODUCTION;

    @Enumerated(EnumType.STRING)
    @Column(name = "create_method_type")
    CreateMethodType createMethodType;

    @Column(name = "description")
    private String description;

    public Set<OrderProduct> getProducts() {
        if (products == null) {
            products = new HashSet<>();
        }
        return products;
    }

    public void addProduct(OrderProduct product) {
        product.setOrder(this);
        products.add(product);
    }

    public OrderDTO toDto() {
        OrderDTO dto = new OrderDTO();
//        dto.setCreateMethodType(getCreateMethodType());
        dto.setReleaseMethodType(getReleaseMethodType());
//        dto.setProducts(getProducts().stream().map(OrderProduct::toDto).collect(Collectors.toSet()));
        return dto;
    }
}
