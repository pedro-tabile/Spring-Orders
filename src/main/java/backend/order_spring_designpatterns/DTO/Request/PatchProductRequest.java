package backend.order_spring_designpatterns.DTO.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/* Record responsável por definir o transporte de dados de uma requisição Patch ao ProductService, delimitando informações
específicas */
public record PatchProductRequest(
        String name,

        @Positive(message = "O valor deve ser maior que 0!")
        @DecimalMax(value = "99999.99", message = "O valor limite é R$ 99.999,99!")
        BigDecimal price,

        @Positive(message = "O valor deve ser maior que 0!")
        BigDecimal stock
) {
}
