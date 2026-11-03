package uz.uzkassa.smartpossupply.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonNameDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteCustomerStatisticsItemDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@SqlResultSetMappings({
        @SqlResultSetMapping(name = "CommonDTOMapper",
                classes = {
                        @ConstructorResult(targetClass = CommonDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class)
                                })
                }),

        @SqlResultSetMapping(name = "CommonCodeDTOMapper",
                classes = {
                        @ConstructorResult(targetClass = CommonCodeDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "code", type = String.class)
                                })
                }),

        @SqlResultSetMapping(name = "CommonNameDTOMapper",
                classes = {
                        @ConstructorResult(targetClass = CommonNameDTO.class,
                                columns = {
                                        @ColumnResult(name = "name", type = String.class)
                                })
                }),

        @SqlResultSetMapping(name = "PlanRouteCustomerStatisticsItemDTOMapper",
                classes = {
                        @ConstructorResult(targetClass = PlanRouteCustomerStatisticsItemDTO.class,
                                columns = {
                                        @ColumnResult(name = "dataType", type = String.class),
                                        @ColumnResult(name = "total", type = Long.class)
                                })
                }),
        @SqlResultSetMapping(name = "StockProductQtyDetailListMapper",
                classes = {
                        @ConstructorResult(targetClass = StockProductQtyDetailListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "stockNumber", type = String.class),
                                        @ColumnResult(name = "stockDate", type = LocalDateTime.class),
                                        @ColumnResult(name = "stockCompletedDate", type = LocalDateTime.class),
                                        @ColumnResult(name = "qty", type = BigDecimal.class),
                                        @ColumnResult(name = "companyId", type = Long.class),
                                        @ColumnResult(name = "companyName", type = String.class),
                                        @ColumnResult(name = "companyTin", type = String.class)
                                })
                }),
})
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Slf4j
public abstract class AbstractAuditingEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 99986L;

    @CreatedBy
    @Column(name = "creator_id")
    protected Long creatorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", updatable = false, insertable = false)
    protected User creator;

    @LastModifiedBy
    @Column(name = "updater_id")
    protected Long updaterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updater_id", updatable = false, insertable = false)
    protected User updater;

    @CreatedDate
    @Column(name = "created_date")
    @JsonIgnore
    protected LocalDateTime createdDate = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "updated_date")
    @JsonIgnore
    protected LocalDateTime updatedDate = LocalDateTime.now();

    public boolean isNew() {
        return id == null;
    }
}
