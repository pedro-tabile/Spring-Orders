package backend.order_spring_designpatterns.Service;

import backend.order_spring_designpatterns.DTO.Request.ClientRequest;
import backend.order_spring_designpatterns.DTO.Request.PatchClientRequest;
import backend.order_spring_designpatterns.Entity.Client;
import backend.order_spring_designpatterns.Exception.IdNotFound;
import backend.order_spring_designpatterns.Repository.ClientRepository;
import backend.order_spring_designpatterns.Service.Interfaces.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/* Classe que define regras de negócio para Client */
@Service
public class ClientService implements CrudService<Client, Long, ClientRequest, PatchClientRequest> {
    @Autowired
    private ClientRepository clientRepository;

    public List<Client> findAll(){
        return clientRepository.findAll();
    }

    public Client findById(Long id){
        return clientRepository.findById(id).orElseThrow(()->new IdNotFound("Cliente", id));
    }

    public Client insert(ClientRequest clientDTO){
        Client client = new Client();
        client.setName(clientDTO.name().trim().replaceAll("\\s+", " "));
        client.setEmail(clientDTO.email());

        clientRepository.save(client);
        return client;
    }

    public Client update(PatchClientRequest clientDTO, Long id){
        Client clientById = findById(id);

        if (clientDTO.email() != null && !clientDTO.email().isEmpty()) clientById.setEmail(clientDTO.email().trim());
        if (clientDTO.name() != null && !clientDTO.name().trim().isEmpty()) clientById.setName(clientDTO.name().trim().replaceAll("\\s+", " "));

        return clientRepository.save(clientById);
    }

    public void delete(Long id){
        findById(id);
        clientRepository.deleteById(id);
    }
}
