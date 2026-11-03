package uz.uzkassa.silen.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@NoArgsConstructor
public class OrganizationSelectItem implements Serializable {
    private String id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tin;

    public OrganizationSelectItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public OrganizationSelectItem(String id, String name, String tin) {
        this.id = id;
        this.name = name;
        this.tin = tin;
    }
}
