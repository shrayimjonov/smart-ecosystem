package uz.uzkassa.silen.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.domain.Customer;
import uz.uzkassa.silen.repository.pg.CustomerRepository;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImportCustomerExcel extends AbstractProductExcel {

    private final CustomerRepository customerRepository;

    /**
     * headers[String]{
     * "ИНН", "НАЗВАНИЕ ПРОДУКТА", "КОРОТКОЕ НАЗВАНИЕ ПРОДУКТА",
     * "ИКПУ", "ЕД. ИЗМЕРЕНИЯ", "НДС (СТАВКА %)", "ЦЕНА", "GTIN", "ТИП ПРОДУКЦИИ"
     * }
     *
     * @return Integer counts imported products
     */
    public int startImport(MultipartFile multipartFile) {
        int savedRows = 0;

        try (InputStream file = multipartFile.getInputStream()) {
            ZipSecureFile.setMinInflateRatio(0);
            //Create Workbook instance holding reference to .xls file
            XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(file);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            final String organizationId = SecurityUtils.getCurrentOrganizationId();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Customer customer = new Customer();
                    //tin
                    if (cellIterator.hasNext()) {
                        customer.setTin(convertToString(cellIterator.next()));
                    }
                    //getName
                    if (cellIterator.hasNext()) {
                        customer.setName(convertToString(cellIterator.next()));
                    }
                    //organizationId
                    customer.setOrganizationId(organizationId);

                    boolean customerExists = customerRepository.existsCustomerByOrganizationIdAndTinAndDeletedIsFalse(organizationId, customer.getTin());
                    log.info("Customer {} exists {}", customer.getName(), customerExists);
                    if (!customerExists) {
                        customerRepository.save(customer);
                        savedRows++;
                    }
                }
            }

        } catch (IOException e) {
            log.error("Starting import failed", e);
        }
        return savedRows;
    }

    @Override
    public void writeBody() {
    }
}
