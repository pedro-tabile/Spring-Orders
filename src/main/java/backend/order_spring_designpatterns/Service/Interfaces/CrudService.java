package backend.order_spring_designpatterns.Service.Interfaces;

import java.util.List;

/* Interface que estabelece contrato de operações CRUD para Services (somente àqueles com endpoint: Product, Client e
Order) */
public interface CrudService<T, ID, DTOInsert, DTOPatch> {
    T insert(DTOInsert entityDTO);

    List<T> findAll();

    T findById(ID id);

    T update(DTOPatch entityDTO, ID id);

    void delete(ID id);
}
