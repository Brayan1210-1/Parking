package dto;

import enums.Rol;
import lombok.Data;
import lombok.NoArgsConstructor;

//Registrar nuevo usuario
@NoArgsConstructor
@Data
public class RegisterRequestDto {
    private String name;
    private String email;
    private String password;
    private String document;
    private String phone;
    private Rol rol;
}
