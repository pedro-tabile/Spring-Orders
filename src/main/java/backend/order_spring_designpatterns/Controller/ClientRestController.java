package backend.order_spring_designpatterns.Controller;

import backend.order_spring_designpatterns.DTO.Request.ClientRequest;
import backend.order_spring_designpatterns.DTO.Request.PatchClientRequest;
import backend.order_spring_designpatterns.DTO.Response.ClientResponse;
import backend.order_spring_designpatterns.Entity.Client;
import backend.order_spring_designpatterns.Service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController// Usa-se para indicar o retorno de dados no corpo da resposta HTTP/web
@RequestMapping("/clients")
// Classe responsável pelo controle de requisições e respostas da API para operações com clientes
public class ClientRestController {
    @Autowired
    private ClientService clientService;

    // Annotations Mapping (mapeiam para métodos Java) permitem tratar métodos HTTP como PUT, DELETE, CREATE e UPDATE.
    @GetMapping("/listAll")
    public ResponseEntity<List<ClientResponse>> findAll(){
        // A classe ResponseEntity representa a resposta HTTP inteira (status e corpo) enviada ao cliente após requisição
        List<Client> clients = clientService.findAll();
        List<ClientResponse> clientsResponse = clients.stream().map(ClientResponse::new).toList();

        return ResponseEntity.ok(clientsResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id){
        Client client = clientService.findById(id);
        ClientResponse clientResponse = new ClientResponse(client);

        return ResponseEntity.ok(clientResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientResponse> update(@RequestBody @Valid PatchClientRequest clientRequestDTO,
                                                 @PathVariable Long id){
        Client client = clientService.update(clientRequestDTO, id);
        ClientResponse clientResponse = new ClientResponse(client);

        return ResponseEntity.ok(clientResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id){
        clientService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Retorna resposta sem corpo
    }

    @PostMapping
    public ResponseEntity<ClientResponse> insert(@RequestBody @Valid ClientRequest clientRequestDTO){
        Client client = clientService.insert(clientRequestDTO);
        ClientResponse clientResponse = new ClientResponse(client);

        return ResponseEntity.ok(clientResponse);
    }
}
