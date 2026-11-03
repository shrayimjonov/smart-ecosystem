package uz.uzkassa.silen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import uz.uzkassa.silen.config.DbConstants;

import java.sql.Types;
import java.time.LocalDateTime;

/**
 * A Factory.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "true_token")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AsilBelgiToken {

    @Id
    @Column(length = 14, nullable = false, unique = true, updatable = false)
    private String tin;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "token")
    private String token;

    @Column(name = "token_date")
    private LocalDateTime tokenDate;
}



