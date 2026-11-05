package backend.order_spring_designpatterns.Repository;

import backend.order_spring_designpatterns.Entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/* Definição de interface que interage com banco de dados e implementa interface com métodos CRUD para o tipo/tabela
especificado */
/* A dependência (Spring Data JPA) cria a implementação dos métodos em tempo de execução */
@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, UUID> {
    // Spring Data JPA entende pelo nome do método quais operações devem ser feitas
    Optional<UserAuth> findByUsername(String username);
}