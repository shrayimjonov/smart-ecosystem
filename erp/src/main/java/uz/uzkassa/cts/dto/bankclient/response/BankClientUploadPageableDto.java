package uz.uzkassa.cts.dto.bankclient.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BankClientUploadPageableDto {
    private Page<BankClientDetailsDto> list;
    private String fileUrl;
}
