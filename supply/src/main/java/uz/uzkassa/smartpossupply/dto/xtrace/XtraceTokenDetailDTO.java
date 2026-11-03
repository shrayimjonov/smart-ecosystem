package uz.uzkassa.smartpossupply.dto.xtrace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class XtraceTokenDetailDTO implements Serializable {
    Long companyId;

    String apiKey;

    String apiKeyId;

    LocalDateTime expireDate;
}
