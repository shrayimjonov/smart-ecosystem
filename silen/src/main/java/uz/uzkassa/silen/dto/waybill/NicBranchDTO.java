package uz.uzkassa.silen.dto.waybill;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NicBranchDTO implements Serializable {
    String id;

    String tin;

    String name;

    String branchName;

    String branchNum;

    Boolean isDeleted;

    //region
    String ns10Code;

    String ns10Name;
    //district
    String ns11Code;

    String ns11Name;

    Double latitude;

    Double longitude;

    String address;

    String directorTin;

    String directorName;

}
