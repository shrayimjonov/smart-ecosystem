package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.GroupRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    GroupRepository groupRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Groups.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "Компания", "Филиал", "Отдел", "Статус");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        groupRepository
                .getResultList(filter)
                .getList()
                .forEach(group ->
                        csvWriter.writeNext(
                                new String[]{
                                        group.getName(),
                                        group.getDepartment().getBranch().getCompany().getName(),
                                        group.getDepartment().getBranch().getName(),
                                        group.getDepartment().getName(),
                                        group.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
