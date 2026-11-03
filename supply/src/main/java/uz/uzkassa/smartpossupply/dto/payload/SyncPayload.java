package uz.uzkassa.smartpossupply.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpossupply.enums.AppType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SyncPayload extends Payload {

    AppType appType;

    public SyncPayload(Long id, AppType appType) {
        super(id);
        this.appType = appType;
    }
}
