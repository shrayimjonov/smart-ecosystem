package uz.uzkassa.silen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.waybill.WaybillRouteDTO;
import uz.uzkassa.silen.enumeration.TransportType;

@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "waybill_route")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WaybillRoute extends BaseEntity {

    @Column(name = "region_id")
    Integer regionId;
    @Column(name = "region_name")
    String regionName;

    @Column(name = "district_id")
    Integer districtCode;
    @Column(name = "district_name")
    String districtName;

    @Column(name = "mahalla_id")
    Integer mahallaId;
    @Column(name = "mahalla_name")
    String mahallaName;

    @Column(name = "address")
    String address;
    @Column(name = "longitude")
    Double longitude;
    @Column(name = "latitude")
    Double latitude;

    @Column(name = "tin")
    String tin;
    @Column(name = "trasnsport_type")
    TransportType transportType;

    public WaybillRouteDTO toDTO() {
        WaybillRouteDTO dto = new WaybillRouteDTO();
        dto.setId(getId());
        dto.setTin(getTin());
        dto.setRegionId(getRegionId());
        dto.setRegionName(getRegionName());
        dto.setDistrictCode(getDistrictCode());
        dto.setDistrictName(getDistrictName());
        dto.setMahallaId(getMahallaId());
        dto.setMahallaName(getMahallaName());
        dto.setAddress(getAddress());
        dto.setLongitude(getLongitude());
        dto.setLatitude(getLatitude());
        dto.setTransportType(getTransportType());
        return dto;
    }
}
