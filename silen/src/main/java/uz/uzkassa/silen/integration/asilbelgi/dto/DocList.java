package uz.uzkassa.silen.integration.asilbelgi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DocList implements Serializable {

    private List<DocListItem> results;
}
