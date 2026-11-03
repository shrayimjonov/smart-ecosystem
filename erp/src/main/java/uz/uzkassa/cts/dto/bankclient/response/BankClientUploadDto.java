package uz.uzkassa.cts.dto.bankclient.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BankClientUploadDto {
    private List<BankClientDetailsDto> list = new ArrayList<>();
    private String fileUrl;
}
