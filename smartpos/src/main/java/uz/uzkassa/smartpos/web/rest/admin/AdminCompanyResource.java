package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.CompanyDTO;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CompanyReceiptsDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CompanyReceiptsStatDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CurrentBranchesAndTerminalsDTO;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.dto.filter.CompanyFilter;
import uz.uzkassa.smartpos.dto.filter.UserFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;
import uz.uzkassa.smartpos.dto.nic.TINAbstractDTO;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.service.*;
import uz.uzkassa.smartpos.utils.EncryptionUtils;
import uz.uzkassa.smartpos.utils.RandomUtil;
import uz.uzkassa.smartpos.web.rest.vm.KeyAndPasswordVM;
import uz.uzkassa.smartpos.web.rest.vm.RegisterVM;

import java.util.List;

@RestController
@RequestMapping("/api/admin/company")
@RequiredArgsConstructor
public class AdminCompanyResource {
    private final CompanyService companyService;
    private final UserService userService;
    private final EtcSmsService smsService;
    private final LocalizationService localizationService;
    private final BranchService branchService;


    @GetMapping
    public Page<CompanyDTO> getCompanies(@ParameterObject CompanyFilter filter) {
        return companyService.getCompanies(filter);
    }


    @GetMapping("/lookup")
    public List<CompanyBaseDTO> getCompaniesLookup(@ParameterObject CompanyFilter filter) {
        return companyService.getCompaniesLookup(filter);
    }

    @PutMapping
    public ResponseEntity<CompanyDTO> updateCompany(@RequestBody @Valid CompanyDTO companyDTO) {
        if (companyDTO.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("id.required"));
        }
        CompanyDTO result = companyService.update(companyDTO, true);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CommonDTO>> searchCompany(@RequestParam(required = false) String searchKey) {
        List<CommonDTO> result = companyService.search(searchKey);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/has-telegram")
    public ResponseEntity<Boolean> isHasTelegram(@PathVariable Long id) {
        boolean result = companyService.hasTelegramAccount(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createCompanyUser(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new BadRequestException(localizationService.getMessage("new.user.cannot.already.id"));
        } else {
            userDTO.setPassword(RandomUtil.generateNumericKey());
            User newUser = userService.createUser(userDTO);
            smsService.sendSms(new EtcRequestDto(userDTO.getLogin(), StringUtils.join(Constants.COMMON_SMS, userDTO.getPassword())));
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser.getDTO());
        }
    }

    @GetMapping("/users")
    public Page<UserDTO> getCompanyUsers(@ParameterObject UserFilter filter) {
        return userService.getUsers(filter, true);
    }

    @GetMapping("/receipts")
    public Page<CompanyReceiptsDTO> getReceiptAmountByCompany(@ParameterObject CustomerReceiptsFilter filter) {
        return companyService.getReceiptsAmountByCompany(filter);
    }

    @GetMapping("/receipts/stat")
    public CompanyReceiptsStatDTO getReceiptsByCompanyStat(@ParameterObject CustomerReceiptsFilter filter) {
        return companyService.getReceiptsAmountByCompanyStat(filter);
    }

    @GetMapping("/current/branches-and-terminals")
    public CurrentBranchesAndTerminalsDTO getCurrentBranchesAndTerminalsCountByCompany(@RequestParam(required = false) Long companyId) {
        return companyService.getCurrentBranchesAndTerminalsCountByCompany(companyId);
    }

    @GetMapping("/by-tin/{tin}")
    public ResponseEntity<TINAbstractDTO> getNicCompany(@PathVariable String tin) {
        return companyService.getCompanyFromNic(tin);
    }

    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        if (companyDTO.getId() != null) {
            throw new BadRequestException(localizationService.getMessage("new.entity.cannot.id"));
        }
        CompanyDTO result = companyService.create(companyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/owner/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterVM registerVM) {
        if (!EncryptionUtils.checkLogin(registerVM.getLogin())) {
            throw new BadRequestException("Invalid phone");
        }

        User user = userService.registerOwner(registerVM);
        smsService.sendSms(new EtcRequestDto(user.getLogin(), StringUtils.join(Constants.COMMON_SMS, user.getActivationKey())));
        smsService.sendSms(new EtcRequestDto(user.getLogin(), Constants.PUBLIC_OFFER));
    }

    @PostMapping("/owner/activate")
    public ResponseEntity<UserDTO> activate(@RequestBody KeyAndPasswordVM param) {
        userService.activate(param.getLogin(), param.getKey(), param.getNewPassword());
        return ResponseEntity.ok(userService.getUser(param.getLogin()));
    }

    @GetMapping("/stats/{id}")
    public ResponseEntity<CompanyReceiptsStatDTO> getStats(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchCountAndTerminalCountFromCompanyId(id));
    }
}
