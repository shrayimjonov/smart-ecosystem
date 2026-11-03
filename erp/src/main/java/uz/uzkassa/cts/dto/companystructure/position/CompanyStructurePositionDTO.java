package uz.uzkassa.cts.dto.companystructure.position;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyStructurePositionDTO extends CompanyCommonDTO implements Serializable {

    List<PositionStructureDTO> positions = new ArrayList<>();

    public void addPosition(PositionStructureDTO position) {
        this.positions.add(position);
    }
}

