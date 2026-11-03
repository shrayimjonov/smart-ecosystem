package uz.uzkassa.smartpossupply.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.03.2024
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductPackageSyncDTO implements Serializable {

    private List<PackageSyncDTO> packages;
}
