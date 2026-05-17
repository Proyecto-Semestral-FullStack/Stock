package cl.dsy1103.ms_stock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import cl.dsy1103.ms_stock.exception.CatalogoException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Cliente para comunicarse con ms-catalogo mediante WebClient.
 *
 * ¿Cómo funciona?:
 * 1. Spring inyecta el WebClient (con @LoadBalanced) de WebClientConfig
 * 2. Este client consulta propiedades de application.properties
 * 3. Construye la URL: http://ms-catalogo/catalogo/productos/{id}
 * 4. Eureka automáticamente resuelve "ms-catalogo" → localhost:8082
 * 5. Realiza la llamada HTTP GET
 * 6. Convierte el JSON a ProductoDTO
 * 7. Maneja errores
 */
@Component
@Slf4j
public class CatalogoClient {

    private final WebClient webClient;

    // Valores inyectados desde application.properties
    @Value("${app.catalogo.url}")
    private String catalogoUrl;

    @Value("${app.catalogo.timeout}")
    private long timeoutSegundos;

    @Value("${app.catalogo.endpoint.producto-por-id}")
    private String endpointProductoPorId;

    /**
     * Constructor que recibe WebClient inyectado por Spring.
     */
    public CatalogoClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Obtener producto de ms-catalogo.
     *
     * Flujo:
     * 1. Reemplaza {id} en el endpoint
     * 2. Construye URL completa: http://ms-catalogo/catalogo/productos/5
     * 3. Hace GET request con timeout y retry
     * 4. Convierte JSON a ProductoDTO
     * 5. Si falla, lanza CatalogoException
     *
     * @param productoId ID del producto a consultar
     * @return ProductoDTO con datos del producto
     * @throws CatalogoException si no existe o hay error de conexión
     */
    public ProductoDTO obtenerProducto(Long productoId) {
        log.debug("Consultando ms-catalogo para producto ID: {}", productoId);

        try {
            // Construir URL dinámica reemplazando {id}
            String uri = endpointProductoPorId.replace("{id}", productoId.toString());
            String urlCompleta = catalogoUrl + uri;

            log.debug("URL construida: {}", urlCompleta);

            ProductoDTO producto = webClient
                    .get()
                    .uri(urlCompleta)  // URL completa: http://ms-catalogo/catalogo/productos/5
                    .retrieve()         // Ejecutar request
                    .bodyToMono(ProductoDTO.class)  // Convertir JSON a ProductoDTO
                    .timeout(Duration.of(timeoutSegundos, ChronoUnit.SECONDS))  // Timeout
                    .retry(2)  // Reintentar 2 veces si falla
                    .block();  // Esperar la respuesta (bloqueante)

            log.info("Producto obtenido correctamente de ms-catalogo: {}", productoId);
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
                    "Error al consultar ms-catalogo: " + e.getStatusCode(),
                    e
            );

        } catch (Exception e) {
            // Timeout, conexión rechazada, Eureka no resolvió el nombre, etc.
            log.error("Error comunicándose con ms-catalogo", e);
            throw new CatalogoException(
                    "No se puede conectar con ms-catalogo: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * DTO para recibir respuesta JSON de ms-catalogo.
     *
     * Las propiedades deben coincidir exactamente con la respuesta JSON.
     * Ejemplo de respuesta:
     * {
     *   "id": 5,
     *   "nombre": "Laptop",
     *   "precio": 1200.00,
     *   "categoria": "Electrónica"
     * }
     */
    public static class ProductoDTO {
        private Long id;
        private String nombre;
        private Double precio;
        private String categoria;

        // Constructores vacío y completo
        public ProductoDTO() {}

        public ProductoDTO(Long id, String nombre, Double precio, String categoria) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
            this.categoria = categoria;
        }

        // Getters y Setters (necesarios para deserialización JSON)
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Double getPrecio() {
            return precio;
        }

        public void setPrecio(Double precio) {
            this.precio = precio;
        }

        public String getCategoria() {
            return categoria;
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