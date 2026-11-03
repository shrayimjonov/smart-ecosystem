package uz.uzkassa.smartpossupply.domain.waybill;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillCarOwnerDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillPersonDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillRoadWayTransportDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillTruckDTO;
import uz.uzkassa.smartpossupply.enums.TrailerType;
import uz.uzkassa.smartpossupply.enums.TransportType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "waybill_transport",
        indexes = {
                @Index(columnList = "waybill_id", name = "waybill_transport_waybill_id_idx")
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WaybillTransport extends SimpleEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type")
    TransportType transportType;

    @Column(name = "driver_id")
    Long driverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", updatable = false, insertable = false)
    User driver;

    @Column(name = "driver_pinfl")
    String driverPinfl;

    @Column(name = "driver_name")
    String driverName;

    @Column(name = "truck_reg_no")
    String truckRegNo;

    @Column(name = "truck_model")
    String truckModel;

    @Column(name = "trailer_reg_no")
    String trailerRegNo;

    @Column(name = "trailer_model")
    String trailerModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "trailer_type")
    TrailerType trailerType;

    //railWay properties
    @Column(name = "train_carriage_number")
    String trainCarriageNumber;

    @Column(name = "train_direction")
    String trainDirection;

    @NotNull
    @Column(name = "waybill_id", nullable = false)
    Long waybillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waybill_id", updatable = false, insertable = false)
    Waybill waybill;

    @ElementCollection(targetClass = Carriages.class)
    @Column(name = "carriage", nullable = false)
    @CollectionTable(name = "waybill_carriages", joinColumns = @JoinColumn(name = "waybill_transport_id", referencedColumnName = "id"))
    private List<Carriages> carriages = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "other_car_owners", columnDefinition = "jsonb")
    private List<WaybillCarOwnerDTO> otherCarOwners = new ArrayList<>();

    @OneToMany(mappedBy = "waybillTransport", cascade = CascadeType.ALL)
    @SQLRestriction("deleted = false")
    List<WaybillProductGroup> productGroups = new ArrayList<>();

    public void addCarriages(Carriages carriages) {
        this.carriages.add(carriages);
    }

    public WaybillRoadWayTransportDTO toDTO() {
        WaybillRoadWayTransportDTO dto = new WaybillRoadWayTransportDTO();
        dto.setId(getId());
        if (getDriver() != null) {
            dto.setDriver(new WaybillPersonDTO(getDriver().getPinfl(), getDriver().getFullName()));
        } else if (StringUtils.hasText(getDriverPinfl()) && StringUtils.hasText(getDriverName())) {
            dto.setDriver(new WaybillPersonDTO(getDriverPinfl(), getDriverName()));
        }
        if (StringUtils.hasText(getTruckRegNo())) {
            dto.setTruckDTO(new WaybillTruckDTO(getTruckRegNo(), getTruckModel()));
        }
        if (StringUtils.hasText(getTrailerRegNo())) {
            dto.setTrailer(new WaybillTruckDTO(getTrailerRegNo(), getTrailerModel()));
        }
        dto.setTrailerType(getTrailerType());
        dto.setTransportType(getTransportType());
        // rail-way properties
        dto.setTrainDirection(getTrainDirection());
        dto.setTrainCarriageNumber(getTrainCarriageNumber());


        if (!Hibernate.isInitialized(getCarriages())) {
            Hibernate.initialize(getCarriages());
        }
        if (!CollectionUtils.isEmpty(getCarriages())) {
            List<WaybillTruckDTO> dtoList = new ArrayList<>();
            for (Carriages carriage : getCarriages()) {
                dtoList.add(new WaybillTruckDTO(carriage.getRegNo(), carriage.getModel()));
            }
            dto.setCarriages(dtoList);
        }
        if (CollectionUtils.isNotEmpty(getOtherCarOwners())) {
            dto.setOtherCarOwners(getOtherCarOwners());
        }

        if (CollectionUtils.isNotEmpty(getProductGroups())) {
            dto.setProductGroups(getProductGroups().stream().map(WaybillProductGroup::toDTO).toList());
        }
        return dto;
    }
}
