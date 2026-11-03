package uz.uzkassa.smartpossupply.domain.factura;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;

import java.io.Serial;

@Entity
@Table(name = "factura_host")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FacturaHost extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 95644257451L;

    @Column(name = "host", unique = true)
    String host;

    @Column(name = "active", columnDefinition = "boolean default false")
    boolean active = false;
}
