package uz.uzkassa.silen.dto.mq;

import lombok.*;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ExportReportPayload {

    Object filter;

    Long exportReportId;
}
