package backend.order_spring_designpatterns.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/* Record responsável por definir o transporte de dados da requisição ao ClientService, delimitando informações
específicas */
public record ClientRequest(
        @NotBlank(message = "Nome inválido!")
        String name,

        @NotBlank(message = "Email inválido!")
        @Email(message = "O formato deve ser compatível ao de um email!")
        String email
) {
}
