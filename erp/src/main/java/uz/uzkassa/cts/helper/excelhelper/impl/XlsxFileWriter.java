package uz.uzkassa.cts.helper.excelhelper.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.helper.excelhelper.XlsxField;
import uz.uzkassa.cts.helper.excelhelper.XlsxWriter;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxCompositeField;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSheet;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSingleField;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class XlsxFileWriter implements XlsxWriter {


    @Override
    public <T> void write(List<T> data, ByteArrayOutputStream bos, List<List<String>> columnTitles, Workbook workbook) {

        if (data.isEmpty()) {
            log.error("No data received to write Xls file..");
            return;
        }

        long start = System.currentTimeMillis();

//      setting up the basic styles for the workbook
        Font boldFont = getBoldFont(workbook);
        Font genericFont = getGenericFont(workbook);
        CellStyle headerStyle = getLeftAlignedCellStyle(workbook, boldFont);
        CellStyle currencyStyle = setCurrencyCellStyle(workbook);
        CellStyle centerAlignedStyle = getCenterAlignedCellStyle(workbook);
        CellStyle genericStyle = getLeftAlignedCellStyle(workbook, genericFont);

        try {
// using POJO class metadata for the sheet name
            XlsxSheet annotation = data.getFirst().getClass().getAnnotation(XlsxSheet.class);
            String sheetName = annotation.value();
            Sheet sheet = workbook.createSheet(sheetName);

//  get the metadata for each field of the POJO class into a list
            List<XlsxField> xlsColumnFields = getFieldNamesForClass(data.getFirst().getClass());

            int tempRowNo = 0;
            int recordBeginRowNo;
            int recordEndRowNo = 0;

//    set spreadsheet titles
            Row mainRow;
            Cell columnTitleCell;
            for (List<String> titles : columnTitles) {
                mainRow = sheet.createRow(tempRowNo);
                for (int i = 0; i < titles.size(); i++) {
                    columnTitleCell = mainRow.createCell(i);
                    columnTitleCell.setCellStyle(headerStyle);
                    columnTitleCell.setCellValue(titles.get(i));
                }
                tempRowNo ++;
                recordEndRowNo++;
            }

//    get class of the passed dataset
            Class<?> clazz = data.getFirst().getClass();
//    looping the past dataset
            for (T record : data) {
                tempRowNo = recordEndRowNo;
                recordBeginRowNo = tempRowNo;
                mainRow = sheet.createRow(tempRowNo++);
                boolean isFirstValue;
                boolean isFirstRow;
                boolean isRowNoToDecrease = false;
                Method xlsMethod;
                Object xlsObjValue;
                List<Object> objValueList;

//      get max size of the record if its multiple row
                int maxListSize = getMaxListSize(record, xlsColumnFields, clazz);


//      looping through the fields of the current record
                for (XlsxField xlsColumnField : xlsColumnFields) {
//       writing a single field
                    if (!xlsColumnField.isAnArray() && !xlsColumnField.isComposite()) {
                        writeSingleFieldRow(mainRow, xlsColumnField, clazz, currencyStyle, centerAlignedStyle, genericStyle,
                            record, workbook);

//       overlooking the next field and adjusting the starting row
                        if (isNextColumnAnArray(xlsColumnFields, xlsColumnField, clazz, record)) {
                            isRowNoToDecrease = true;
                            tempRowNo = recordBeginRowNo + 1;
                        }

//       writing an single array field
                    } else if (xlsColumnField.isAnArray() && !xlsColumnField.isComposite()) {
                        xlsMethod = getMethod(clazz, xlsColumnField);
                        xlsObjValue = xlsMethod.invoke(record, (Object[]) null);
                        objValueList = (ArrayList<Object>) xlsObjValue;
                        isFirstValue = true;

//       looping through the items of the single array
                        for (Object objectValue : objValueList) {
                            Row childRow;
                            if (isFirstValue) {
                                childRow = mainRow;
                                writeArrayFieldRow(childRow, xlsColumnField, objectValue, currencyStyle, centerAlignedStyle, genericStyle, workbook);
                                isFirstValue = false;
                            } else if (isRowNoToDecrease) {
                                childRow = getOrCreateNextRow(sheet, tempRowNo++);
                                writeArrayFieldRow(childRow, xlsColumnField, objectValue, currencyStyle, centerAlignedStyle, genericStyle, workbook);
                                isRowNoToDecrease = false;
                            } else {
                                childRow = getOrCreateNextRow(sheet, tempRowNo++);
                                writeArrayFieldRow(childRow, xlsColumnField, objectValue, currencyStyle, centerAlignedStyle, genericStyle, workbook);
                            }
                        }

                        //      overlooking the next field and adjusting the starting row
                        if (isNextColumnAnArray(xlsColumnFields, xlsColumnField, clazz, record)) {
                            isRowNoToDecrease = true;
                            tempRowNo = recordBeginRowNo + 1;
                        }

//      writing a composite array field
                    } else if (xlsColumnField.isAnArray()) {
                        xlsMethod = getMethod(clazz, xlsColumnField);
                        xlsObjValue = xlsMethod.invoke(record, (Object[]) null);
                        objValueList = (List<Object>) xlsObjValue;
                        isFirstRow = true;

//       looping through the items of the composite array
                        for (Object objectValue : objValueList) {
                            Row childRow;
                            List<XlsxField> xlsCompositeColumnFields = getFieldNamesForClass(objectValue.getClass());
                            if (isFirstRow) {
                                childRow = mainRow;
                                for (XlsxField xlsCompositeColumnField : xlsCompositeColumnFields) {
                                    writeCompositeFieldRow(objectValue, xlsCompositeColumnField, childRow, currencyStyle, centerAlignedStyle, genericStyle, workbook);
                                }
                                isFirstRow = false;
                            } else if (isRowNoToDecrease) {
                                childRow = getOrCreateNextRow(sheet, tempRowNo++);
                                for (XlsxField xlsCompositeColumnField : xlsCompositeColumnFields) {
                                    writeCompositeFieldRow(objectValue, xlsCompositeColumnField, childRow, currencyStyle, centerAlignedStyle, genericStyle, workbook);
                                }
                                isRowNoToDecrease = false;
                            } else {
                                childRow = getOrCreateNextRow(sheet, tempRowNo++);
                                for (XlsxField xlsCompositeColumnField : xlsCompositeColumnFields) {
                                    writeCompositeFieldRow(objectValue, xlsCompositeColumnField, childRow, currencyStyle, centerAlignedStyle, genericStyle, workbook);
                                }
                            }
                        }

//       overlooking the next field and adjusting the starting row
                        if (isNextColumnAnArray(xlsColumnFields, xlsColumnField, clazz, record)) {
                            isRowNoToDecrease = true;
                            tempRowNo = recordBeginRowNo + 1;
                        }
                    }
                }

//      adjusting the ending row number for the current record
                recordEndRowNo = maxListSize + recordBeginRowNo;
            }

// auto sizing the columns of the whole sheet
            autoSizeColumns(sheet, xlsColumnFields.size());
            workbook.write(bos);
            log.info("Xls file generated in [{}] seconds", processTime(start));
        } catch (Exception e) {
            log.info("Xls file write failed", e);
        }
    }

    @Override
    public <T> void write(List<T> data, ByteArrayOutputStream bos, Workbook workbook) {

        try {
            List<List<String>> columnNames = getColumnNames(data.getFirst().getClass());
            write(data, bos, columnNames, workbook);
        } catch (Exception e) {
            log.info("Xls file write failed", e);
        }
    }

    private void writeCompositeFieldRow(Object objectValue, XlsxField xlsCompositeColumnField, Row childRow, CellStyle currencyStyle, CellStyle centerAlignedStyle, CellStyle genericStyle, Workbook workbook) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method nestedCompositeXlsMethod = getMethod(objectValue.getClass(), xlsCompositeColumnField);
        Object nestedCompositeValue = nestedCompositeXlsMethod.invoke(objectValue, (Object[]) null);
        Cell compositeNewCell = childRow.createCell(xlsCompositeColumnField.getCellIndex());
        setCellValue(compositeNewCell, nestedCompositeValue, currencyStyle, centerAlignedStyle, genericStyle, workbook);
    }

    private void writeArrayFieldRow(Row childRow, XlsxField xlsColumnField, Object objectValue,
                                    CellStyle currencyStyle, CellStyle centerAlignedStyle, CellStyle genericStyle, Workbook workbook) {
        Cell newCell = childRow.createCell(xlsColumnField.getCellIndex());
        setCellValue(newCell, objectValue, currencyStyle, centerAlignedStyle, genericStyle, workbook);
    }

    private <T> void writeSingleFieldRow(Row mainRow, XlsxField xlsColumnField, Class<?> clazz, CellStyle currencyStyle, CellStyle centerAlignedStyle, CellStyle genericStyle, T record, Workbook workbook) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Cell newCell = mainRow.createCell(xlsColumnField.getCellIndex());
        Method xlsMethod = getMethod(clazz, xlsColumnField);
        Object xlsObjValue = xlsMethod.invoke(record, (Object[]) null);
        setCellValue(newCell, xlsObjValue, currencyStyle, centerAlignedStyle, genericStyle, workbook);
    }

    private <T> boolean isNextColumnAnArray(List<XlsxField> xlsColumnFields, XlsxField xlsColumnField, Class<?> clazz, T record)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        XlsxField nextXlsColumnField;
        int fieldsSize = xlsColumnFields.size();
        Method nestedXlsMethod;
        Object nestedObjValue;
        List<Object> nestedObjValueList;
        if (xlsColumnFields.indexOf(xlsColumnField) < (fieldsSize - 1)) {
            nextXlsColumnField = xlsColumnFields.get(xlsColumnFields.indexOf(xlsColumnField) + 1);
            if (nextXlsColumnField.isAnArray()) {
                nestedXlsMethod = getMethod(clazz, nextXlsColumnField);
                nestedObjValue = nestedXlsMethod.invoke(record, (Object[]) null);
                nestedObjValueList = (List<Object>) nestedObjValue;
                return nestedObjValueList.size() > 1;
            }
        }
        return xlsColumnFields.indexOf(xlsColumnField) == (fieldsSize - 1);

    }

    private void setCellValue(Cell cell, Object objValue, CellStyle currencyStyle, CellStyle centerAlignedStyle,
                              CellStyle genericStyle, Workbook workbook) {
        Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
        if (objValue != null) {
            switch (objValue) {
                case String cellValue -> {
                    cell.setCellStyle(genericStyle);
                    if (cellValue.contains("https://") || cellValue.contains("http://")) {
                        link.setAddress(cellValue);
                        cell.setCellValue(cellValue);
                        cell.setHyperlink(link);
                    } else {
                        cell.setCellValue(cellValue);
                    }
                }
                case Long l -> cell.setCellValue(l);
                case BigInteger bigInteger -> cell.setCellValue(bigInteger.longValue());
                case BigDecimal bigDecimal -> {
                    double cellValue = bigDecimal.doubleValue();
                    cell.setCellStyle(currencyStyle);
                    cell.setCellValue(cellValue);
                }
                case Integer i -> cell.setCellValue(i);
                case Double cellValue -> {
                    cell.setCellStyle(currencyStyle);
                    cell.setCellValue(cellValue);
                }
                case Boolean b -> {
                    cell.setCellStyle(centerAlignedStyle);
                    if (objValue.equals(true)) {
                        cell.setCellValue(1);
                    } else {
                        cell.setCellValue(0);
                    }
                }
                default -> {
                }
            }
        }
    }


    private static List<List<String>> getColumnNames(Class<?> clazz) {
        List<List<String>> columnNames = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        List<String> col = getColumnNames(0, clazz);
        columnNames.add(col);
        for (Field field : fields) {
            XlsxCompositeField xlsCompositeField = field.getAnnotation(XlsxCompositeField.class);
            if (xlsCompositeField != null) {
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Class<?> compositeFieldClass = (Class<?>) listType.getActualTypeArguments()[0];
                columnNames.add(getColumnNames(xlsCompositeField.from(), compositeFieldClass));
            }
        }
        return columnNames;
    }

    private static List<String> getColumnNames(int begin, Class<?> clazz) {
        List<String> columnNames = new ArrayList<>();
        for (int i = 0; i < begin; i++) {
            columnNames.add("");
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            XlsxCompositeField xlsCompositeField = field.getAnnotation(XlsxCompositeField.class);
            if (xlsCompositeField != null) {
                columnNames.add(xlsCompositeField.columnName());
                int diff = xlsCompositeField.to() - xlsCompositeField.from();
                for (int i = 0; i < diff; i++) {
                    columnNames.add("");
                }
            } else {
                XlsxSingleField xlsField = field.getAnnotation(XlsxSingleField.class);
                columnNames.add(xlsField.columnName());
            }
        }
        return columnNames;
    }

    private static List<XlsxField> getFieldNamesForClass(Class<?> clazz) {
        List<XlsxField> xlsColumnFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            XlsxField xlsColumnField = new XlsxField();
            if (Collection.class.isAssignableFrom(field.getType())) {
                xlsColumnField.setAnArray(true);
                XlsxCompositeField xlsCompositeField = field.getAnnotation(XlsxCompositeField.class);
                if (xlsCompositeField != null) {
                    xlsColumnField.setCellIndexFrom(xlsCompositeField.from());
                    xlsColumnField.setCellIndexTo(xlsCompositeField.to());
                    xlsColumnField.setComposite(true);
                } else {
                    XlsxSingleField xlsField = field.getAnnotation(XlsxSingleField.class);
                    xlsColumnField.setCellIndex(xlsField.columnIndex());
                }
            } else {
                XlsxSingleField xlsField = field.getAnnotation(XlsxSingleField.class);
                xlsColumnField.setAnArray(false);
                if (xlsField != null) {
                    xlsColumnField.setCellIndex(xlsField.columnIndex());
                    xlsColumnField.setComposite(false);
                }
            }
            xlsColumnField.setFieldName(field.getName());
            xlsColumnFields.add(xlsColumnField);
        }
        return xlsColumnFields;
    }

    private static String capitalize(String s) {
        if (s.isEmpty())
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    private <T> int getMaxListSize(T record, List<XlsxField> xlsColumnFields, Class<?> aClass)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        List<Integer> listSizes = new ArrayList<>();
        for (XlsxField xlsColumnField : xlsColumnFields) {
            if (xlsColumnField.isAnArray()) {
                Method method = getMethod(aClass, xlsColumnField);
                Object value = method.invoke(record, (Object[]) null);
                List<Object> objects = (List<Object>) value;
                if (objects.size() > 1) {
                    listSizes.add(objects.size());
                }
            }
        }

        if (listSizes.isEmpty()) {
            return 1;
        } else {
            return Collections.max(listSizes);
        }

    }

    private Method getMethod(Class<?> clazz, XlsxField xlsColumnField) throws NoSuchMethodException {
        Method method;
        try {
            method = clazz.getMethod("get" + capitalize(xlsColumnField.getFieldName()));
        } catch (NoSuchMethodException nme) {
            method = clazz.getMethod(xlsColumnField.getFieldName());
        }

        return method;
    }

    private long processTime(long start) {
        return (System.currentTimeMillis() - start) / 1000;
    }

    private void autoSizeColumns(Sheet sheet, int noOfColumns) {
        for (int i = 0; i < noOfColumns; i++) {
            sheet.autoSizeColumn((short) i);
        }
    }

    private Row getOrCreateNextRow(Sheet sheet, int rowNo) {
        Row row;
        if (sheet.getRow(rowNo) != null) {
            row = sheet.getRow(rowNo);
        } else {
            row = sheet.createRow(rowNo);
        }
        return row;
    }

    private CellStyle setCurrencyCellStyle(Workbook workbook) {
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setWrapText(true);
        DataFormat df = workbook.createDataFormat();
        currencyStyle.setDataFormat(df.getFormat("#0.00"));
        return currencyStyle;
    }

    private Font getBoldFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) (10 * 20));
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        return font;
    }

    private Font getGenericFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeight((short) (10 * 20));
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        return font;
    }

    private CellStyle getCenterAlignedCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        return cellStyle;
    }

    private CellStyle getLeftAlignedCellStyle(Workbook workbook, Font font) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        return cellStyle;
    }
}
