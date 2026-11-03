package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.filter.TransferFilter;
import uz.uzkassa.smartpos.dto.markings.MarkingCheckDTO;
import uz.uzkassa.smartpos.dto.markings.MarkingRequestDTO;
import uz.uzkassa.smartpos.dto.warehouse.TransferDTO;
import uz.uzkassa.smartpos.service.TransferService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/warehouse")
@Secured({SMARTPOS_ADMIN, OWNER, SUPPORT, SALES_MANAGER, BRANCH_ADMIN})
public class TransferResource {

    private final TransferService transferService;

    public TransferResource(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * {@code POST  /transfer/send} : Create new transfer of products.
     *
     * @param transferDTO the Transfer to send.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the transfer
     */
    @PostMapping("/transfer/send")
    public ResponseEntity<TransferDTO> sendTransfer(@Valid @RequestBody TransferDTO transferDTO) {
        TransferDTO result = transferService.sendTransfer(transferDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * {@code POST  /transfer/request} : request transfer from different branch.
     *
     * @param transferDTO the new Transfer to request.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the transfer
     */
    @PostMapping("/transfer/request")
    public ResponseEntity<TransferDTO> requestTransfer(@Valid @RequestBody TransferDTO transferDTO) {
        TransferDTO result = transferService.requestTransfer(transferDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * {@code POST  /transfer/approve} : approve transfer from different branch.
     *
     * @param transferDTO the Transfer to approve.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the transfer
     */
    @PutMapping("/transfer/approve")
    public ResponseEntity<TransferDTO> approveTransfer(@Valid @RequestBody TransferDTO transferDTO) {
        TransferDTO result = transferService.approveTransfer(transferDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code POST  /transfer/cancel} : request transfer from different branch.
     *
     * @param transferDTO the Transfer to cancel.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the transfer
     */
    @PutMapping("/transfer/cancel")
    public ResponseEntity<TransferDTO> cancelTransfer(@Valid @RequestBody TransferDTO transferDTO) {
        TransferDTO result = transferService.cancelTransfer(transferDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code GET  /transfer} : get all transfers for current branch.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of incoming products in body.
     */
    @GetMapping("/transfer")
    public Page<TransferDTO> getAllTransfers(@ParameterObject TransferFilter filter) {
        return transferService.getAllTransfers(filter);
    }

    /**
     * {@code GET  /transfer/:id} : get all transfers for current branch.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the transfer in body.
     */
    @GetMapping("/transfer/{id}")
    public ResponseEntity<TransferDTO> getTransferById(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.getTransfer(id));
    }

    @GetMapping("/transfer/{id}/excel")
    public ResponseEntity<ByteArrayResource> getTransferExcel(@PathVariable Long id) {
        ByteArrayResource resource = transferService.getTransferExcel(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + URLEncoder.encode("transfers", StandardCharsets.UTF_8) + ".xlsx")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @PostMapping("/transfer/markings/check")
    public MarkingCheckDTO getMarkingsForTransfer(@RequestBody MarkingRequestDTO dto) {
        return transferService.getMarkingsForTransfer(dto);
    }
}
