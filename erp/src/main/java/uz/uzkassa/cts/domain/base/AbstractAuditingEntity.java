package uz.uzkassa.cts.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.customer.CustomerBranchListMapDTO;

import java.io.Serial;
import java.time.LocalDateTime;

@SqlResultSetMappings({
        @SqlResultSetMapping(name = "CustomerBranchListMapMapper",
                classes = {
                        @ConstructorResult(targetClass = CustomerBranchListMapDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "brand", type = String.class),
                                        @ColumnResult(name = "phone", type = String.class),
                                        @ColumnResult(name = "longitude", type = String.class),
                                        @ColumnResult(name = "latitude", type = String.class),
                                        @ColumnResult(name = "customer", type = String.class),
                                        @ColumnResult(name = "tin", type = String.class),
                                        @ColumnResult(name = "activityType", type = String.class)
                                })
                })
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 93409L;

    @CreatedBy
    @Column(name = "creator_id")
    Long creatorId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", updatable = false, insertable = false)
    User creator;

    @LastModifiedBy
    @Column(name = "last_modifier_id")
    Long lastModifierId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_modifier_id", updatable = false, insertable = false)
    User lastModifier;

    @CreatedDate
    @Column(name = "created_date")
    @JsonIgnore
    LocalDateTime createdDate = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "last_modified_date")
    @JsonIgnore
    LocalDateTime lastModifiedDate = LocalDateTime.now();
}
