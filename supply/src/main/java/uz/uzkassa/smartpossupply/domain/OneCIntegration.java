package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 25.01.2024
 */
@Entity
@Table(name = "one_c_integration", indexes = {
        @Index(columnList = "company_id", name = "one_c_integration_company_id_idx"),
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OneCIntegration extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 95644257451L;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "url", unique = true)
    String url;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;
}
