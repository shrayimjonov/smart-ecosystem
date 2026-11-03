package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.repository.OrderRepository;

import java.util.LinkedList;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderListCsvHandler extends AbstractCsvHandler<OrderFilter> {

    OrderRepository orderRepository;

    @Override
    public String getFileName(OrderFilter filter) {
        return "Orders.csv";
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
    protected void drawItemList(OrderFilter filter, CSVWriter csvWriter) {
        orderRepository.getResultList(filter).getList()
                .forEach(order ->
                        csvWriter.writeNext(
                                new String[]{
                                        order.getOrderNumber(),
                                        order.getCustomer().getName().concat(" ").concat(order.getCustomer().getTin()),
                                        order.getCreator().getName(),
                                        order.getOrderDate().toLocalDate().toString(),
                                        order.getStockOut() != null ? order.getStockOut().getName() : "",
                                        order.getBranch().getName(),
                                        numberFormat.format(order.getTotalPrice()),
                                        Optional.ofNullable(order.getStatus().getName()).orElse(""),
                                },
                                false
                        )
                );
    }
}
