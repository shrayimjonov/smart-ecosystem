package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.KapitalLegalPaymentDetail;
import uz.uzkassa.cts.domain.KapitalbankPayment;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;
import uz.uzkassa.cts.repository.KapitalLegalPaymentDetailRepository;
import uz.uzkassa.cts.repository.KapitalbankPaymentRepository;
import uz.uzkassa.cts.service.KapitalBank1CService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KapitalBank1CServiceImpl implements KapitalBank1CService {

    private final KapitalbankPaymentRepository kapitalbankPaymentRepository;
    private final KapitalLegalPaymentDetailRepository kapitalLegalPaymentDetailRepository;

    public KapitalBank1CServiceImpl(KapitalbankPaymentRepository kapitalbankPaymentRepository,
                                    KapitalLegalPaymentDetailRepository kapitalLegalPaymentDetailRepository) {
        this.kapitalbankPaymentRepository = kapitalbankPaymentRepository;
        this.kapitalLegalPaymentDetailRepository = kapitalLegalPaymentDetailRepository;
    }

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{2}.\\d{2}.\\d{4}$");

    private static String[] getDate(String desc) {
        int count = 0;
        String[] allMatches = new String[2];
        Matcher m = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d").matcher(desc);
        while (m.find()) {
            allMatches[count] = m.group();
            count++;
        }
        return allMatches;
    }


    static final List<List<Long>> listId = new ArrayList<>();


    public static void stringToLong(String acc) {
        String[] s = acc.split(" ");
        List<Long> list = new ArrayList<>();
        for (String child : s) {
            if (!StringUtils.isEmpty(child))
                list.add(Long.parseLong(child));
        }
        if (!list.isEmpty())
            listId.add(list);
    }

    private static List<KapitalbankPayment> adding(List<KapitalbankPayment> payments, KapitalbankPayment payment) {
        payments.add(payment);
        return payments;
    }

    Map<Long, Long> myObjectsCounted;
    Map<Long, List<KapitalbankPayment>> myObjects;
    List<List<KapitalbankPayment>> foundSubsets;
    private List<Long> amounts;

    private void initObjects() {
        myObjectsCounted = new HashMap<>();
        myObjects = new HashMap<>();
        amounts = new ArrayList<>();
        foundSubsets = new ArrayList<>();
    }

    public void findSubsets(Long total, int position) {
        if (total == 0) {
            // If total == 0, it means that there is nothing to collect, and we found a subset.
            // Add it to the result.
            if (!foundSubsets.isEmpty())
                return;

            List<KapitalbankPayment> FoundSubset = new ArrayList<>();
            for (Map.Entry<Long, Long> entry : myObjectsCounted.entrySet()) {
                Long currentCount = entry.getValue();
                for (int i = 0; i < currentCount; i++) {
                    FoundSubset.add(myObjects.get(entry.getKey()).get(i));
                }
            }
            foundSubsets.add(FoundSubset);

            return;
        }

        if (position == amounts.size()) {
            // We reached the end of array with amounts => nothing else to add. Returning.
            return;
        }

        // Try to iterate over the next amount.
        Long currentAmount = amounts.get(position);
        for (int i = 0; total >= currentAmount * i && i <= myObjects.get(currentAmount).size(); i++) {
            // Try to use i objects with CurrentAmount.
            if (!foundSubsets.isEmpty())
                return;

            myObjectsCounted.put(currentAmount, (long) i);

            // Fill the subset recursively.
            findSubsets(total - currentAmount * i, position + 1);

            // Remove bact those i objects.
            myObjectsCounted.put(currentAmount, 0L);
        }
    }

    // Return true if there exists a subsequence of A[0..n] with given sum
    public static List<Integer> subsetSum(List<KapitalbankPayment> A, int n, int tempN, int sum, Map<String, List<Integer>> lookup, List<Integer> ids, Map<String, List<Integer>> results) {

        if (n == tempN) {
            ids = new ArrayList<>();
        }
        if (tempN < 0 || sum < 0)
            return null;

        int amount = A.get(tempN).getAmount().multiply(BigDecimal.valueOf(100)).intValue();
        ids.add(amount);

        String key = tempN + "-" + sum;
        if (sum == 0) {
            results.put(key, ids);
            return ids;
        }
        // if sub-problem is seen for the first time, solve it and
        // store its result in a map
        if (!lookup.containsKey(key)) {
            List<Integer> include = subsetSum(A, n, tempN - 1, sum, lookup, ids, results);
            List<Integer> exclude = subsetSum(A, n, tempN - 1, sum - amount, lookup, ids, results);
            lookup.put(key, include != null ? include : exclude);
        }
        return lookup.get(key);
    }

//    public static void printSubsetSums(List<KapitalbankPayment> arr, Long sum, int i, String acc) {
//        if (sum == 0l && !"".equals(acc)) {
//            System.out.println(acc);
//            stringToLong(acc);
//            acc = "";
//        }
//        if (i == arr.size()) {
//            return;
//        }
//        printSubsetSums(arr, sum, i + 1, acc);
//        printSubsetSums(arr, sum - (arr.get(i).getAmount().multiply(BigDecimal.valueOf(100)).longValue()), i + 1, acc + " " + arr.get(i).getId());
//    }

    public static List<Integer> printSubsetSums(List<KapitalbankPayment> arr, int sum, int i, String acc, Map<String, List<Integer>> res, Map<String, List<Integer>> lookup) {

        if (!res.isEmpty()) {
            Map.Entry<String, List<Integer>> entry = res.entrySet().iterator().next();
            return entry.getValue();
        }

        String key = i + "-" + sum;
        if (sum == 0 && !"".equals(acc)) {
            System.out.println(acc);
            List<Integer> a = Arrays.stream(acc.split(" ")).map(Integer::parseInt).collect(Collectors.toList());
            res.put(key, a);
            return a;
        }
        if (i == arr.size()) {
            return null;
        }
        if (!lookup.containsKey(key)) {
            List<Integer> include = printSubsetSums(arr, sum, i + 1, acc, res, lookup);

            int amount = arr.get(i).getAmount().multiply(BigDecimal.valueOf(100)).intValue();
            Long id = arr.get(i).getId();

            List<Integer> exclude = printSubsetSums(arr, sum - amount, i + 1, acc + (!acc.isEmpty() ? " " + id : "" + id), res, lookup);
            lookup.put(key, include != null ? include : exclude);
        }
        return lookup.get(key);
    }


    @Override
    public void createDetailing() {

        Long maxBankClientId = kapitalbankPaymentRepository.getMaxBankClientId();
        List<KapitalLegalPaymentDetail> legalPaymentDetails;

        if (maxBankClientId == null) {

            LocalDateTime dateTime = LocalDateTime.now().minusDays(180);
            legalPaymentDetails = kapitalLegalPaymentDetailRepository.findByCreatedDateAfterAndPurposeContainingAndStateOrderByIdAsc(
                    dateTime,
                    "SmartVista",
                    KapitalLegalDocumentState.HELD);
        } else {
            legalPaymentDetails = kapitalLegalPaymentDetailRepository
                    .findByIdGreaterThanAndPurposeContainingAndStateOrderByIdAsc(maxBankClientId,
                            "SmartVista",
                            KapitalLegalDocumentState.HELD);
        }

        LocalDateTime beginDate = LocalDateTime.of(2020, Month.JULY, 27, 17, 0, 40);

        for (KapitalLegalPaymentDetail detail : legalPaymentDetails) {

            LocalDate date = parseLocalDateFromString(detail.getPurpose());
            List<KapitalbankPayment> payments = kapitalbankPaymentRepository.getPaymentsForDetailing(
                    "8600",
                    date.minusDays(3).atStartOfDay(),
                    date.atTime(LocalTime.MAX)
            );

            log.info("amount: {} subset", detail.getAmount());

            Map<String, List<Integer>> lookup = new HashMap<>();
            Map<String, List<Integer>> results = new HashMap<>();
            int sum = Integer.parseInt(detail.getAmount());

            printSubsetSums(payments, sum, 0, "", results, lookup);
            System.out.println(results);

            List<Long> ids = new ArrayList<>();
            if (!results.isEmpty()) {
                Map.Entry<String, List<Integer>> entry = results.entrySet().iterator().next();
                for (Integer ints : entry.getValue()) {
                    ids.add(Long.valueOf(ints));
                }
            }
            if (!ids.isEmpty()) {
                List<KapitalbankPayment> listForUpdate = payments.stream().filter(m -> ids.contains(m.getId())).collect(Collectors.toList());
                kapitalbankPaymentRepository.saveAll(listForUpdate);
            } else {
                log.error("{}cannot found", detail.getAmount());
            }
        }

    }


    private LocalDate parseLocalDateFromString(String purpose) {
        String[] dateInString = getDate(purpose);
        if (dateInString.length > 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(dateInString[0], formatter);
        }
        return LocalDate.now();
    }
}
