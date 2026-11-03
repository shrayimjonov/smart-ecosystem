package uz.uzkassa.silen.integration.asilbelgi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @snippet
 * {
 *   "isSpdn": false,
 *   "productionDate": "2023-09-18T19:00:00Z",
 *   "sntins": [
 *     "010536756723004821n=ZGZD:\u001d93SjEr",
 *     "0105367567230048218vsKpn<\u001d93aHZo"
 *   ],
 *   "usageType": "PRINTED",
 *   "withReserve": false
 * }
 */
@Getter
@Setter
@NoArgsConstructor
public class ReportInfoJson implements Serializable {

    List<String> sntins;

    String usageType;

    LocalDateTime productionDate;
}
