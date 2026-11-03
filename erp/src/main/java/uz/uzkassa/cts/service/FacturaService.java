package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.factura.FacturaBankDTO;
import uz.uzkassa.cts.dto.factura.FacturaDTO;

import java.util.List;

public interface FacturaService {
    void sendOneTimeOrderInvoice(FacturaDTO facturaDTO);

    void sendServiceOrderInvoice(FacturaDTO facturaDTO);

    void sendActOfTransmissionInvoice(FacturaDTO facturaDTO);

    void sendTradeInInvoice(FacturaDTO facturaDTO);

    void sendRentInvoice(FacturaDTO facturaDTO);

    void sendSaleInvoice(FacturaDTO facturaDTO);

    List<FacturaBankDTO> getBanks(String language);
}
