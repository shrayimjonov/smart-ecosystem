package uz.uzkassa.smartpos.integration.apay;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActivityTypeDTO implements Serializable {
    String code;
    ActivityTypeDTO parent;
}
