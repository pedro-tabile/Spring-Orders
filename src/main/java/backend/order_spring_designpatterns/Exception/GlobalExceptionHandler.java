package backend.order_spring_designpatterns.Exception;

import backend.order_spring_designpatterns.DTO.Response.SendEmailErrorResponse;
import backend.order_spring_designpatterns.Service.Enums.PaymentMethodsEnum;
import backend.order_spring_designpatterns.Service.Enums.RolesEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Classe responsável por definir o tratamento global de exceções
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Define a exibição de resposta HTTP para solicitação de registro com username que já está em uso
    @ExceptionHandler(UsernameAlreadyInUseException.class)
    public ResponseEntity<Map<String, String>> handleUsernameException(UsernameAlreadyInUseException ex){
        Map<String, String> errorsMessage = new HashMap<>();
        errorsMessage.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorsMessage);
    }

    // Erro capturado caso o id informado não exista na tabela
    @ExceptionHandler(IdNotFound.class)
    public ResponseEntity<Map<String, String>> handleIdException(IdNotFound ex){
        Map<String, String> errorsMessage = new HashMap<>();
        errorsMessage.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorsMessage);
    }

    // Define a exibição de resposta HTTP para erros de validação, contendo o campo incorreto e a mensagem
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errorsMessage = new HashMap<>();
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        errors.forEach(error -> errorsMessage.put(((FieldError) error).getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMessage);
    }

    // Erro capturado caso o serviço de envio de email do MailerSend seja interrompido devido a um email incorreto
    @ExceptionHandler(MailerSendMailNotValid.class)
    public ResponseEntity<SendEmailErrorResponse> handleMailerSendEmailException(MailerSendMailNotValid ex){
        SendEmailErrorResponse responseDTO = new SendEmailErrorResponse("O pedido foi criado!", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // Erro capturado caso a senha informada para o usuário a ser deletado esteja incorreta
    @ExceptionHandler(InvalidPassword.class)
    public ResponseEntity<Map<String, String>> handleInvalidPasswordToDeleteUserAuthException(InvalidPassword ex){
        Map<String, String> errorsMessage = new HashMap<>();
        errorsMessage.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMessage);
    }

    // Erro capturado caso a quantidade informada de um produto (em um pedido) seja maior que o estoque do mesmo
    @ExceptionHandler(StockLimitExceeded.class)
    public ResponseEntity<Map<String, String>> handleStockLimitExceededException(StockLimitExceeded ex){
        Map<String, String> errorsMessage = new HashMap<>();
        errorsMessage.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMessage);
    }

    // Erro capturado em tentativa de login inválida
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAutheticationException(AuthenticationException ex){
        Map<String, String> errorsMessage = new HashMap<>();
        errorsMessage.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorsMessage);
    }

    // Erro capturado ao informar método de pagamento ou role de usuário inválida (opção escolhida não incluída no enum)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handlePaymentMethodOrUserRoleNotValidException(HttpMessageNotReadableException ex){
        Map<String, String> errorMessage = new HashMap<>();

        if (ex.getCause() instanceof InvalidFormatException invalidFormat &&
                invalidFormat.getTargetType() == RolesEnum.class){
            errorMessage.put("message", "Role inválida! Opções: 'MANAGER', 'STOCKER', 'CASHIER' ou 'CUSTOMER'");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        if (ex.getCause() instanceof InvalidFormatException invalidFormat
                && invalidFormat.getTargetType() == PaymentMethodsEnum.class) {
            errorMessage.put("message", "Tipo de pagamento inválido! Opções: 'ESPECIE', 'DEBITO', 'CREDITO' ou 'PIX'");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        errorMessage.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    /* Erro capturado durante tentaiva de cadastro de produto com um nome já existente ou tentativa de exclusão de
    registro que também é chave estrangeira */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleNameAlreadyInUseException(SQLIntegrityConstraintViolationException ex){
        Map<String, String> errorMessage = new HashMap<>();

        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")
                && ex.getMessage().contains("products.name")) {
            errorMessage.put("message", "Este produto já foi registrado!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")
                && ex.getMessage().contains("clients.email")) {
            errorMessage.put("message", "Este email já foi registrado!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        if (ex.getMessage() != null && ex.getMessage().contains("a foreign key constraint fails")) {
            errorMessage.put(
                    "message",
                    "Não é possível excluir este registro porque existem pedidos aos quais ele está associado! Caso " +
                            "este elemento seja um produto, recomenda-se a atualização do seu valor de estoque para 0 a " +
                            "fim de impedir novas associações a pedidos; caso contrário, não utilize esse registro " +
                            "em novos pedidos."
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        errorMessage.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    // Erro capturado caso username implícito no Bearer token seja inválido
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundBearerException(UsernameNotFoundException ex){
        Map<String, String> errorMessage = new HashMap<>();
        errorMessage.put("message", "Username inválido!");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    // Erro capturado caso algum dado não é informado ou é informado incorretamente à IA para definição do json de Order.
    @ExceptionHandler(AiInvalidDataEntered.class)
    public ResponseEntity<Map<String, String>> handleInvalidDataInAiRequestException(AiInvalidDataEntered ex){
        Map<String, String> errorsMessage = new HashMap<>();
        errorsMessage.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMessage);
    }

    // Erro capturado caso haja tentativa de pagar pedido já concluído.
    @ExceptionHandler(OrderAlreadyConcluded.class)
    public ResponseEntity<Map<String, String>> handleOrderAlreadyConcludedException(OrderAlreadyConcluded ex){
        Map<String, String> errorsMessage = new HashMap<>();
        errorsMessage.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorsMessage);
    }

    // Erro capturado caso o arquivo de áudio enviado seja inválido
    @ExceptionHandler(InvalidAudioFile.class)
    public ResponseEntity<Map<String, String>> handleInvalidAudioFileException(InvalidAudioFile ex){
        Map<String, String> errorMessage = new HashMap<>();
        errorMessage.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
