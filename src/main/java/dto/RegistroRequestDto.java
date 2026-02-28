package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroRequestDto {
    private String name;
    private String email;
    private String password;
    private String document;
    private String phone;
}
