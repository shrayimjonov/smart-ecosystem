package uz.uzkassa.smartpossupply.domain.company;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;


@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "CommittentLookUpMapper",
                        classes = @ConstructorResult(
                                targetClass = CompanyCommittentLookUpDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "committentId", type = Long.class),
                                        @ColumnResult(name = "committentName", type = String.class),
                                        @ColumnResult(name = "committentTin", type = String.class),
                                }
                        )
                )
        }
)

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "company_committent", uniqueConstraints = {@UniqueConstraint(columnNames = {"committent_id", "company_id"})},
        indexes = {
                @Index(columnList = "company_id", name = "company_committent_company_id_idx"),
                @Index(columnList = "committent_id", name = "company_committent_committent_id_idx")
        })
public class CompanyCommittent extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 893283432L;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "token")
    String token;

    @Column(name = "committent_id")
    Long committentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "committent_id", updatable = false, insertable = false)
    Company committent;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "token_time")
    LocalDateTime tokenTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompanyCommittent that = (CompanyCommittent) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(committentId, that.committentId) &&
                Objects.equals(companyId, that.companyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, committentId, companyId);
    }

    public CompanyCommittentLookUpDTO toLookUpDTO() {
        return new CompanyCommittentLookUpDTO(getId(), getCommittentId(), getCommittent().getName(), getCommittent().getTin());
    }
}
