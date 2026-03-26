package com.cesde.parkingFlow.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//new user register
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequestDto {
    private String name;
    private String lastName;
    
    @Email
    @NotBlank(message = "Email obligatorio")
    private String email;
    
    @NotBlank(message = "debe ingresar una contraseña")
    private String password;
    
    @NotBlank(message = "Documento no puede ser nulo")
    private String document;
    
    @NotBlank(message = "")
    private String phone;
}
