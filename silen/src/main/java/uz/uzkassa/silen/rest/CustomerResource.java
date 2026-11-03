package uz.uzkassa.silen.rest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.dto.CustomerProductDTO;
import uz.uzkassa.silen.dto.filter.CustomerFilter;
import uz.uzkassa.silen.dto.invoice.FacturaCustomer;
import uz.uzkassa.silen.dto.warehouse.CustomerCommonDTO;
import uz.uzkassa.silen.dto.warehouse.CustomerDTO;
import uz.uzkassa.silen.dto.warehouse.FinancialDiscountDTO;
import uz.uzkassa.silen.excel.ImportCustomerExcel;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.pdf.DiscountPdfHandler;
import uz.uzkassa.silen.service.CustomerService;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Customer}.
 */
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Validated
public class CustomerResource {
    private final CustomerService customerService;
    private final DiscountPdfHandler discountPdfHandler;
    private final ImportCustomerExcel importCustomerExcel;

    @PostMapping
    public ResponseEntity<CustomerDTO> create(@RequestBody @Valid CustomerDTO customerDTO) {
        if (customerDTO.getId() != null) {
            throw new BadRequestException("A new customer cannot already have an ID");
        }
        return ResponseEntity.ok(customerService.save(customerDTO));
    }

    @PutMapping
    public ResponseEntity<CustomerDTO> update(@RequestBody @Valid CustomerDTO customerDTO) {
        if (customerDTO.getId() == null) {
            throw new BadRequestException("ID required!");
        }
        return ResponseEntity.ok(customerService.save(customerDTO));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> getAll(@ModelAttribute @Valid CustomerFilter filter) {
        return ResponseEntity.ok(customerService.findAll(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findOne(id));
    }

    @GetMapping("/by-tin/{tin}")
    public ResponseEntity<FacturaCustomer> getCustomerByTin(@PathVariable String tin) {
        return ResponseEntity.ok(customerService.getCustomer(tin, false));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items")
    public ResponseEntity<List<CustomerCommonDTO>> getItems(@ModelAttribute CustomerFilter filter) {
        return ResponseEntity.ok(customerService.getItems(filter));
    }

    @PutMapping("/discount")
    public ResponseEntity<CustomerProductDTO> setDiscount(@RequestBody @Valid CustomerProductDTO customerDTO) {
        return ResponseEntity.ok(customerService.updateDiscount(customerDTO));
    }

    @PutMapping("/discount-all")
    public ResponseEntity<Void> setDiscountAll(@RequestBody @Valid CustomerProductDTO customerDTO) {
        customerService.updateDiscountAll(customerDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/financial-discount")
    public ResponseEntity<CustomerDTO> setFinanceDiscount(@RequestBody @Valid FinancialDiscountDTO customerDTO) {
        customerService.updateFinancialDiscount(customerDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/products")
    public ResponseEntity<Page<CustomerProductDTO>> getAllProducts(@ModelAttribute CustomerFilter filter) {
        return ResponseEntity.ok(customerService.findAllProducts(filter));
    }

    @GetMapping("/migrate_products")
    public ResponseEntity<Void> migrateProducts() {
        customerService.migrateProducts();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/discount/excel/{customerId}")
    public void discountProductsExcel(@PathVariable("customerId") Long customerId, HttpServletResponse response) throws Exception {
        customerService.discountProductsExcel(customerId);
    }

    @GetMapping("/discount/pdf/{customerId}")
    public ResponseEntity<Void> discountProductsPdf(@PathVariable("customerId") Long customerId, HttpServletResponse response) throws Exception {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=discount.pdf");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        discountPdfHandler.export(customerId, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Integer> importCustomers(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(importCustomerExcel.startImport(file));
    }

    @PutMapping("/sync/{customerId}")
    public ResponseEntity<CustomerDTO> syncFromNic(@PathVariable("customerId") Long customerId) {
        return ResponseEntity.ok(customerService.syncFromNIC(customerId));
    }

}
