package uz.uzkassa.smartpossupply.domain.factura;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;

import java.io.Serial;

@Entity
@Table(name = "factura_sync_guid", indexes = {
        @Index(columnList = "factura_id", name = "factura_sync_guid_factura_id_idx"),
        @Index(columnList = "used", name = "factura_sync_guid_used_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FacturaGUID extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 9257451L;

    @Size(max = 24)
    @Column(name = "factura_id", length = 24)
    String facturaId;

    @Column(name = "used", columnDefinition = "boolean default false")
    boolean used = false;
}
