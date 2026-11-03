package uz.uzkassa.silen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Getter
@Setter
public class BaseAuditDTO implements Serializable {
    protected boolean deleted;
    @JsonProperty(access = READ_ONLY, index = -100)
    private String createdBy;
    @JsonProperty(access = READ_ONLY, index = -101)
    private LocalDateTime createdDate;
    @JsonProperty(access = READ_ONLY, index = -102)
    private String lastModifiedBy;
    @JsonProperty(access = READ_ONLY, index = -103)
    private LocalDateTime lastModifiedDate;
}
