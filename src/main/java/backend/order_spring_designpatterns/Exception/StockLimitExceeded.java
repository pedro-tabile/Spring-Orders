package backend.order_spring_designpatterns.Exception;

// Exceção lançada quando a quantidade de um produto informada no pedido é superior ao estoque do produto
public class StockLimitExceeded extends RuntimeException {
    public StockLimitExceeded() {
        super("A quantidade disponível para o produto é menor que o valor informado!");
    }
}
