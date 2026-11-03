package uz.uzkassa.smartpossupply.dto.waybill;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransportCheckRequestDTO implements Serializable {

    List<String> tinOrPinfls;

    List<String> regNumbers;

    String date;
}