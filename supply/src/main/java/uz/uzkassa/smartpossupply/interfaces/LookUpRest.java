package uz.uzkassa.smartpossupply.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.List;


public interface LookUpRest<LookUpDTO, Filter> {
    ResponseEntity<List<LookUpDTO>> lookUp(Filter filter);
}
