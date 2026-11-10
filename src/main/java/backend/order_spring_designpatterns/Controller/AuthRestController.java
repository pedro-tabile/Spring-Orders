package backend.order_spring_designpatterns.Controller;

import backend.order_spring_designpatterns.DTO.Request.UpdateUserAuthRequest;
import backend.order_spring_designpatterns.DTO.Request.UserAuthRequest;
import backend.order_spring_designpatterns.DTO.Request.UserAuthRegisterRequest;
import backend.order_spring_designpatterns.DTO.Response.LoginResponse;
import backend.order_spring_designpatterns.DTO.Response.UserAuthResponse;
import backend.order_spring_designpatterns.Entity.UserAuth;
import backend.order_spring_designpatterns.Service.AuthService;
import backend.order_spring_designpatterns.Service.UserAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
/* Classe responsável pelo controle de requisições e respostas da API para operações de autenticação e registro de
usuários com permissões*/
public class AuthRestController {
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private AuthService authService;

    // Endpoint personalizado para camada/tratamento de login, implementando um gerenciador de autenticação
    @PostMapping("/login-token")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid UserAuthRequest userAuthRequest){
        String token = authService.validateLoginAndGenerateToken(userAuthRequest);
        LoginResponse loginResponse = new LoginResponse(token);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid UserAuthRegisterRequest userAuthRegisterDTO){
        UserAuth userAuth = userAuthService.register(userAuthRegisterDTO);
        UserAuthResponse userAuthResponse = new UserAuthResponse(userAuth.getUsername(), userAuth.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(userAuthResponse);
    }

    @DeleteMapping
    public ResponseEntity delete(@RequestBody @Valid UserAuthRequest userAuthRequest){
        userAuthService.delete(userAuthRequest);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping
    public ResponseEntity<UserAuthResponse> update(@RequestBody @Valid UpdateUserAuthRequest dataUpdateUserAuth){
        UserAuth userAuth = userAuthService.update(dataUpdateUserAuth);
        UserAuthResponse userAuthResponse = new UserAuthResponse(userAuth.getUsername(), userAuth.getRole());

        return ResponseEntity.ok(userAuthResponse);
    }
}
