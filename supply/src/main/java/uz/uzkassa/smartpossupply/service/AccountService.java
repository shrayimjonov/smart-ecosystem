package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.Account;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.AccountType;
import uz.uzkassa.smartpossupply.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountService extends BaseService {

    AccountRepository accountRepository;

    @Transactional
    public void createDefault(Company company) {
        List<Account> accounts = new ArrayList<>();
        accounts.add(createAndGet(company, AccountType.EXTERNAL));
        accounts.add(createAndGet(company, AccountType.STOCK));
        accounts.add(createAndGet(company, AccountType.WEB_KASSA));
        accounts.add(createAndGet(company, AccountType.WHOLESALE));
        accountRepository.saveAll(accounts);
    }

    private Account createAndGet(Company company, AccountType accountType) {
        Account account = new Account();
        account.setAccountType(accountType);
        account.setCompanyId(company.getId());
        return account;
    }
}
