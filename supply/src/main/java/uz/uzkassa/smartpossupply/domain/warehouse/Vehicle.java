package uz.uzkassa.smartpossupply.domain.warehouse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleLookupDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.TrailerType;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "vehicle",
    indexes = {
        @Index(columnList = "company_id", name = "vehicle_company_id_idx"),
        @Index(columnList = "deleted", name = "vehicle_deleted_idx")
    }
)
@SQLDelete(sql = "UPDATE vehicle SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class Vehicle extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 6849846533L;

    @Column(name = "name")
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "reg_no", nullable = false)
    String regNo;

    @Column(name = "model", nullable = false)
    String model;

    @Column(name = "trailer_reg_no")
    String trailerRegNo;

    @Column(name = "trailer_model")
    String trailerModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "trailer_type")
    TrailerType trailerType;

    @Column(name = "transport_company_tin")
    String transportCompanyTin;

    @Column(name = "transport_company_name")
    String transportCompanyName;

    @Column(name = "owner_full_name", nullable = false)
    String ownerFullName;

    @Column(name = "owner_pinfl", nullable = false)
    String ownerPinfl;


    public VehicleLookupDTO toLookupDTO() {
        VehicleLookupDTO dto = new VehicleLookupDTO();
        dto.setId(this.getId());
        dto.setName(this.getName());
        dto.setRegNo(this.getRegNo());
        dto.setModel(this.getModel());
        dto.setTrailerRegNo(this.getTrailerRegNo());
        dto.setTrailerModel(this.getTrailerModel());
        dto.setTransportCompanyTin(this.getTransportCompanyTin());
        dto.setTransportCompanyName(this.getTransportCompanyName());
        dto.setOwnerFullName(this.getOwnerFullName());
        dto.setOwnerPinfl(this.getOwnerPinfl());
        return dto;
    }
}