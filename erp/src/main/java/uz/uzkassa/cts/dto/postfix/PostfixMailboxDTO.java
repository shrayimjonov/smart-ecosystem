package uz.uzkassa.cts.dto.postfix;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class PostfixMailboxDTO implements Serializable {

     String firstName;

     String lastName;

     String email;

     String password;

     String phone;
}
