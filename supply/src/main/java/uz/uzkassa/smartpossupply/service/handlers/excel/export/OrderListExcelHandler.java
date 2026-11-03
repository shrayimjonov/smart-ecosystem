package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.repository.OrderRepository;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderListExcelHandler extends AbstractExcelHandler<OrderFilter> {

    OrderRepository orderRepository;

    @Override
    public String getFileName(OrderFilter filter) {
        return "Orders.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Номер заказа");
        header.add("Клиент");
        header.add("Кем создан");
        header.add("Дата заказа");
        header.add("Номер отгрузки");
        header.add("Филиал");
        header.add("Сумма");
        header.add("Статус");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(OrderFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<Order> orders = orderRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Order order : orders) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(order.getOrderNumber());
            row.createCell(1).setCellValue(order.getCustomer().getName().concat(" ").concat(order.getCustomer().getTin()));
            row.createCell(2).setCellValue(order.getCreator().getName());
            row.createCell(3).setCellValue(order.getOrderDate().toLocalDate().toString());
            row.createCell(4).setCellValue(order.getStockOut() != null ? order.getStockOut().getName() : "");
            row.createCell(5).setCellValue(order.getBranch().getName());
            row.createCell(6).setCellValue(order.getTotalPrice().toString());
            row.createCell(7).setCellValue(order.getStatus().getName());
        }
        return workbook;
    }
}
