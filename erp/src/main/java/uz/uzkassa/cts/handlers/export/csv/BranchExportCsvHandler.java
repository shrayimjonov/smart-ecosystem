package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BranchRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    BranchRepository branchRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Branches.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "Родительский филиал", "Название компании", "Регион", "Район", "Статус");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        branchRepository
                .getResultList(filter)
                .getList()
                .forEach(branch ->
                        csvWriter.writeNext(
                                new String[]{
                                        branch.getName(),
                                        branch.getParentId() != null ? branch.getParent().getName() : "",
                                        branch.getCompany().getName(),
                                        branch.getAddress().getRegion().getName(),
                                        branch.getAddress().getDistrict().getName(),
                                        branch.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
