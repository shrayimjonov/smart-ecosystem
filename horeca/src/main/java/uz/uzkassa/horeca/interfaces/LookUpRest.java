package uz.uzkassa.horeca.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.List;


public interface LookUpRest<LookUpDTO, Filter> {
    ResponseEntity<List<LookUpDTO>> lookUp(Filter filter);
}
