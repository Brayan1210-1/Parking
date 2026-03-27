package com.cesde.parkingFlow.service;

import com.cesde.parkingFlow.dto.LoginRequestDto;
import com.cesde.parkingFlow.dto.RegisterRequestDto;
import com.cesde.parkingFlow.dto.response.TokenResponseDto;
import com.cesde.parkingFlow.entity.User;
import com.cesde.parkingFlow.enums.Rol;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cesde.parkingFlow.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    //Registra un nuevo usuario en el sistema
    public TokenResponseDto register(RegisterRequestDto request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .document(request.getDocument())
                .phone(request.getPhone())
                .name(request.getName())
                .lastName(request.getLastName())
                .rol(Rol.ABONADO) // rol por defecto
                .activo(true)
                .build();

        usuarioRepository.save(user);

        return new TokenResponseDto(
                jwtService.generateToken(user),
                jwtService.generateRefreshToken(user)
        );
    }

    //Inicia sesion y devuelve tokens
    public TokenResponseDto login(LoginRequestDto request) {
        User user = findByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String refreshToken = refreshTokenService.createRefreshToken(user);
        
        return new TokenResponseDto(
                jwtService.generateToken(user),
                refreshToken 
        );
    }

  
    
    public User findByEmail(String email) {
    	User user = usuarioRepository.findByEmail(email).
    			orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    	
    	return user;
    	
    }
}