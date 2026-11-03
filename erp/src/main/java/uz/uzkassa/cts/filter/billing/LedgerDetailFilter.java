package uz.uzkassa.cts.filter.billing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LedgerDetailFilter extends PageFilter {
    //    private LocalDate from;
    //    private LocalDate to;
}
