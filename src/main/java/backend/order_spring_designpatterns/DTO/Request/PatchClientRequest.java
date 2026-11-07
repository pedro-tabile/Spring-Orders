package backend.order_spring_designpatterns.DTO.Request;

import jakarta.validation.constraints.Email;

/* Record responsável por definir o transporte de dados de uma requisição Patch ao ClientService, delimitando informações
específicas */
public record PatchClientRequest(
        String name,

        @Email(message = "O formato deve ser compatível ao de um email!")
        String email
) {
}
