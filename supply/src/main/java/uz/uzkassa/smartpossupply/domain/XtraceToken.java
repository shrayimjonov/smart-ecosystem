package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;

import java.io.Serial;
import java.time.LocalDateTime;

@Entity
@Table(name = "xtrace_token",
        indexes = {
                @Index(columnList = "company_id", name = "xtrace_token_company_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class XtraceToken extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 905934083L;

    @NotNull
    @Column(name = "company_id", unique = true, nullable = false)
    Long companyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    Company company;

    @NotNull
    @Column(name = "api_key", nullable = false)
    String apiKey;

    @Column(name = "api_key_id")
    String apiKeyId;

    @NotNull
    @Column(name = "expire_date", nullable = false)
    LocalDateTime expireDate;
}
