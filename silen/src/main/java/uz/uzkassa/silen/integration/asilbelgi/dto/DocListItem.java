package uz.uzkassa.silen.integration.asilbelgi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.AsilBelgiDocStatus;

/**
 * {
 *             "number": "2f156309-c055-4e8e-b6e5-0d651c245117",
 *             "docDate": "2024-08-05T00:00:00.000Z",
 *             "receivedAt": "2024-08-05T10:37:14.086Z",
 *             "type": "UZ_ESF",
 *             "status": "CHECKED_OK",
 *             "senderName": "\"AFSAR COMPANY LTD\" MCHJ",
 *             "receiverName": "\"OLMALIQSHAROBSAVDO\" MCHJ",
 *             "invoiceNumber": "66b0a6e4681c08f2d051aa54",
 *             "total": 0
 *         },
 */
@Getter
@Setter
@NoArgsConstructor
public class DocListItem {

    private String number;

    private AsilBelgiDocStatus status;

    private String invoiceNumber;
}
