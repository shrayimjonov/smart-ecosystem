package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.DepartmentRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DepartmentExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    DepartmentRepository departmentRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Departments.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "Компания", "Филиал", "Статус");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        departmentRepository
                .getResultList(filter)
                .getList()
                .forEach(department ->
                        csvWriter.writeNext(
                                new String[]{
                                        department.getName(),
                                        department.getBranch().getCompany().getName(),
                                        department.getBranch().getName(),
                                        department.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
