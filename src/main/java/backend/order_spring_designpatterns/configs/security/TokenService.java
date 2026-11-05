package backend.order_spring_designpatterns.Configs.security;

import backend.order_spring_designpatterns.Entity.UserAuth;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
//get by name contains

// Classe responsável pelas regras de criação de token de autenticação de sessão
@Service
public class TokenService {
    // Valor (variável de ambiente) de chave secreta
    @Value(value = "${JWT_SECURITY_KEY}")
    private String secret;

    // Geração de token JWT
    public String generateToken(UserAuth userAuth) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(userAuth.getUsername())
                    .withExpiresAt(generateExpirationDate())
                    .withIssuedAt(Instant.now())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException ex) {
            throw new RuntimeException("Error generating JWT Token: ", ex);
        }
    }

    // Validação do token recebido
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            return "";
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(3).toInstant(ZoneOffset.of("-03:00"));
    }
}
