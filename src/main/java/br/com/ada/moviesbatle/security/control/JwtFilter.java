package br.com.ada.moviesbatle.security.control;

import br.com.ada.moviesbatle.security.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;


    public JwtFilter(UserDetailsService userDetailsService, JwtTokenService jwtTokenService) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = jwtTokenService.extractJwtFromRequest(request);

            if (SecurityContextHolder.getContext().getAuthentication() == null &&
                jwt != null &&
                jwtTokenService.validateToken(jwt)) {

                String username = jwtTokenService.getUsernameFromToken(jwt);
                userDetailsService.loadUserByUsername(username);

                // Cria uma autenticação baseada no token
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, null);

                // Define o contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
                 SignatureException | IllegalArgumentException | UsernameNotFoundException e) {
            log.error("Issue with authentication / JWT Token. ", e);
        }

        // Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }
}