package uz.uzkassa.silen.domain;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.waybill.OtherCarOwner;
import uz.uzkassa.silen.dto.waybill.PersonDTO;
import uz.uzkassa.silen.dto.waybill.ProductGroupDTO;
import uz.uzkassa.silen.dto.waybill.RailwayDTO;
import uz.uzkassa.silen.dto.waybill.RoadwayDTO;
import uz.uzkassa.silen.dto.waybill.TruckDTO;
import uz.uzkassa.silen.enumeration.TrailerType;
import uz.uzkassa.silen.enumeration.TransportType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "waybill_transport")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//@SQLDelete(sql = "UPDATE waybill_transport SET deleted = 'true' WHERE id=?")
public class WaybillTransport implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    protected String id;

    @Column(name = "train_carriage_number")
    String trainCarriageNumber;
    @Column(name = "train_direction")
    String trainDirection;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type")
    TransportType transportType;

    @Column(name = "driver_pinfl")
    String driverPinfl;
    @Column(name = "driver_full_name")
    String driverFullName;

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

    @Column(name = "waybill_id", updatable = false, insertable = false)
    String waybillId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waybill_id")
    Waybill waybill;

    @ElementCollection(targetClass = Carriages.class, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Column(name = "carriage", nullable = false)
    @CollectionTable(name = "waybill_carriages", joinColumns = @JoinColumn(name = "waybill_transport_id", referencedColumnName = "id"))
    private List<Carriages> carriages = new ArrayList<>();

    @OneToMany(mappedBy = "waybillTransport", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    List<WaybillProductGroups> productGroups = new ArrayList<>();


    @ElementCollection(targetClass = CarOwner.class, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Column(name = "car_owners", nullable = false)
    @CollectionTable(name = "waybill_car_owners", joinColumns = @JoinColumn(name = "waybill_transport_id", referencedColumnName = "id"))
    private Set<CarOwner> otherCarOwners = new HashSet<>();

//
//    public List<Carriages> getCarriages() {
//        if (carriages != null) {
//            carriages = new ArrayList<>();
//        }
//        return carriages;
//    }

    public List<WaybillProductGroups> getProductGroups() {
        if (productGroups == null) {
            productGroups = new ArrayList<>();
        }
        return productGroups;
    }

    public void addProductGroups(WaybillProductGroups productGroups) {
        productGroups.setWaybillTransport(this);
        this.productGroups.add(productGroups);
    }

    public RoadwayDTO toRoadwayDTO() {
        RoadwayDTO dto = new RoadwayDTO();
        dto.setId(getId());
        dto.setTransportType(TransportType.Roadway);
        if (getDriverPinfl() != null) {
            dto.setDriver(new PersonDTO(getDriverPinfl(), getDriverFullName()));
        }
        if(getTruckRegNo()!=null){
            dto.setTruckDTO(new TruckDTO(getTruckRegNo(), getTruckModel()));
        }
        if(getTrailerRegNo()!=null){
            dto.setTrailer(new TruckDTO(getTrailerRegNo(), getTrailerModel()));
        }
        dto.setTrailerType(getTrailerType());

        List<TruckDTO> dtoList = new ArrayList<>();
        for (Carriages carriage : getCarriages()) {
            dtoList.add(new TruckDTO(carriage.getRegNo(), carriage.getModel()));
        }
        if (CollectionUtils.isNotEmpty(dtoList)) {
            dto.setCarriages(dtoList);
        }

        List<OtherCarOwner> otherCarOwners = new ArrayList<>();
        for (CarOwner carOwner : getOtherCarOwners()) {
            otherCarOwners.add(carOwner.toDTO());
        }
        if (CollectionUtils.isNotEmpty(otherCarOwners)) {
            dto.setOtherCarOwners(otherCarOwners);
        }

        if (getProductGroups() != null && CollectionUtils.isNotEmpty(getProductGroups())) {
            List<ProductGroupDTO> collect = new ArrayList<>();
            for (WaybillProductGroups productGroup : getProductGroups()) {
                ProductGroupDTO productGroupDTO = productGroup.toDTO(TransportType.Roadway);
                collect.add(productGroupDTO);
            }
            dto.setProductGroups(collect);
        }
        return dto;
    }
    public RailwayDTO toRailwayDTO() {
        RailwayDTO dto = new RailwayDTO();
        dto.setId(getId());

        dto.setTrainCarriageNumber(getTrainCarriageNumber());
        dto.setTrainDirection(getTrainDirection());
        dto.setTransportType(TransportType.Railway);
        if (getProductGroups() != null && CollectionUtils.isNotEmpty(getProductGroups())) {
            List<ProductGroupDTO> collect = new ArrayList<>();
            for (WaybillProductGroups productGroup : getProductGroups()) {
                ProductGroupDTO productGroupDTO = productGroup.toDTO(TransportType.Railway);
                collect.add(productGroupDTO);
            }
            dto.setProductGroups(collect);
        }
        return dto;
    }
}
