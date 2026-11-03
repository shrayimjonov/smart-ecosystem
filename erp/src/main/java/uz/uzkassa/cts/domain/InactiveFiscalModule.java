package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;

@Entity
@Table(name = "inactive_fiscal_module",
        indexes = {
                @Index(columnList = "fiscal_module_id", name = "inactive_fiscal_module_fm_id_idx")
        })
@Getter
@Setter
public class InactiveFiscalModule extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 2908003437L;

    @Column(name = "fiscal_module_id")
    Long fiscalModuleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiscal_module_id", updatable = false, insertable = false)
    FiscalModule fiscalModule;
}
