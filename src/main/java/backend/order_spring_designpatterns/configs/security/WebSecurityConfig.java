package backend.order_spring_designpatterns.Configs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
// Habilita configurações de segurança personalizadas
@EnableWebSecurity
// Classe com configurações de segurança (Spring Security) relacionadas à autenticação e acesso aos endpoints
public class WebSecurityConfig {
    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                // Desabilita a proteção do csrf (token)
                .csrf(AbstractHttpConfigurer::disable)
                // Define métodos/endpoints autorizados com base na hierarquia
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/auth/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/products").hasAnyRole("STOCKER", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("STOCKER", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("STOCKER", "MANAGER")

                        .requestMatchers(HttpMethod.POST, "/orders/**").hasAnyRole("CASHIER", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/orders/**").hasAnyRole("CASHIER", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/orders/**").hasAnyRole("CASHIER", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("CASHIER", "MANAGER")

                        .requestMatchers(HttpMethod.GET, "/ai/**").hasAnyRole("CASHIER", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/ai/**").hasAnyRole("CASHIER", "MANAGER")

                        /* No caso do endpoint clients, considera-se que um customer somente pode adicionar, alterar e
                        excluir suas próprias informações no mundo real. */
                        .requestMatchers(HttpMethod.POST, "/clients/**").hasAnyRole("CASHIER", "CUSTOMER", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/clients/**").hasAnyRole("CUSTOMER", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/clients/**").hasAnyRole("CUSTOMER", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/clients/**").hasAnyRole("CASHIER", "MANAGER")

                        .anyRequest().authenticated()
                )
                // Filtro executado antes do processamento de uma autenticação enviada
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)

                // Captura de exceção lançada quando o acesso é negado ou quando a autenticação falha
                .exceptionHandling(exceptionAuth -> {
                    exceptionAuth
                            .accessDeniedHandler((req, res, accessDeniedException) -> {
                                res.setStatus(HttpStatus.FORBIDDEN.value());
                                res.setHeader("Content-Type", "text/plain");
                                res.setCharacterEncoding("UTF-8");
                                res.getWriter().write("""
                                        {
                                            message: "Acesso negado! Você não tem permissão para realizar essa operação!"
                                        }
                                        """);
                            })
                            .authenticationEntryPoint((req, res, authException) -> {
                                res.setStatus(HttpStatus.UNAUTHORIZED.value());
                                res.setHeader("Content-Type", "text/plain");
                                res.setCharacterEncoding("UTF-8");
                                res.getWriter().write("""
                                        {
                                            message: "Token inválido! Realize a autenticação!"
                                        }
                                        """);
                            });
                })

                // Gerenciamento de sessão como stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /* Método de autenticação http basic - desnecessário devido ao endpoint de login e exposição do bean de
                AuthenticationManager */
                // .httpBasic(Customizer.withDefaults())

                .build();
    }

    // Retorna um authenticationManager para processamento de autenticação
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Codificação de senhas
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        // Função hash de senha - implementa usando uma função BCrypt para permitir a comparação com o hash salvo no banco
        return new BCryptPasswordEncoder();
    }
}