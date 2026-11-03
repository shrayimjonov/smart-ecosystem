package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MxikPackageResponseDTO implements Serializable {
    private List<PackageDTO> data;
}
