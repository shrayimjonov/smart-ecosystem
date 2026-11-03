package uz.uzkassa.silen.integration.asilbelgi.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @snippet
 * {
 *   "reportId": "0995dfb3-135c-4513-478f-697d0f95ced0",
 *   "status": "SENT",
 *   "type": "UTILISATION",
 *   "pg": "alcohol",
 *   "createdTimestamp": "2023-09-27T06:46:49.761Z",
 *   "updatedTimestamp": "2023-09-27T06:46:58.435Z",
 *   "isAutoReport": false,
 *   "json": {
 *     "isSpdn": false,
 *     "productionDate": "2023-09-18T19:00:00Z",
 *     "sntins": [
 *       "010536756723004821n=ZGZD:\u001d93SjEr",
 *       "0105367567230048218vsKpn<\u001d93aHZo"
 *     ],
 *     "usageType": "PRINTED",
 *     "withReserve": false
 *   }
 * }
 */
@Getter
@Setter
@NoArgsConstructor
public class ReportInfo implements Serializable {

    String reportId;
    String status;
    ReportInfoJson json;
}
