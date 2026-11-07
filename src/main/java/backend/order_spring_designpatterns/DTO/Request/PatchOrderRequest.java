package backend.order_spring_designpatterns.DTO.Request;

import jakarta.validation.Valid;

import java.util.List;

/* Classe responsável por definir o transporte de dados de uma requisição Patch ao OrderService, delimitando informações
específicas */
public record PatchOrderRequest(
        Long clientId,

        @Valid List<OrderItemRequest> orderItems,

        @Valid PaymentRequest payment
) {
}
