package com.cesde.parkingFlow.service;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component//Spring gestiona esta clase como Bean
public class JwtAuthenticationFilter extends OncePerRequestFilter {
//Filtro que se ejecuta una vez por cada request

    //Servicio para validar/generar JWT
    private final JwtService jwtService;

    //Constructo con inyeccion de dependencia
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        //Obtiene el header Authorization
        final String authHeader = request.getHeader("Authorization");

        //Si no hay token, deja pasar la request sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);//Extrae el token sin "Bearer"

        try {
            Claims claims = jwtService.validateToken(token);//Valida token

            if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //Extrae roles del claim "roles" del JWT
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);

                List<SimpleGrantedAuthority> authorities = (roles != null)
                        ? roles.stream().map(SimpleGrantedAuthority::new).toList()
                        : List.of();

                UsernamePasswordAuthenticationToken authToken
                        = new UsernamePasswordAuthenticationToken(
                                claims.getSubject(), // user id
                                null,
                                authorities //Roles mapeados del JWT
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException e) {
            //Token invalido/expirado: no se autentica, Spring Security manejara el 401
        }

        filterChain.doFilter(request, response);
    }
}
