package uz.uzkassa.silen.pdf;

import com.lowagie.text.*;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.VerticalAlignment;
import com.lowagie.text.pdf.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import uz.uzkassa.silen.domain.CustomerProduct;
import uz.uzkassa.silen.repository.pg.CustomerProductRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiscountPdfHandler {
    private final CustomerProductRepository customerProductRepository;

    private final String[] headers = new String[]{"№", "НАИМЕНОВАНИЕ ТОВАРА", "СКИДКА %", "СКИДКА UZS", "СТОИМОСТЬ"};
    private Font baseFont;
    private File file;

    @PostConstruct
    private void init() {
        log.debug("Registered families {}", FontFactory.getRegisteredFamilies());

        BaseFont helvetica = null;
        try {
            String fontPath = ResourceUtils.getFile("classpath:pdf/fonts/opensans/OpenSans-Regular.ttf").toString();
            helvetica = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (IOException e) {
            log.error("Helvetica not found ");
        }
        baseFont = helvetica != null ? new Font(helvetica, 12, Font.NORMAL) : new Font();


        String temporaryFileName = "export_to_pdf_file.pdf";
        file = new File(temporaryFileName);
    }

    public void export(Long customerId, HttpServletResponse response) throws IOException {

        Document document = new Document(PageSize.A4, 30, 30, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        document.newPage();

        // file header label content
        List<CustomerProduct> productList = customerProductRepository.findAllByCustomerId(customerId);
        if (productList.size() > 0) {
            document.add(createPWithBaseFont(productList.get(0).getCustomer().getName()));
            document.add(createPWithBaseFont("\n"));
        }

        // add table to file content
        document.add(createTable(productList));

        // add background image to all pages
        document.close();
        writer.close();

        // set watermark
        setWaterMark(file, response);

//        float width = document.getPageSize().getWidth();
//        float height = document.getPageSize().getHeight();
//        log.debug("width {}, Height {}", width, height);
    }

    private PdfPTable createTable(List<CustomerProduct> productList) {
        TableBuilder tableBuilder = new TableBuilder(5);
        tableBuilder.addRowWithAlignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER, baseFont, headers);

        for (int i = 0; i < productList.size(); ) {
            CustomerProduct customerProduct = productList.get(i);
            tableBuilder.addRow(baseFont, String.valueOf(++i), customerProduct.getProduct().getName(), Optional.ofNullable(customerProduct.getDiscountPercent()).orElse(BigDecimal.ZERO).toString(), Optional.ofNullable(customerProduct.getDiscount()).orElse(BigDecimal.ZERO).toString(), Optional.ofNullable(customerProduct.getProduct().getPrice()).orElse(BigDecimal.ZERO).toString());
        }
        PdfPTable table = tableBuilder.build();
        // set configs table
        table.setWidthPercentage(100);
        table.setWidths(new int[]{20, 200, 70, 70, 70});

        return table;
    }

    private void setWaterMark(File file, HttpServletResponse response) {
        try (PdfReader reader = new PdfReader(file.getName())) {
            PdfStamper stamp = new PdfStamper(reader, response.getOutputStream());

            String templateImageFile = ResourceUtils.getFile("classpath:pdf/watermark/watermark.png").getPath();
            //Image Rectangle: 2003.0x1997.0 (rot: 0 degrees)
            Image templateImage = Image.getInstance(templateImageFile);
//            page size width 595.0, Height 842.0
            templateImage.scaleToFit(400, 400);
            templateImage.setAbsolutePosition(200, 442);
            PdfContentByte tempalteBytes;
            int page = 1;
            do {
                tempalteBytes = stamp.getUnderContent(page);
                tempalteBytes.addImage(templateImage);
                page++;
            } while (reader.getNumberOfPages() >= page);
            stamp.close();
        } catch (DocumentException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Paragraph createPWithBaseFont(String contentOfP) {
        return new Paragraph(convertString(contentOfP), baseFont);
    }

    private String convertString(String string) {
        byte[] text4 = string.getBytes(StandardCharsets.UTF_16);
        return new String(text4, StandardCharsets.UTF_16);
    }

    public static PdfPCell simpleCell(String cellContent) {
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Paragraph(cellContent));
        return cell;
    }
}
