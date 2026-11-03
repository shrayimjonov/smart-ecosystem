package uz.uzkassa.smartpos.dto.factura;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ImportMarkStatus;
import uz.uzkassa.smartpos.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class InvoiceListDto {
    private Long id;
    private LocalDateTime createdDate;
    private String facturaId;
    private String number;
    private LocalDate invoiceDate;
    private Status status;
    private String notes;
    private BigDecimal total;
    private ImportMarkStatus importMarkStatus;
    private String sellerTin;
    private String sellerName;
    private String buyerTin;
    private String buyerName;


    public DashboardDTO toDashboardDTO(boolean revert) {
        DashboardDTO dto = new DashboardDTO();
        dto.setId(getId());
        dto.setCreatedDate(getCreatedDate());
        dto.setFacturaId(getFacturaId());
        dto.setNumber(getNumber());
        dto.setInvoiceDate(getInvoiceDate());
        dto.setStatus(getStatus());
        if (getStatus() != null) {
            dto.setStatusName(getStatus().getRuText());
        }
        dto.setNotes(getNotes());
        dto.setTotal(getTotal());
        dto.setImportMarkStatus(getImportMarkStatus());
        if (revert) {
            dto.setContrAgentTIN(getSellerTin());
            dto.setContrAgentName(getSellerName());
        } else {
            dto.setContrAgentTIN(getBuyerTin());
            dto.setContrAgentName(getBuyerName());
        }
        return dto;
    }

}
