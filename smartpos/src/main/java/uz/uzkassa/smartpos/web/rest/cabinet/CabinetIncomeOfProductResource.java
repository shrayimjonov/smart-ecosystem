package uz.uzkassa.smartpos.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.dto.warehouse.v2.*;
import uz.uzkassa.smartpos.filter.IncomeOfProductFilterV2;
import uz.uzkassa.smartpos.service.IncomeOfProductServiceExcel;
import uz.uzkassa.smartpos.service.IncomeOfProductServiceV2;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/cabinet/income-of-products")
@RequiredArgsConstructor
public class CabinetIncomeOfProductResource {
    private final IncomeOfProductServiceV2 incomeOfProductService;
    private final IncomeOfProductServiceExcel incomeOfProductServiceExcel;

    @PostMapping
    public Long create(@Valid @RequestBody IncomeOfProductCreateDto<IncomeOfProductDetailCreateDto> dto) {
        return incomeOfProductService.create(dto, null);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody IncomeOfProductCreateDto<IncomeOfProductDetailCreateDto> dto) {
        incomeOfProductService.update(id, dto);
    }

    @GetMapping
    public Page<IncomeOfProductListDto> findAllByFilter(@ParameterObject IncomeOfProductFilterV2 filter) {
        return incomeOfProductService.findAllByFilter(filter);
    }

    @GetMapping("/{id}")
    public IncomeOfProductDtoV2 findById(@PathVariable Long id) {
        return incomeOfProductService.getById(id);
    }

    @PutMapping("/{id}/approve")
    public void approve(@PathVariable Long id) {
        incomeOfProductService.accept(id);
    }

    @PutMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        incomeOfProductService.cancel(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        incomeOfProductService.delete(id);
    }

    @PostMapping("/from-invoice")
    public Long createFromInvoice(@Valid @RequestBody IncomeOfProductCreateDto<IncomeDetailCreateFromInvoiceDto> dto) {
        return incomeOfProductService.createFromInvoice(dto);
    }


    @GetMapping(value = "/excel/template")
    public ResponseEntity<Resource> getIncomeOfProductTemplate() {
        return ServerUtils.getResponseEntity(incomeOfProductServiceExcel.getTemplate(), "income_of_product");
    }

    @PostMapping(value = "/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> incomeOfProductImport(@RequestParam Long branchId, @RequestParam("file") MultipartFile file) throws IOException {
        return ServerUtils.getResponseEntity(incomeOfProductServiceExcel.importFromExcel(branchId, file), "income_of_product");
    }

}
