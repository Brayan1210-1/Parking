package service;

import dto.LoginRequestDto;
import dto.RegisterRequestDto;
import dto.TokenResponseDto;
import entity.User;
import enums.Rol;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UserRepository;

//Servicio de autenticacion(Registro, login y refresh de tokens)
@Service
@RequiredArgsConstructor//Inyeccion de dependencias
public class AuthService {
    private final UserRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    //Registra un nuevo usuario en el sistema
    public TokenResponseDto register(RegisterRequestDto request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
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
        User user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return new TokenResponseDto(
                jwtService.generateToken(user),
                jwtService.generateRefreshToken(user)
        );
    }

    //Refresca el access token usando un refresh token valido
    public TokenResponseDto refresh(String refreshToken) {
        Claims claims = jwtService.validateToken(refreshToken);
        Long userId = Long.parseLong(claims.getSubject());

        User user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new TokenResponseDto(jwtService.generateToken(user), refreshToken);
    }
}