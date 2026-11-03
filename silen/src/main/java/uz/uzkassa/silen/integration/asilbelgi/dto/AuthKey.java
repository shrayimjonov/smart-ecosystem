package uz.uzkassa.silen.integration.asilbelgi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthKey implements Serializable {
    private String uuid;
    private String data;
}
