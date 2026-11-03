package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class OfdApplicationFilter extends BaseFilter {

    String applicatonType;

    String ofdStatus;

    String fromDate;

    String toDate;

    List<Long> customerIds;
}
