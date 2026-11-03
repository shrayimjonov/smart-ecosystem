package uz.uzkassa.smartpossupply.domain.company;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_aggregation_info",
        indexes = {
                @Index(columnList = "company_id", name = "aggregation_info_company_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyAggregationInfo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 822229138L;

    @Column(name = "company_id", unique = true)
    Long companyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    Company company;

    @Column(name = "gcp", length = 9)
    String gcp;

    @Column(name = "oms_id")
    String omsId;

    @Column(name = "contact_person")
    String contactPerson;

    @Column(name = "turon_token")
    String turonToken;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "asl_belgi_token")
    String aslBelgiToken;

    @Column(name = "asl_belgi_token_date")
    LocalDateTime aslBelgiTokenDate;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "asl_belgi_xtrace_token")
    String aslBelgiXtraceToken;

    @Column(name = "asl_belgi_xtrace_token_expire_date")
    LocalDateTime aslBelgiXtraceTokenExpireDate;

    @Column(name = "factory_id")
    String factoryId;

    @Column(name = "factory_country")
    String factoryCountry;
}
