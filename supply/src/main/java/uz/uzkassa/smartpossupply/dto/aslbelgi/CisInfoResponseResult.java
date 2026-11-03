package uz.uzkassa.smartpossupply.dto.aslbelgi;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CisInfoResponseResult implements Serializable {

    List<CisInfoResponse> cisInfoResponses;
}
