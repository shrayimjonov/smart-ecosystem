package uz.uzkassa.silen.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhysicalPersonDTO implements Serializable {


//    Long ns10Code;

//    Long ns11Code;

    String tin;

    String fullName;

    String name;

//    String mfo;

//    String account;

    String address;

//    String oked;

//    String director;

//    String directorTin;

//    String accountant;

}
