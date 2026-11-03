package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;

@Entity
@Table(name = "company_model", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"model_id", "company_id"})
}, indexes = {
        @Index(columnList = "model_id", name = "company_model_model_id_idx"),
        @Index(columnList = "company_id", name = "company_model_company_id_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyModel extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 8275845700L;

    @NotNull
    @Column(name = "model_id", nullable = false)
    Long modelId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", updatable = false, insertable = false)
    DeviceModel model;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

}
