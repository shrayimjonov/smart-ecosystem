package uz.uzkassa.cts.helper.excelhelper;


import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.util.List;
public interface XlsxWriter {
    <T> void write(List<T> data, ByteArrayOutputStream bos, List<List<String>> columnTitles, Workbook workbook);
    <T> void write(List<T> data, ByteArrayOutputStream bos, Workbook workbook);
}
