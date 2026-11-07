package backend.order_spring_designpatterns.Repository;

import backend.order_spring_designpatterns.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/* Definição de interface que interage com banco de dados e implementa interface com métodos CRUD para o tipo/tabela
especificado */
/* A dependência (Spring Data JPA) cria a implementação dos métodos em tempo de execução */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Método para select com base nos parâmetros (campos correspondentes na tabela) especificados
    // @Query especifica uma consulta SQL
    @Query(value = "SELECT * FROM products WHERE name LIKE CONCAT('%', :name, '%')", nativeQuery = true)
    // @Param define o parâmetro e seu respectivo argumento utilizado na query
    List<Product> findByNameContaining(@Param("name") String name);

    // Método usado pela tool - Spring Data JPA entende pelo nome do método quais operações devem ser feitas
    Optional<Product> findByName(String name);
}