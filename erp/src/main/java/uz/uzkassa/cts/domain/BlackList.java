package uz.uzkassa.cts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

@Entity
@Table(name = "black_list",
        indexes = {
                @Index(columnList = "tin", name = "black_list_tin_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE black_list SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class BlackList extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 5745021813L;

    @NotNull
    @Size(min = 9, max = 14)
    @Column(name = "tin", unique = true)
    String tin;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "description")
    String description;
}
