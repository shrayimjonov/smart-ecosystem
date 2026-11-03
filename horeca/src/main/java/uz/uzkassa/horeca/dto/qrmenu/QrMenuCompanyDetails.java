package uz.uzkassa.horeca.dto.qrmenu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.address.AddressDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QrMenuCompanyDetails {

    String name;

    String brand;

    String contactPhone;

    String contactName;

    String wifiPassword;

    AddressDTO address;

    FileDTO logo;

    FileDTO background;
}
