package uz.uzkassa.silen.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadMarksExcel extends AbstractProductExcel {

    public Map<String, EnumDTO> startRead(final MultipartFile multipartFile) throws IOException {
        final Map<String, EnumDTO> codes = new HashMap<>();
        try (InputStream file = multipartFile.getInputStream()) {
            ZipSecureFile.setMinInflateRatio(0);
            //Create Workbook instance holding reference to .xls file
            final XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(file);

            //Get first/desired sheet from the workbook
            final XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            for (Row row : sheet) {
                //For each row, iterate through all the columns
                final Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    final String mark = convertToString(cellIterator.next());
                    final String clearMark = Utils.clearPrintCode(mark);
                    if (Utils.isMark(mark)) {
                        codes.put(clearMark, new EnumDTO(mark, ""));
                    } else {
                        codes.put(clearMark, new EnumDTO(mark, "Ошибка! Код не соответствует стандартному формату" + mark));
                    }
                }
            }
            return codes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return codes;
    }

    @Override
    public void writeBody() {
    }
}
