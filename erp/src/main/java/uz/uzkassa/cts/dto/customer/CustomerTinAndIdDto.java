package uz.uzkassa.cts.dto.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerTinAndIdDto {
    private Long id;
    private String tin;
    private String oldTin;
    private String name;
    private String companyName;

    public CustomerTinAndIdDto(Long id, String tin, String oldTin, String name, String companyName) {
        this.id = id;
        this.tin = tin;
        this.oldTin = oldTin;
        this.name = name;
        this.companyName = companyName;
    }
}
