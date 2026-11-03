package uz.uzkassa.silen.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadMarksCsv extends AbstractProductExcel {

    public Map<String, EnumDTO> startRead(final MultipartFile multipartFile) {
        final Map<String, EnumDTO> codes = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8))) {
            String mark = bufferedReader.readLine();
/*            if (StringUtils.isNotEmpty(line)) {
                readFirstLine(line, codes);
                line = bufferedReader.readLine();
            }*/
            while (StringUtils.isNotEmpty(mark)) {
                final String clearMark = Utils.clearPrintCode(mark);
                if (Utils.isMark(mark)) {
                    codes.put(clearMark, new EnumDTO(mark, ""));
                } else {
                    codes.put(clearMark, new EnumDTO(mark, "Ошибка! Код не соответствует стандартному формату" + mark));
                }
                mark = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return codes;
    }
/*
    public void readFirstLine(String line, Map<String, String> codes) throws IOException {
        String mark = String.valueOf(line.subSequence(1, line.length()));
        checkError(mark, codes);
    }

    public void checkError(String mark, Map<String, String> codes) {
        if (Utils.isMark(mark)) {
            codes.put(Utils.clearPrintCode(mark), "");
        } else {
            codes.put(mark, "Код не соответствует стандартному формату" + Utils.clearPrintCode(mark));
        }
    }*/

    @Override
    public void writeBody() {
    }
}
