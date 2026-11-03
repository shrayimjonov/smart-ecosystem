package uz.uzkassa.silen.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.VerticalAlignment;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class TableBuilder {
    private final PdfPTable table;

    public TableBuilder(PdfPTable table) {
        this.table = new PdfPTable(table);
    }

    public TableBuilder(int numberColumns) {
        this.table = new PdfPTable(numberColumns);
    }

    public static PdfPCell simpleCell(String cellContent, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Paragraph(cellContent, font));
        return cell;
    }

    public void addRow(Font font, String... pdfCellsContents) {
        for (String pdfCell : pdfCellsContents) {
            table.addCell(simpleCell(pdfCell, font));
        }
    }

    public void addRowWithAlignment(HorizontalAlignment x, VerticalAlignment y, Font font, String... pdfCellsContents) {
        for (String pdfCell : pdfCellsContents) {
            PdfPCell cell = simpleCell(pdfCell, font);
            cell.setVerticalAlignment(y.getId());
            cell.setHorizontalAlignment(x.getId());
            table.addCell(cell);
        }
    }

    public PdfPTable build() {
        return table;
    }
}
