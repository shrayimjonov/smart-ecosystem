package uz.uzkassa.smartpossupply.interfaces;

import org.springframework.data.domain.Page;


public interface Crud<ID, DTO, DetailDTO, ListDTO, Filter> {
    ID create(DTO dto);

    ID update(ID id, DTO dto);

    DetailDTO get(ID id);

    Page<ListDTO> getList(Filter f);

    void delete(ID id);

    void validate(DTO dto);
}
