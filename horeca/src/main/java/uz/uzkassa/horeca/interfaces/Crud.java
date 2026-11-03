package uz.uzkassa.horeca.interfaces;

import org.springframework.data.domain.Page;


public interface Crud<ID, DTO, DetailDTO, ListDTO, Filter> {
    ID create(DTO dto);

    ID update(ID id, DTO dto);

    DetailDTO get(ID id);

    Page<ListDTO> getList(Filter f);

    void delete(ID id);
}
