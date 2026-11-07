package backend.order_spring_designpatterns.Entity;

import backend.order_spring_designpatterns.Service.Enums.StatusOrderEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter

// DefiniçãOo da entidade Order (Pedido)
@Entity
// order em sql é uma palavra reservada
@Table(name = "orders")
public class Order {
    // Chave primária com auto-incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Definição de relação n-1 com a tabela Client - unidirecional
    @ManyToOne
    // Coluna definida como client_id e como FK
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /* Definição de relação 1-1 com a tabela Payment e lado não dominante (inverse side) de relacionamento bidirecional,
    sendo controlado/referenciado pelo order da tabela Payment que gerencia a FK */
    @OneToOne(mappedBy = "order", cascade = CascadeType.REMOVE)
    // Coluna definida como payment_id e como FK - aceita null para permitir o processo inicial de criação de Order
    //@JoinColumn(name = "payment_id")
    private Payment payment;

    // Definição de relação 1-n com a tabela OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems;

    @Column(nullable = false)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Column(nullable = false)
    // Salva o nome do Enum no banco, não um número ordinal
    @Enumerated(value = EnumType.STRING)
    private StatusOrderEnum status;

    @Column(nullable = false)
    private OffsetDateTime creationDate;
}