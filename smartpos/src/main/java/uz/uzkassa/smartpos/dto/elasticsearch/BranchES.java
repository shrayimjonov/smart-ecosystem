package uz.uzkassa.smartpos.dto.elasticsearch;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BranchES implements Serializable {
    private Long id;
    private Long ctoId;

    private String name;

    private String address;

    private String description;

    private boolean deleted = false;

    private String reasonForDeletion;

    private String phone;

    private boolean registered;

    private boolean fiscal;

    private Long regionId;
    private RegionES region;

    private Long cityId;
    private CityES city;

    private Long companyId;
    private CompanyES company;
    private Long activityTypeId;
}
