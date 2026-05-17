package cl.dsy1103.ms_stock.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * MANEJADOR GLOBAL DE EXCEPCIONES.
 *
 * Este componente:
 * 1. Captura TODAS las excepciones de los Controllers
 * 2. Las interception antes de que lleguen al cliente
 * 3. Devuelve respuestas HTTP consistentes
 * 4. Registra logs de errores
 *
 * @RestControllerAdvice: Spring lo inyecta en todos los Controllers
 * @Slf4j: Lombok genera un logger llamado "log" automáticamente
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Respuesta de error estándar.
     * Todos los errores devolverán este formato.
     */
    private record ErrorResponse(
            int status,
            String mensaje,
            LocalDateTime timestamp,
            String path
    ) {}

    /**
     * Captura: StockNoEncontradoException
     * Código HTTP: 404 Not Found
     */
    @ExceptionHandler(StockNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleStockNoEncontrado(
            StockNoEncontradoException ex,
            WebRequest request
    ) {
        // Log: registrar el error en la consola
        log.warn("Stock no encontrado: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    /**
     * Captura: StockInsuficienteException
     * Código HTTP: 400 Bad Request
     */
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleStockInsuficiente(
            StockInsuficienteException ex,
            WebRequest request
    ) {
        log.warn("Stock insuficiente: {} (Disponible: {}, Solicitado: {})",
                ex.getMessage(),
                ex.getCantidadDisponible(),
                ex.getCantidadSolicitada()
        );

        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("mensaje", ex.getMessage());
        error.put("cantidadDisponible", ex.getCantidadDisponible());
        error.put("cantidadSolicitada", ex.getCantidadSolicitada());
        error.put("diferencia", ex.getDiferencia());
        error.put("timestamp", LocalDateTime.now());
        error.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Captura: StockDuplicadoException
     * Código HTTP: 409 Conflict
     */
    @ExceptionHandler(StockDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleStockDuplicado(
            StockDuplicadoException ex,
            WebRequest request
    ) {
        log.warn("Stock duplicado: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    /**
     * Captura: OperacionInvalidaException
     * Código HTTP: 400 Bad Request
     */
    @ExceptionHandler(OperacionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleOperacionInvalida(
            OperacionInvalidaException ex,
            WebRequest request
    ) {
        log.warn("Operación inválida: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Captura: CatalogoException
     * Código HTTP: 502 Bad Gateway
     */
    @ExceptionHandler(CatalogoException.class)
    public ResponseEntity<ErrorResponse> handleCatalogoException(
            CatalogoException ex,
            WebRequest request
    ) {
        log.error("Error comunicándose con ms-catalogo: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_GATEWAY.value(),
                "Error conectando con catálogo de productos: " + ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(error);
    }

    /**
     * Captura: Errores de VALIDACIÓN (@Valid falló)
     * Código HTTP: 400 Bad Request
     *
     * Cuando el cliente envía un DTO inválido:
     * {
     *   "productoId": null,  // Falta @NotNull
     *   "cantidadDisponible": -5  // Falta @PositiveOrZero
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        log.warn("Error de validación en request");

        // Recolectar TODOS los errores de validación
        Map<String, String> camposErroneos = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String nombreCampo = ((FieldError) error).getField();
            String mensajeError = error.getDefaultMessage();
            camposErroneos.put(nombreCampo, mensajeError);
        });

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        respuesta.put("mensaje", "Validación fallida");
        respuesta.put("errores", camposErroneos);
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respuesta);
    }

    /**
     * Captura: CUALQUIER otra excepción no manejada.
     * Código HTTP: 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request
    ) {
        log.error("Error no controlado", ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor. Por favor contacte al administrador.",
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}