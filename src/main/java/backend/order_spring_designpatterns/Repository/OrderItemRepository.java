package backend.order_spring_designpatterns.Repository;

import backend.order_spring_designpatterns.Entity.OrderItem;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/* Definição de interface que interage com banco de dados e implementa interface com métodos CRUD para o tipo/tabela
especificado */
/* A dependência (Spring Data JPA) cria a implementação dos métodos em tempo de execução */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Método para select com base nos parâmetros (campos correspondentes na tabela) especificados
    // @Query especifica uma consulta SQL
    @Query(value = "SELECT * FROM order_items WHERE product_id = :productId AND order_id = :orderId", nativeQuery = true)
    // @Param define o parâmetro e seu respectivo argumento utilizado na query
    OrderItem findByProductAndOrderId(@Param("productId") Long productId, @Param("orderId") Long orderId);
}
