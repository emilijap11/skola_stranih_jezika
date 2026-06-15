package rs.fon.skolajezika.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import rs.fon.skolajezika.repository.KorisnickiNalogRepository;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, TabTokenAuthenticationFilter tabTokenFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/prijava.html", "/favicon.ico", "/api/o-projektu",
                                "/api/tab-session/login").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/tab-session").authenticated()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/auth/lozinka").hasAnyRole("ADMIN", "PROFESOR")
                        .requestMatchers(HttpMethod.GET, "/api/moj-nalog").hasRole("PROFESOR")
                        .requestMatchers(HttpMethod.PUT, "/api/moj-nalog/kontakt").hasRole("PROFESOR")
                        .requestMatchers("/api/moj-pregled/**").hasRole("UCENIK")
                        .requestMatchers(HttpMethod.POST, "/api/ucenici").hasAnyRole("ADMIN", "PROFESOR")
                        .requestMatchers(HttpMethod.POST, "/api/grupe", "/api/upisi").hasAnyRole("ADMIN", "PROFESOR")
                        .requestMatchers(HttpMethod.PUT, "/api/grupe/*").hasAnyRole("ADMIN", "PROFESOR")
                        .requestMatchers(HttpMethod.POST, "/api/termini").hasAnyRole("ADMIN", "PROFESOR")
                        .requestMatchers(HttpMethod.PUT, "/api/termini/*/otkazi").hasAnyRole("ADMIN", "PROFESOR")
                        .requestMatchers(HttpMethod.PUT, "/api/termini/*/odrzi").hasAnyRole("ADMIN", "PROFESOR")
                        .requestMatchers(HttpMethod.GET, "/api/termini/sedmicni-pregled").hasAnyRole("ADMIN", "PROFESOR")
                        .requestMatchers(HttpMethod.GET, "/api/grupe").hasAnyRole("ADMIN", "PROFESOR")
                        .requestMatchers(HttpMethod.GET, "/api/ucenici", "/api/kursevi", "/api/termini", "/api/upisi")
                        .hasAnyRole("ADMIN", "PROFESOR")
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .httpBasic(httpBasic -> {
                })
                .exceptionHandling(errors -> errors.authenticationEntryPoint((request, response, exception) ->
                        response.sendError(HttpStatus.UNAUTHORIZED.value())))
                .addFilterBefore(tabTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .build();
    }

    @Bean
    TabTokenService tabTokenService() {
        return new TabTokenService();
    }

    @Bean
    TabTokenAuthenticationFilter tabTokenAuthenticationFilter(TabTokenService tokenService) {
        return new TabTokenAuthenticationFilter(tokenService);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(KorisnickiNalogRepository repository) {
        return korisnickoIme -> repository.findByKorisnickoIme(korisnickoIme)
                .map(nalog -> User.withUsername(nalog.getKorisnickoIme())
                        .password(nalog.getLozinkaHash())
                        .roles(nalog.getUloga().name())
                        .build())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "Korisnicki nalog nije pronadjen."
                ));
    }
}
