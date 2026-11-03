package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.enums.CustomerBranchApplicationStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CustomerBranchApplicationRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerBranchApplicationExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    CustomerBranchApplicationRepository customerBranchApplicationRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Customer_Branch_Applications.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "TT", "Номер телефона", "ИНН", "Регион", "Район", "Сотрудник", "Причина", "Статус заявки", "Дата создания");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        customerBranchApplicationRepository
                .getResultList(filter)
                .getList()
                .forEach(customerBranchApplication ->
                        csvWriter.writeNext(
                                new String[]{
                                        customerBranchApplication.getCustomerBranch().getCustomer().getName(),
                                        customerBranchApplication.getCustomerBranch().getName(),
                                        customerBranchApplication.getCustomerBranch().getPhone(),
                                        customerBranchApplication.getCustomerBranch().getCustomer().getTin(),
                                        Optional.ofNullable(customerBranchApplication.getCustomerBranch().getAddress()).map(BaseAddress::getRegion).map(Region::getName).orElse(""),
                                        Optional.ofNullable(customerBranchApplication.getCustomerBranch().getAddress()).map(BaseAddress::getDistrict).map(District::getName).orElse(""),
                                        customerBranchApplication.getManager().getName(),
                                        Optional.ofNullable(customerBranchApplication.getReason()).map(Reference::getName).orElse(""),
                                        Optional.ofNullable(customerBranchApplication.getStatus()).map(CustomerBranchApplicationStatus::getName).orElse(""),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(customerBranchApplication.getCreatedDate())
                                },
                                false
                        )
                );
    }
}
