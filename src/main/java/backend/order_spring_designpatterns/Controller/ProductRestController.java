package backend.order_spring_designpatterns.Controller;

import backend.order_spring_designpatterns.DTO.Request.PatchProductRequest;
import backend.order_spring_designpatterns.DTO.Request.ProductRequest;
import backend.order_spring_designpatterns.DTO.Response.ProductResponse;
import backend.order_spring_designpatterns.Entity.Product;
import backend.order_spring_designpatterns.Service.ProductService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController// Usa-se para indicar o retorno de dados no corpo da resposta HTTP/web
@RequestMapping("/products")
// Classe responsável pelo controle de requisições e respostas da API para operações com produtos
public class ProductRestController {
    @Autowired
    private ProductService productService;

    // Annotations Mapping permitem tratar métodos HTTP como PUT, DELETE, CREATE e UPDATE.
    @GetMapping("/listAll")
    public ResponseEntity<List<ProductResponse>> findAll(){
        // A classe ResponseEntity representa a resposta HTTP inteira (status e corpo) enviada ao cliente após requisição
        List<Product> products = productService.findAll();
        List<ProductResponse> productsResponse = products.stream().map(ProductResponse::new).toList();

        return ResponseEntity.ok(productsResponse);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id){
        Product product = productService.findById(id);
        ProductResponse productResponse = new ProductResponse(product);

        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> findByNameContaining(@RequestParam String name){
        List<Product> products = productService.findByNameContaining(name);
        List<ProductResponse> productsResponse = products.stream().map(ProductResponse::new).toList();

        return ResponseEntity.ok(productsResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@RequestBody @Valid PatchProductRequest productRequestDTO,
                                                  @PathVariable Long id){
        Product product = productService.update(productRequestDTO, id);
        ProductResponse productResponse = new ProductResponse(product);

        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id){
        productService.delete(id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna resposta sem corpo
    }

    @PostMapping
    public ResponseEntity<ProductResponse> insert(@RequestBody @Valid ProductRequest productRequestDTO){
        Product product = productService.insert(productRequestDTO);
        ProductResponse productResponse = new ProductResponse(product);

        return ResponseEntity.ok(productResponse);
    }
}
