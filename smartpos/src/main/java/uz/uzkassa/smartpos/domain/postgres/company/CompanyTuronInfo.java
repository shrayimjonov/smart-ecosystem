package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import uz.uzkassa.smartpos.security.TokenUtils;

import java.sql.Types;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class CompanyTuronInfo {
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;
    @JdbcTypeCode(Types.LONGVARCHAR)
    @NotNull
    private String token;
    @NotNull
    private LocalDateTime receivedDate;
    @NotNull
    private LocalDateTime expirationDate;

    public CompanyTuronInfo(Long id, String token) {
        this.id = id;
        this.token = token;
        this.receivedDate = LocalDateTime.now();
        this.expirationDate = TokenUtils.getExpirationTime(token);
    }

    public void setToken(@NotNull String token) {
        this.token = token;
        this.receivedDate = LocalDateTime.now();
        this.expirationDate = TokenUtils.getExpirationTime(token);
    }
}
