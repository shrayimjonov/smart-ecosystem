package uz.uzkassa.smartpos.dto.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageDto<T> {
    private int number;
    private int size;
    private Long totalElements;
    private List<T> content;

    public PageDto(List<T> content, Pageable pageable, Long totalElements) {
        this.number = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.totalElements = totalElements;
        this.content = content;
    }

    public PageDto(Page<T> page) {
        this(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    @SuppressWarnings("unused")
    public Long getTotalPages() {
        return (long) Math.ceil((double) totalElements / size);
    }
}
