package backend.order_spring_designpatterns.Exception;

// Exceção lançada quando há tentativa de pagar pedido já concluído.
public class OrderAlreadyConcluded extends RuntimeException {
    public OrderAlreadyConcluded() {
        super("Este pedido já está pago e concluído!");
    }
}
