package uz.uzkassa.horeca.dto.branch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.address.AddressDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BranchDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 33289238L;

    AddressDTO address;

    Long companyId;

    @JsonProperty("isMain")
    boolean isMain;

    Long ownerId;

    Long ctsId;
}
