package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.transaction.Transaction;
import uz.uzkassa.smartpossupply.domain.transaction.TransactionItem;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.transaction.TransactionDTO;
import uz.uzkassa.smartpossupply.dto.transaction.TransactionItemListDTO;
import uz.uzkassa.smartpossupply.dto.transaction.TransactionListDTO;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.filters.TransactionFilter;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.repository.TransactionItemRepository;
import uz.uzkassa.smartpossupply.repository.TransactionRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2023 19:35
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TransactionService extends BaseService {

    TransactionRepository transactionRepository;
    TransactionItemRepository transactionItemRepository;
    NumberingContext numberingContext;

    @Transactional
    public Long create(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.TRANSACTION, getCurrentCompanyId());
        transaction.setTransactionNumber(numberingMap.get(NUMBER));
        transaction.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setCurrency(transactionDTO.getCurrency());
        transaction.setCompanyId(getCurrentCompanyId());
        transactionRepository.save(transaction);

        transactionDTO.getItems().forEach(transactionItemDTO -> {
            TransactionItem item = new TransactionItem();
            item.setDebit(transactionItemDTO.getDebit());
            item.setCredit(transactionItemDTO.getCredit());
            item.setAccountId(transactionItemDTO.getAccountId());
            transaction.addItem(item);
        });
        return transaction.getId();
    }

    @Transactional
    public Long update(Long id, TransactionDTO transactionDTO) {
        return transactionRepository.findById(id).map(transaction -> {
            transaction.setCurrency(transactionDTO.getCurrency());
            transaction.setDescription(transactionDTO.getDescription());

            Map<Long, TransactionItem> transactionItemMap = transaction
                .getItems()
                .stream()
                .collect(Collectors.toMap(TransactionItem::getId, Function.identity()));

            transactionDTO.getItems().forEach(transactionItemDTO -> {
                TransactionItem transactionItem;
                if (transactionItemDTO.getItemId() != null && transactionItemMap.get(transactionItemDTO.getItemId()) != null) {
                    transactionItem = transactionItemMap.get(transactionItemDTO.getItemId());
                } else {
                    transactionItem = new TransactionItem();
                }
                transactionItem.setAccountId(transactionItemDTO.getAccountId());
                transactionItem.setDebit(transactionItemDTO.getDebit());
                transactionItem.setCredit(transactionItemDTO.getCredit());
                transaction.addItem(transactionItem);
                if (transactionItem.getId() != null) {
                    transactionItemMap.remove(transactionItem.getId());
                }
            });
            transactionRepository.save(transaction);
            if (!transactionItemMap.isEmpty()) {
                transactionItemRepository.deleteAllByIdInBatch(transactionItemMap.keySet());
            }
            return transaction.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Transaction.class.getSimpleName(), "id", id));
    }

    public Page<TransactionListDTO> getList(TransactionFilter filter) {
        ResultList<Transaction> resultList = transactionRepository.getResultList(filter);
        List<TransactionListDTO> result = resultList
            .getList()
            .stream()
            .map(transaction -> {
                TransactionListDTO transactionListDTO = new TransactionListDTO();
                transactionListDTO.setId(transaction.getId());
                transactionListDTO.setTransactionNumber(transaction.getTransactionNumber());
                transactionListDTO.setStatus(transaction.getStatus());
                transactionListDTO.setCurrency(transaction.getCurrency());
                transactionListDTO.setCreatedDate(transaction.getCreatedDate());
                transaction.getItems().forEach(transactionItem -> {
                    TransactionItemListDTO transactionItemListDTO = new TransactionItemListDTO();
                    transactionItemListDTO.setId(transactionItem.getId());
                    transactionItemListDTO.setDebit(transactionItem.getDebit());
                    transactionItemListDTO.setCredit(transactionItem.getCredit());
                    if (transactionItem.getAccount() != null) {
                        transactionItemListDTO.setAccount(transactionItem.getAccount().toCommonDTO());
                    }
                    transactionListDTO.addItem(transactionItemListDTO);
                });
                return transactionListDTO;
            }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }
}
