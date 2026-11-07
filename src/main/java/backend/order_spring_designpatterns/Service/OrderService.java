package backend.order_spring_designpatterns.Service;

import backend.order_spring_designpatterns.DTO.Request.EmailRequest;
import backend.order_spring_designpatterns.DTO.Request.FromToRequest;
import backend.order_spring_designpatterns.DTO.Request.OrderItemRequest;
import backend.order_spring_designpatterns.DTO.Request.OrderRequest;
import backend.order_spring_designpatterns.DTO.Request.PatchOrderRequest;
import backend.order_spring_designpatterns.DTO.Request.PersonalizationDataRequest;
import backend.order_spring_designpatterns.DTO.Request.PersonalizationEmailRequest;
import backend.order_spring_designpatterns.Entity.Client;
import backend.order_spring_designpatterns.Entity.Order;
import backend.order_spring_designpatterns.Entity.OrderItem;
import backend.order_spring_designpatterns.Entity.Payment;
import backend.order_spring_designpatterns.Entity.Product;
import backend.order_spring_designpatterns.Exception.IdNotFound;
import backend.order_spring_designpatterns.Exception.StockLimitExceeded;
import backend.order_spring_designpatterns.Repository.OrderRepository;
import backend.order_spring_designpatterns.Service.Enums.StatusOrderEnum;
import backend.order_spring_designpatterns.Service.Interfaces.CrudService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* Classe que define regras de negócio para Order */
@Service
public class OrderService implements CrudService<Order, Long, OrderRequest, PatchOrderRequest> {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ClientService clientService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ProductService productService;

    @Autowired
    private MailerSendService mailerSendService;

    public List<Order> findAll(){
        return orderRepository.findAll();
    }

    public Order findById(Long id){
        return orderRepository.findById(id).orElseThrow(()->new IdNotFound("Pedido", id));
    }

    public Order insert(OrderRequest orderRequest){
        /* Primeira parte da inserção responsável pelo armazenamento de informações not null para geração de id, de
        forma a permitir a inserção de valores OrderItem com a referência a este order criado. */
        Client client = clientService.findById(orderRequest.clientId());
        Order order = new Order();
        order.setClient(client);
        order.setStatus(StatusOrderEnum.PENDING);
        order.setCreationDate(OffsetDateTime.now());
        order.setPayment(null);

        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (var item : orderRequest.orderItems()){
            // Tratamento de erro para exclusão do registro inicial do pedido da tabela
            try {
                OrderItem orderItem = orderItemService.insert(item, order);
                orderItems.add(orderItem);
            } catch (StockLimitExceeded ex) {
                delete(order.getId());
                throw ex;
            }
        }
        order.setOrderItems(orderItems);
        order.setTotalValue(order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        Payment payment = paymentService.insert(orderRequest.payment(), order);
        order.setPayment(payment);

        orderRepository.save(order);

        for (var item : order.getOrderItems()) {
            BigDecimal newStock = item.getProduct().getStock().subtract(BigDecimal.valueOf(item.getAmount()));
            productService.updateStock(newStock, item.getProduct().getId());
        }

        sendEmailByApi(order);
        return order;
    }

    @Value("${TEMPLATE_ID_HTML}")
    private String templateId;

    @Value("${ADDRESS_EMAIL}")
    private String addressEmail;

    // Processo de criação e envio de body para POST na rota destinada ao serviço de email no client da SendPulse
    public void sendEmailByApi(Order order){
        FromToRequest sender = new FromToRequest("Teste Order Spring", addressEmail);
        FromToRequest recipient = new FromToRequest(order.getClient().getName(), order.getClient().getEmail());
        String subjectMessage;

        if (order.getStatus() == StatusOrderEnum.PENDING) {
            subjectMessage = "Novo registro de pedido vinculado ao seu email - Spring Orders";
        } else {
            subjectMessage = "Um pedido vinculado ao seu email foi pago e concluído - Spring Orders";
        }

        EmailRequest emailData = new EmailRequest(
                recipient,
                sender,
                subjectMessage,
                templateId,
                new PersonalizationEmailRequest(
                        recipient.email(),
                        new PersonalizationDataRequest(
                                recipient.name(),
                                order.getId(),
                                order.getStatus(),
                                order.getCreationDate(),
                                order.getTotalValue(),
                                order.getPayment().getType()
                        )
                )
        );

        mailerSendService.sendEmail(emailData);
    }

    public Order update(@Valid PatchOrderRequest orderRequest, Long id){
        Order orderSaved = findById(id);

        if (orderRequest.clientId() != null) {
            Client client = clientService.findById(orderRequest.clientId());
            orderSaved.setClient(client);
        }

        if (orderRequest.orderItems() != null && !orderRequest.orderItems().isEmpty()) {
            // Percorre lista nova (passada como parâmetro) e verifica se o item já está cadastrado - ou não - para atualização ou inserção à lista
            for (OrderItemRequest itemRequest : orderRequest.orderItems()) {
                OrderItem orderItem = orderItemService.findByProductAndOrderId(itemRequest.productId(), orderSaved.getId());

                if (orderItem != null) {
                    // Atualização do estoque do item já existente com base em um possível novo valor para a quantidade comprada
                    Integer oldAmount = orderItem.getAmount();
                    Integer newAmount = itemRequest.amount();
                    BigDecimal productStock = orderItem.getProduct().getStock();
                    BigDecimal newStock;

                    if (oldAmount > newAmount) {
                        newStock = productStock.add(BigDecimal.valueOf(oldAmount - newAmount));
                    } else if (oldAmount < newAmount) {
                        newStock = productStock.subtract(BigDecimal.valueOf(newAmount - oldAmount));
                    } else {
                        newStock = productStock;
                    }

                    productService.updateStock(newStock, itemRequest.productId());
                    orderItemService.updateFromDTOData(itemRequest, orderItem);
                } else {
                    OrderItem newOrderItem = orderItemService.insert(itemRequest, orderSaved);
                    orderSaved.getOrderItems().add(newOrderItem);

                    // Atualização do estoque do novo item
                    BigDecimal newStock = newOrderItem.getProduct().getStock().subtract(BigDecimal.valueOf(newOrderItem.getAmount()));
                    productService.updateStock(newStock, newOrderItem.getProduct().getId());
                }
            }

            // Verifica se cada item salvo na lista atual do pedido (Order) também está presente na nova lista recebida
            Iterator<OrderItem> iteratorOrderItems = orderSaved.getOrderItems().iterator();
            while (iteratorOrderItems.hasNext()) {
                OrderItem atualValue = iteratorOrderItems.next();
                boolean presentInNewList = orderRequest.orderItems().stream()
                        .anyMatch(e -> e.productId().equals(atualValue.getProduct().getId()));

                // Remove item caso não esteja presente na nova lista
                if (!presentInNewList) {
                    // Atualiza stock repondo a quantidade do item que havia sido comprado, mas foi cancelado
                    Product orderItemProduct = productService.findById(atualValue.getProduct().getId());
                    BigDecimal newStockValue = orderItemProduct.getStock().add(BigDecimal.valueOf(atualValue.getAmount()));
                    productService.updateStock(newStockValue, atualValue.getProduct().getId());

                    orderItemService.deleteById(atualValue.getId());
                    iteratorOrderItems.remove();
                }
            }

            orderSaved.setTotalValue(orderSaved.getOrderItems().stream()
                    .map(OrderItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (orderRequest.payment() != null) paymentService.updateById(orderSaved.getPayment().getId(), orderRequest.payment());

        orderRepository.save(orderSaved);

        if (orderRequest.clientId() != null && orderRequest.orderItems() != null && !orderRequest.orderItems().isEmpty()
                && orderRequest.payment() != null)
            sendEmailByApi(orderSaved);

        return orderSaved;
    }

    public Order updatePaid(Long id){
        Order orderSaved = findById(id);
        orderSaved.setStatus(StatusOrderEnum.CONCLUDED);
        paymentService.updatePaid(orderSaved.getPayment().getId());

        orderRepository.save(orderSaved);
        sendEmailByApi(orderSaved);

        return orderSaved;
    }

    public void delete(Long id){
        findById(id);
        orderRepository.deleteById(id);
    }
}
