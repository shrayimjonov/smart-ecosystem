package uz.uzkassa.cts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;

@Getter
@Setter
@Table(name = "device_license")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceLicense extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 2757181539L;

    @NotNull
    @Column(name = "device_serial_number", unique = true, nullable = false)
    private String deviceSerialNumber;
}
