package uz.uzkassa.silen.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.silen.dto.*;
import uz.uzkassa.silen.dto.dashboard.DashboardStatsDTO;
import uz.uzkassa.silen.dto.dashboard.OrganizationsDTO;
import uz.uzkassa.silen.dto.dashboard.StatsDTO;
import uz.uzkassa.silen.dto.invoice.DashboardInfoDTO;
import uz.uzkassa.silen.dto.marking.AggregationStatsDTO;
import uz.uzkassa.silen.dto.marking.BatchAggregationListDTO;
import uz.uzkassa.silen.dto.report.GeneralDTO;
import uz.uzkassa.silen.dto.warehouse.StockStatsDTO;
import uz.uzkassa.silen.dto.warehouse.WarehouseOperationStatsDTO;
import uz.uzkassa.silen.dto.warehouse.WarehouseSelectItem;
import uz.uzkassa.silen.dto.warehouse.WarehouseStatsDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base abstract class for entities which will hold definitions for created, last modified, created by,
 * last modified by attributes.
 */
@SqlResultSetMappings({
        @SqlResultSetMapping(name = "SelectItemMapper",
                classes = {
                        @ConstructorResult(targetClass = SelectItem.class,
                                columns = {
                                        @ColumnResult(name = "id", type = String.class),
                                        @ColumnResult(name = "name", type = String.class)
                                })
                }),
        @SqlResultSetMapping(name = "TopSelectItemMapper",
                classes = {
                        @ConstructorResult(targetClass = TopSelectItem.class,
                                columns = {
                                        @ColumnResult(name = "productId", type = String.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "quantity", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "TopShippedSelectItemMapper",
                classes = {
                        @ConstructorResult(targetClass = DashboardStatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "organizationId", type = String.class),
                                        @ColumnResult(name = "productId", type = String.class),
                                        @ColumnResult(name = "unit", type = Integer.class),
                                        @ColumnResult(name = "quantity", type = BigDecimal.class)
                                })
                }),

        @SqlResultSetMapping(name = "DashboardStatsMapper",
                classes = {
                        @ConstructorResult(targetClass = DashboardStatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "productId", type = String.class),
                                        @ColumnResult(name = "unit", type = String.class),
                                        @ColumnResult(name = "quantity", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "SelectItemDescriptionMapper",
                classes = {
                        @ConstructorResult(targetClass = SelectItem.class,
                                columns = {
                                        @ColumnResult(name = "id", type = String.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "description", type = String.class)
                                })
                }),
        @SqlResultSetMapping(name = "ProductSelectCountItemMapper",
                classes = {
                        @ConstructorResult(targetClass = ProductSelectCountItem.class,
                                columns = {
                                        @ColumnResult(name = "type", type = String.class),
                                        @ColumnResult(name = "counter", type = Integer.class)
                                })
                }),
        @SqlResultSetMapping(name = "OrganizationsMapper",
                classes = {
                        @ConstructorResult(targetClass = OrganizationsDTO.class,
                                columns = {
                                        @ColumnResult(name = "type", type = String.class),
                                        @ColumnResult(name = "total", type = BigInteger.class)

                                })
                }),
        @SqlResultSetMapping(name = "StatsMapper",
                classes = {
                        @ConstructorResult(targetClass = StatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "type", type = String.class),
                                        @ColumnResult(name = "total", type = BigDecimal.class)

                                })
                }),
        @SqlResultSetMapping(name = "GeneralMapper",
                classes = {
                        @ConstructorResult(targetClass = GeneralDTO.class,
                                columns = {
                                        @ColumnResult(name = "organizationId", type = String.class),
                                        @ColumnResult(name = "organizationName", type = String.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "productionAmount", type = BigDecimal.class),
                                        @ColumnResult(name = "realizedAmount", type = BigDecimal.class),
                                        @ColumnResult(name = "balanceAmount", type = BigDecimal.class)

                                })
                }),
        @SqlResultSetMapping(name = "DashboardInfoMapper",
                classes = {
                        @ConstructorResult(targetClass = DashboardInfoDTO.class,
                                columns = {
                                        @ColumnResult(name = "pendingCount", type = BigInteger.class),
                                        @ColumnResult(name = "acceptedCount", type = BigInteger.class),
                                        @ColumnResult(name = "rejectedCount", type = BigInteger.class),
                                        @ColumnResult(name = "cancelledCount", type = BigInteger.class),
                                        @ColumnResult(name = "agentAcceptedCount", type = BigInteger.class),
                                        @ColumnResult(name = "agentRejectedCount", type = BigInteger.class)
                                })
                }),
        @SqlResultSetMapping(name = "BatchAggregationMapper",
                classes = {
                        @ConstructorResult(targetClass = BatchAggregationListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = String.class),
                                        @ColumnResult(name = "number", type = String.class),
                                        @ColumnResult(name = "gcp", type = String.class),
                                        @ColumnResult(name = "unit", type = Integer.class),
                                        @ColumnResult(name = "quantity", type = Integer.class),
                                        @ColumnResult(name = "unUsed", type = Integer.class),
                                        @ColumnResult(name = "description", type = String.class),
                                        @ColumnResult(name = "createdDate", type = LocalDateTime.class)
                                })
                }),
        @SqlResultSetMapping(name = "AggregationStatsMapper",
                classes = {
                        @ConstructorResult(targetClass = AggregationStatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "palletQuantity", type = BigInteger.class),
                                        @ColumnResult(name = "boxQuantity", type = BigInteger.class),
                                        @ColumnResult(name = "blockQuantity", type = BigInteger.class),
                                        @ColumnResult(name = "bottleQuantity", type = BigInteger.class)
                                })
                }),
        @SqlResultSetMapping(name = "ShipmentStatsMapper",
                classes = {
                        @ConstructorResult(targetClass = ShipmentStatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "status", type = String.class),
                                        @ColumnResult(name = "qty", type = Integer.class)

                                })
                }),
        @SqlResultSetMapping(name = "StockMapper",
                classes = {
                        @ConstructorResult(targetClass = StockStatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "productId", type = String.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "amount", type = BigDecimal.class),
                                        @ColumnResult(name = "priceWithVat", type = BigDecimal.class),
                                        @ColumnResult(name = "sumWithVat", type = BigDecimal.class),
                                        @ColumnResult(name = "sumVat", type = BigDecimal.class),
                                        @ColumnResult(name = "sumPriceWithoutVat", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "StockStatsMapper",
                classes = {
                        @ConstructorResult(targetClass = StockStatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "amount", type = BigDecimal.class),
                                        @ColumnResult(name = "sumWithVat", type = BigDecimal.class),
                                        @ColumnResult(name = "sumVat", type = BigDecimal.class),
                                        @ColumnResult(name = "sumPriceWithoutVat", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "WarehouseOrgStatsMapper",
                classes = {
                        @ConstructorResult(targetClass = WarehouseStatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "organizationId", type = String.class),
                                        @ColumnResult(name = "organizationName", type = String.class),
                                        @ColumnResult(name = "organizationType", type = String.class),
                                        @ColumnResult(name = "productType", type = String.class),
                                        @ColumnResult(name = "production", type = BigDecimal.class),
                                        @ColumnResult(name = "shipment", type = BigDecimal.class),
//                    @ColumnResult(name = "losses", type = BigDecimal.class),
//                    @ColumnResult(name = "income", type = BigDecimal.class),
                                        @ColumnResult(name = "balance", type = BigDecimal.class)
                                }),
                }),
        @SqlResultSetMapping(name = "WarehouseStatsMapper",
                classes = {
                        @ConstructorResult(targetClass = WarehouseStatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "productType", type = String.class),
                                        @ColumnResult(name = "production", type = BigDecimal.class),
                                        @ColumnResult(name = "shipment", type = BigDecimal.class),
//                    @ColumnResult(name = "losses", type = BigDecimal.class),
//                    @ColumnResult(name = "income", type = BigDecimal.class),
                                        @ColumnResult(name = "balance", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "WarehouseSelectItemMapper",
                classes = {
                        @ConstructorResult(targetClass = WarehouseSelectItem.class,
                                columns = {
                                        @ColumnResult(name = "id", type = String.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "operation", type = String.class),
                                        @ColumnResult(name = "amount", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "ShipmentHistoryMapper",
                classes = {
                        @ConstructorResult(targetClass = ShipmentHistoryDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = String.class),
                                        @ColumnResult(name = "shipmentDate", type = LocalDateTime.class),
                                        @ColumnResult(name = "shipmentNumber", type = String.class),
                                        @ColumnResult(name = "intNumber", type = Integer.class),
                                        @ColumnResult(name = "invoiceId", type = Long.class),
                                        @ColumnResult(name = "invoiceNumber", type = String.class),
                                        @ColumnResult(name = "total", type = BigDecimal.class),
                                        @ColumnResult(name = "listInvoiceId", type = Long.class),
                                        @ColumnResult(name = "facturaId", type = String.class)
                                })
                }),
        @SqlResultSetMapping(name = "WarehouseOperationStatsMapper",
                classes = {
                        @ConstructorResult(targetClass = WarehouseOperationStatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "amount", type = BigDecimal.class),
                                        @ColumnResult(name = "block", type = BigDecimal.class),
                                        @ColumnResult(name = "box", type = BigDecimal.class),
                                        @ColumnResult(name = "pallet", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "DailyOperationsStatsMapper",
                classes = {
                        @ConstructorResult(targetClass = DailyOperationsStats.class,
                                columns = {
                                        @ColumnResult(name = "production", type = BigDecimal.class),
                                        @ColumnResult(name = "shipment", type = BigDecimal.class),
                                        @ColumnResult(name = "returned", type = BigDecimal.class),
                                        @ColumnResult(name = "dropout", type = BigDecimal.class)
                                })
                }),

        @SqlResultSetMapping(name = "DailyOperationsMaterialStatsMapper",
                classes = {
                        @ConstructorResult(targetClass = DailyOperationsMaterialStats.class,
                                columns = {
                                        @ColumnResult(name = "productId", type = String.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "barcode", type = String.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),
                                        @ColumnResult(name = "serialId", type = String.class),
                                        @ColumnResult(name = "serialNumber", type = String.class),
                                        @ColumnResult(name = "serialProductionDate", type = LocalDate.class),
                                        @ColumnResult(name = "productionQty", type = BigDecimal.class),
                                        @ColumnResult(name = "shippedQty", type = BigDecimal.class),
                                        @ColumnResult(name = "returnedQty", type = BigDecimal.class),
                                        @ColumnResult(name = "dropoutQty", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "AggregationQuantityMapper",
                classes = {
                        @ConstructorResult(targetClass = AggregationSum.class,
                                columns = {
                                        @ColumnResult(name = "organizationId", type = String.class),
                                        @ColumnResult(name = "productId", type = String.class),
                                        @ColumnResult(name = "serialId", type = String.class),
                                        @ColumnResult(name = "quantity", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "AttributeValueMapper",
                classes = {
                        @ConstructorResult(targetClass = AttributeValueDTO.class,
                                columns = {
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "prefix", type = String.class),
                                        @ColumnResult(name = "value", type = String.class),
                                        @ColumnResult(name = "postfix", type = String.class)
                                })
                }),
        @SqlResultSetMapping(name = "MaterialReportMapper",
                classes = {
                        @ConstructorResult(targetClass = MaterialReportDTO.class,
                                columns = {
                                        @ColumnResult(name = "productId", type = String.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "price", type = BigDecimal.class),
                                        @ColumnResult(name = "productionBottle", type = BigDecimal.class),
                                        @ColumnResult(name = "shippedBottle", type = BigDecimal.class),
                                        @ColumnResult(name = "productionBox", type = BigDecimal.class),
                                        @ColumnResult(name = "shippedBox", type = BigDecimal.class),
                                        @ColumnResult(name = "productionPallet", type = BigDecimal.class),
                                        @ColumnResult(name = "shippedPallet", type = BigDecimal.class)
                                })
                }),
        @SqlResultSetMapping(name = "SerialNumberListDTOMapper",
                classes = {
                        @ConstructorResult(targetClass = SerialNumberListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = String.class),
                                        @ColumnResult(name = "serialNumber", type = String.class),
                                        @ColumnResult(name = "productId", type = String.class),
                                        @ColumnResult(name = "productName", type = String.class),
                                        @ColumnResult(name = "barcode", type = String.class),
                                        @ColumnResult(name = "startQuantity", type = Integer.class),
                                        @ColumnResult(name = "quantity", type = Integer.class),
                                        @ColumnResult(name = "forExpertise", type = Integer.class),
                                        @ColumnResult(name = "createdDate", type = LocalDateTime.class),
                                        @ColumnResult(name = "productionDate", type = LocalDate.class),
                                        @ColumnResult(name = "lastModifiedDate", type = LocalDateTime.class),
                                        @ColumnResult(name = "status", type = String.class),
                                        @ColumnResult(name = "description", type = String.class)
                                })
                }),
        @SqlResultSetMapping(name = "DropoutStatsMapper",
                classes = {
                        @ConstructorResult(targetClass = DropoutStatsDTO.class,
                                columns = {
                                        @ColumnResult(name = "boxCount", type = Long.class),
                                        @ColumnResult(name = "blockCount", type = Long.class),
                                        @ColumnResult(name = "bottleCount", type = Long.class)
                                })
                }),
        @SqlResultSetMapping(name = "PermissionsCountMapper",
                classes = {
                        @ConstructorResult(targetClass = PermissionsCountDTO.class,
                                columns = {
                                        @ColumnResult(name = "orgId", type = String.class),
                                        @ColumnResult(name = "qty", type = Integer.class)
                                })
                })

})
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    @JsonIgnore
    private LocalDateTime createdDate = LocalDateTime.now();

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    @JsonIgnore
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    protected SelectItem toSelectItem() {
        return null;
    }

    protected boolean isNew() {
        return true;
    }

    <T extends BaseAuditDTO> T setAuditValues(T baseDTO) {
        baseDTO.setCreatedBy(getCreatedBy());
        baseDTO.setCreatedDate(getCreatedDate());
        baseDTO.setLastModifiedBy(getLastModifiedBy());
        baseDTO.setLastModifiedDate(getLastModifiedDate());
        return baseDTO;
    }
}


