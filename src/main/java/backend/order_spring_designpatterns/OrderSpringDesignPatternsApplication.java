package backend.order_spring_designpatterns;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderSpringDesignPatternsApplication {
    public static void main(String[] args) {
        // Configuração de carregamento de variáveis de ambiente do arquivo .env
        Dotenv dotenv = Dotenv.configure().load();
        // Definição de cada variável nas propriedades (properties) do sistema
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

        SpringApplication.run(OrderSpringDesignPatternsApplication.class, args);
    }

    // ------------------------------ Comentários adicionais: ------------------------------

    /* O bean (objeto gerenciado pelo Spring) @Component e suas especializações (como @Repository, @Controller e
    @Service) têm escopo Singleton por padrão. */

    /* @Autowired realiza a injeção de dependência automática, permitindo que o Spring forneça um bean (objeto
    gerenciado pelo framework). */

    /* Os atributos marcados com @Autowired (como clientRepository) recebem a referência para um bean do tipo
    especificado, gerenciado pelo Spring. Portanto, esses beans são Singleton e a referência para um atributo de
    determinado tipo em diferentes classes aponta para o mesmo objeto. */
}
