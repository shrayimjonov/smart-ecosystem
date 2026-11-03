package uz.uzkassa.smartpos.dto.launcher;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ActiveVersionDTO implements Serializable {
    private String versionName;
    private Long versionCode;
}
