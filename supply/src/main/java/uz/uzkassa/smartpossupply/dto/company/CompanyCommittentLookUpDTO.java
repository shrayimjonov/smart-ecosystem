package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyCommittentLookUpDTO implements Serializable {

    Long id;

    CompanyBaseDTO committent;

    @SuppressWarnings("unused")
    public CompanyCommittentLookUpDTO(Long id, Long committentId, String committentName, String committentTin) {
        this.id = id;
        this.committent = new CompanyBaseDTO(committentId, committentName, committentTin);
    }
}
