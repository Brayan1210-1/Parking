package com.cesde.parkingFlow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.cesde.parkingFlow.service.JwtAuthenticationFilter;

//Config de seguridad, define endpoints publicos y cuales requieren autenticacion
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;//Inyecta filtro JWT personalizado

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;//Constructor que recibe el filtro JWT
    }

    @Bean//Bean administrado por Spring
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())//Desactiva CSRF (No necesario en APIs REST
                //Configura sesiones como STATELESS -> no se guarda estado en el servidor, se maneja con JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())//Desactiva login por formulario HTML
                .httpBasic(basic -> basic.disable())//Desactiva autenticacion basica

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()//Permite acceso libre a endpoints de autenticacion
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/**").hasRole("ADMIN")//Solo admin puede acceder a /admin/**
                        .requestMatchers(
                                "/swagger/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/docs/**").permitAll()//Swagger libre
                        .anyRequest().authenticated())//Lo demas requiere autenticacion

                //Inserta filtro JWT antes del filtro de autenticacion por user/password
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();//Devuelve la configuracion final
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

