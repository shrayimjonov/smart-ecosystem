package uz.uzkassa.smartpos.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.catalog.ColorGroupImage;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ColorGroupImageDTO implements Serializable {
    private Long id;
    private Long fileId;
    private String url;
    private boolean main;

    public ColorGroupImageDTO(ColorGroupImage entity) {
        this.id = entity.getId();
        this.fileId = entity.getFileId();
        if (entity.getFile() != null) {
            this.url = entity.getFile().getUrl();
        }
        this.main = entity.isMain();
    }
}
