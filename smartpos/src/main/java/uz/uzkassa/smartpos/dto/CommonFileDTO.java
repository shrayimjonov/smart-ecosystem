package uz.uzkassa.smartpos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;

import java.io.Serializable;

/**
 * DTO for {@link FileUpload}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonFileDTO implements Serializable {
    private Long id;
    private String url;
}
