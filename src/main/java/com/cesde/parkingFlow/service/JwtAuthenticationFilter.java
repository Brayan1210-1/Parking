package com.cesde.parkingFlow.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
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
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    //Rutas que no necesitan pasar por el filtro JWT
    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/swagger",
            "/swagger/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/docs",
            "/docs/**",
            "/webjars/**",
            "/favicon.ico",
            "/api/v1/auth/**"
    );

    //Constructor con inyeccion de dependencia
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    //Excluye rutas publicas del filtro JWT
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
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
