package uz.uzkassa.smartpossupply.dto.importreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 3.03.2023 15:37
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportReportItemDetailDTO {

    Long id;

    String errorMessage;

    List<String> values = new ArrayList<>();

}
