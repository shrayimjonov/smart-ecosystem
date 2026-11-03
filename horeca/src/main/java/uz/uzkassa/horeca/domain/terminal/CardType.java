package uz.uzkassa.horeca.domain.terminal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Powered by Shuxratjon Rayimjonov
 * Date: 03.05.2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CardType {
    @Id
    private String prefix;
}
