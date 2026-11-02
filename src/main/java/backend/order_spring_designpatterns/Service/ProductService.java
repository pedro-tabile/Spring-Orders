package backend.order_spring_designpatterns.Service;

import backend.order_spring_designpatterns.DTO.Request.ProductRequest;
import backend.order_spring_designpatterns.Entity.Product;
import backend.order_spring_designpatterns.Exception.IdNotFound;
import backend.order_spring_designpatterns.Exception.ProductNotFoundByName;
import backend.order_spring_designpatterns.Exception.StockLimitExceeded;
import backend.order_spring_designpatterns.Repository.ProductRepository;
import backend.order_spring_designpatterns.Service.Interfaces.CrudService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/* Classe que define regras de negócio para Product */
@Service
public class ProductService implements CrudService<Product, Long, ProductRequest> {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll(){
        return productRepository.findAll();
    }

    public Product findById(Long id){
        return productRepository.findById(id)
                .orElseThrow(()->new IdNotFound("Produto", id));
    }

    @Tool(
            name = "get-product-by-name",
            description = """
                    Busca as informações de um único produto pelo nome ou retorna um produto com valores null caso não 
                    seja encontrado. Use somente para fazer uma única consulta para cada produto informado. 
                    """
    )
    public Product findByNameTool(String name){
        return productRepository.findByName(name)
                .orElse(new Product(Long.getLong("0"), null, null, null));
    }

    public Product findByName(String name){
        return productRepository.findByName(name)
                .orElseThrow(()->new ProductNotFoundByName(name));
    }

    public Product insert(ProductRequest productDTO){
        Product product = new Product();
        product.setName(productDTO.name());
        product.setPrice(productDTO.price());
        product.setStock(productDTO.stock());

        productRepository.save(product);
        return product;
    }

    public Product update(ProductRequest productDTO, Long id){
        Product productById = findById(id);
        productById.setName(productDTO.name());
        productById.setPrice(productDTO.price());
        productById.setStock(productDTO.stock());

        productRepository.save(productById);
        return productById;
    }

    public void updateStock(BigDecimal stock, Long id){
        Product productById = findById(id);
        productById.setStock(stock);

        // Validação para casos de estoque negativo, indicando operação com maior quantidade de produtos comprados que o disponível
        if (productById.getStock().longValue() < 0){
            throw new StockLimitExceeded();
        }

        productRepository.save(productById);
    }

    public void delete(Long id){
        findById(id);
        productRepository.deleteById(id);
    }
}
