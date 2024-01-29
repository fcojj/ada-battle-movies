package br.com.ada.moviesbatle.config;

import br.com.ada.moviesbatle.security.control.JwtFilter;
import br.com.ada.moviesbatle.security.service.JwtTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig{

    private static final String[] PERMITTED_URLS_WITHOUT_AUTHENTICATON = {
            "/actuator/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/open_api/**",
            "/webjars/**",
            "/match/**",
            "/h2-console/**",
            "/error/**"};

    private final JwtTokenService jwtTokenService;

    public WebSecurityConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails userTest1 = User.builder().username("player1")
                .password(passwordEncoder().encode("qwe123"))
                .roles("USER")
                .build();

        UserDetails userTest2 = User.builder().username("player2")
                .password(passwordEncoder().encode("qwe123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userTest1, userTest2);
    }

    @Bean
    public AuthenticationManager authManager() {
        var authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                    .csrf(csrf -> { csrf.disable();
                                   csrf.ignoringRequestMatchers(PERMITTED_URLS_WITHOUT_AUTHENTICATON); })
                    .cors(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(requests -> {
                                                          requests.requestMatchers(PERMITTED_URLS_WITHOUT_AUTHENTICATON).permitAll();
                                                          requests.anyRequest().authenticated();
                                                       })
                    .addFilterBefore(new JwtFilter(userDetailsService(), jwtTokenService), UsernamePasswordAuthenticationFilter.class)
                    .userDetailsService(userDetailsService())
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .httpBasic(Customizer.withDefaults()).build();

    }
}