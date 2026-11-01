package backend.order_spring_designpatterns.Service;

import backend.order_spring_designpatterns.DTO.Request.OrderItemRequest;
import backend.order_spring_designpatterns.Entity.Order;
import backend.order_spring_designpatterns.Entity.OrderItem;
import backend.order_spring_designpatterns.Entity.Product;
import backend.order_spring_designpatterns.Exception.StockLimitExceeded;
import backend.order_spring_designpatterns.Repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/* Classe que define regras de negócio para Order_Item (componente de Order) */
@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductService productService;

    public OrderItem insert(OrderItemRequest orderItemRequest, Order order) {
        Product product = productService.findById(orderItemRequest.productId());
        Integer productAmount = orderItemRequest.amount();
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(productAmount.longValue()));

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);

        if (productAmount.longValue() > product.getStock().longValue())
            throw new StockLimitExceeded();

        orderItem.setAmount(productAmount);
        orderItem.setTotalPrice(totalPrice);

        /* Uma vez que "order" corresponde ao objeto passado como parâmetro, sendo uma referência ao Order recebido de
        OrderService.insert(), o campo order_id da tabela OrderItem será preenchido pelo JPA com o id gerado para tal
        Order durante sua criação. Isso ocorre porque houve declaração de relacionamente entre as entidades/campos
        (expressa nos arquivos Entity) */
        orderItem.setOrder(order);

        orderItemRepository.save(orderItem);
        return orderItem;
    }

    public OrderItem findByProductAndOrderId(Long productId, Long orderId){
        return orderItemRepository.findByProductAndOrderId(productId, orderId);
    }

    public void updateFromDTOData(OrderItemRequest orderItemRequest, OrderItem orderItemSaved){
        Product product = productService.findById(orderItemRequest.productId());
        orderItemSaved.setAmount(orderItemRequest.amount());
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.amount().longValue()));
        orderItemSaved.setTotalPrice(totalPrice);

        orderItemRepository.save(orderItemSaved);
    }

    public void deleteById(Long id){
        orderItemRepository.deleteById(id);
    }
}
