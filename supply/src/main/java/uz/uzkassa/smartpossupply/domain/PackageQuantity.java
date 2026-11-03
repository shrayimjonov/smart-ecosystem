package uz.uzkassa.smartpossupply.domain;


import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PackageType;

@Entity
@Table(name = "package_quantity")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PackageQuantity {

    @EmbeddedId
    EntityCompositeId id;

    @Column(name = "quantity_in_package")
    Integer quantityInPackage;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "package_type")
    @Enumerated(value = EnumType.STRING)
    PackageType packageType;

}
