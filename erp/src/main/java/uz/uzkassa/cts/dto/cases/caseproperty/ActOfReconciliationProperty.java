package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.enums.CaseStatus;
import uz.uzkassa.cts.service.CaseService;

import java.util.List;

@Getter
@Setter
@Component
public class ActOfReconciliationProperty implements CaseAction {

    @Autowired
    @JsonIgnore
    private CaseService caseService;

    @NotNull
    private Long customerId;
    @NotBlank
    private String appType;
    private Long caseId;

    @Override
    public void accept() {
        Cases cases = caseService.findById(caseId);
        cases.setStatus(CaseStatus.ACCEPTED);
        caseService.save(cases);
    }

    @Override
    public List<String> generateTemplate() {
        throw new UnsupportedOperationException("Шаблон не предусмотрен");
    }

    @JsonIgnore
    @Override
    public boolean is2Step() {
        return false;
    }
}
