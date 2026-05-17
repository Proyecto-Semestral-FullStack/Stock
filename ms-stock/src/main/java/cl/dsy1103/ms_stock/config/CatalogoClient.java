package cl.dsy1103.ms_stock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import cl.dsy1103.ms_stock.exception.CatalogoException;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Cliente para comunicarse con ms-catalogo.
 *
 * Responsabilidades:
 * - Llamar a ms-catalogo
 * - Manejar errores de conexión
 * - Reintentos automáticos
 * - Logging de llamadas
 *
 * @Component: Spring lo inyecta automáticamente en services
 * @Slf4j: Logger automático de Lombok
 */
@Component
@Slf4j
public class CatalogoClient {

    private final WebClient webClient;

    // Inyectamos valores del application.yml
    @Value("${app.catalogo.url}")
    private String catalogoUrl;

    @Value("${app.catalogo.timeout}")
    private long timeoutSegundos;

    @Value("${app.catalogo.endpoint.producto-por-id}")
    private String endpointProductoPorId;

    /**
     * Constructor que recibe WebClient por inyección.
     *
     * @param webClient bean que creamos en WebClientConfig
     */
    public CatalogoClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Obtener información de un producto desde ms-catalogo.
     *
     * Flujo:
     * 1. Construye la URL: http://ms-catalogo/catalogo/productos/5
     * 2. Hace GET request
     * 3. Convierte a ProductoDTO
     * 4. Reintenta 3 veces si falla
     * 5. Maneja errores
     *
     * @param productoId ID del producto
     * @return ProductoDTO con datos del producto
     * @throws CatalogoException si no encuentra o hay error
     */
    public ProductoDTO obtenerProducto(Long productoId) {
        log.debug("Consultando ms-catalogo para producto ID: {}", productoId);

        try {
            // Construir URL dinámica: /catalogo/productos/{id}
            String uri = endpointProductoPorId.replace("{id}", productoId.toString());

            ProductoDTO producto = webClient
                    .get()
                    .uri(catalogoUrl + uri)  // URL completa: http://ms-catalogo/catalogo/productos/5
                    .retrieve()               // Ejecutar request
                    .bodyToMono(ProductoDTO.class)  // Convertir respuesta a ProductoDTO
                    .timeout(Duration.ofSeconds(timeoutSegundos))  // Timeout de 5 seg
                    .retry(3)  // Reintentar 3 veces si falla
                    .block();  // Esperar respuesta (bloqueante)

            log.info("Producto obtenido correctamente: {}", productoId);
            return producto;

        } catch (WebClientResponseException.NotFound e) {
            // ms-catalogo devolvió 404 (producto no existe)
            log.warn("Producto {} no encontrado en ms-catalogo", productoId);
            throw new CatalogoException(
                    "Producto con ID " + productoId + " no encontrado en catálogo"
            );

        } catch (WebClientResponseException e) {
            // ms-catalogo devolvió otro error (401, 403, 500, etc)
            log.error("Error en ms-catalogo (HTTP {}): {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );
            throw new CatalogoException(
                    "Error al consultar ms-catalogo: " + e.getMessage(),
                    e
            );

        } catch (Exception e) {
            // Timeout, conexión rechazada, etc
            log.error("Error comunicándose con ms-catalogo", e);
            throw new CatalogoException(
                    "No se puede conectar con ms-catalogo: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * DTO para recibir datos de ms-catalogo.
     *
     * Representa la respuesta JSON de ms-catalogo.
     * Solo incluimos los campos que nos importan.
     *
     * Ejemplo de respuesta de ms-catalogo:
     * {
     *   "id": 5,
     *   "nombre": "Laptop",
     *   "precio": 1200.00,
     *   "stock": 50,
     *   "categoria": "Electrónica"
     * }
     */
    public static class ProductoDTO {
        private Long id;
        private String nombre;
        private Double precio;
        private String categoria;

        // Constructores
        public ProductoDTO() {}

        public ProductoDTO(Long id, String nombre, Double precio, String categoria) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
            this.categoria = categoria;
        }

        // Getters
        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public Double getPrecio() {
            return precio;
        }

        public String getCategoria() {
            return categoria;
        }

        // Setter (para deserialización de JSON)
        public void setId(Long id) {
            this.id = id;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public void setPrecio(Double precio) {
            this.precio = precio;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        @Override
        public String toString() {
            return "ProductoDTO{" +
                    "id=" + id +
                    ", nombre='" + nombre + '\'' +
                    ", precio=" + precio +
                    ", categoria='" + categoria + '\'' +
                    '}';
        }
    }
}