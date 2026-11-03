package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.factura.FacturaCheckStatusDataDto;
import uz.uzkassa.cts.dto.factura.FacturaDTO;
import uz.uzkassa.cts.dto.factura.FacturaIncomingDto;
import uz.uzkassa.cts.dto.factura.GetIncomingInvoiceRequest;
import uz.uzkassa.cts.dto.xfile.request.XfileChangeInvoiceStatusRequestDto;

public interface XfileService {

    String authenticate();

    String sendInvoice(FacturaDTO dto);

    FacturaIncomingDto getDataByTin(GetIncomingInvoiceRequest request);

    Long getTotalDataByTin(GetIncomingInvoiceRequest request);

    void changeInvoiceStatus(XfileChangeInvoiceStatusRequestDto dto);

    FacturaCheckStatusDataDto checkFacturaStatus(String facturaId);


}
