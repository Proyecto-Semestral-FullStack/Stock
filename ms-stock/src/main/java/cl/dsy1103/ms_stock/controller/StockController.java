package cl.dsy1103.ms_stock.controller;

import cl.dsy1103.ms_stock.dto.AjustarStockDTO;
import cl.dsy1103.ms_stock.dto.AumentarStockDTO;
import cl.dsy1103.ms_stock.dto.DisminuirStockDTO;
import cl.dsy1103.ms_stock.dto.StockCreateDTO;
import cl.dsy1103.ms_stock.dto.StockResponseDTO;
import cl.dsy1103.ms_stock.dto.StockUpdateDTO;
import cl.dsy1103.ms_stock.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLADOR REST para operaciones de Stock.
 *
 * @RestController: Combina @Controller + @ResponseBody
 *                  Las respuestas se convierten automáticamente a JSON
 *
 * @RequestMapping("/stocks"): URL base para todos los endpoints
 *                             GET /stocks, GET /stocks/{id}, etc.
 *
 * @Slf4j: Logger automático para auditoría de requests
 * @RequiredArgsConstructor: Constructor automático para inyectar StockService
 */
@RestController
@RequestMapping("/stocks")
@Slf4j
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /* ====================================
       ENDPOINTS DE CONSULTA (GET)
       ==================================== */

    /**
     * GET /stocks
     *
     * Obtener TODOS los stocks.
     *
     * Respuesta HTTP 200:
     * [
     *   {
     *     "id": 1,
     *     "productoId": 5,
     *     "cantidadDisponible": 100,
     *     "stockMinimo": 10,
     *     "fechaCreacion": "2026-05-16T10:30:45.123",
     *     "fechaActualizacion": "2026-05-16T10:30:45.123"
     *   },
     *   ...
     * ]
     *
     * @return List<StockResponseDTO>
     */
    @GetMapping
    public ResponseEntity<List<StockResponseDTO>> listarStocks() {
        log.info("GET /stocks - Listando todos los stocks");
        List<StockResponseDTO> stocks = stockService.listarStocks();
        return ResponseEntity.ok(stocks);
    }

    /**
     * GET /stocks/{id}
     *
     * Obtener stock por ID.
     *
     * Ejemplo:
     *   GET /stocks/1
     *
     * Respuesta HTTP 200:
     * {
     *   "id": 1,
     *   "productoId": 5,
     *   "cantidadDisponible": 100,
     *   "stockMinimo": 10,
     *   ...
     * }
     *
     * Error HTTP 404 si no existe:
     * {
     *   "status": 404,
     *   "mensaje": "Stock con ID 1 no encontrado",
     *   ...
     * }
     *
     * @param id ID del stock
     * @return StockResponseDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /stocks/{} - Obtener stock por ID", id);
        StockResponseDTO stock = stockService.obtenerPorId(id);
        return ResponseEntity.ok(stock);
    }

    /**
     * GET /stocks/producto/{productoId}
     *
     * Obtener stock de un PRODUCTO específico (por productoId de ms-catalogo).
     *
     * Ejemplo:
     *   GET /stocks/producto/5
     *
     * Útil para: ms-carrito quiere saber "¿cuánto stock hay del producto 5?"
     *
     * @param productoId ID del producto (referencia a ms-catalogo)
     * @return StockResponseDTO
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<StockResponseDTO> obtenerPorProducto(@PathVariable Long productoId) {
        log.info("GET /stocks/producto/{} - Obtener stock por producto", productoId);
        StockResponseDTO stock = stockService.obtenerPorProductoId(productoId);
        return ResponseEntity.ok(stock);
    }

    /**
     * GET /stocks/disponible
     *
     * Verificar si hay DISPONIBILIDAD de un producto.
     *
     * Query parameters:
     *   - productoId: ID del producto (requerido)
     *   - cantidad: cantidad requerida (requerido)
     *
     * Ejemplo:
     *   GET /stocks/disponible?productoId=5&cantidad=10
     *
     * Respuesta HTTP 200:
     * {
     *   "disponible": true,
     *   "productoId": 5,
     *   "cantidadRequerida": 10,
     *   "cantidadActual": 100
     * }
     *
     * Utilidad: ms-carrito verifica antes de agregar al carrito
     *
     * @param productoId ID del producto
     * @param cantidad cantidad requerida
     * @return Map con disponibilidad
     */
    @GetMapping("/disponible")
    public ResponseEntity<java.util.Map<String, Object>> verificarDisponibilidad(
            @RequestParam(name = "productoId") Long productoId,
            @RequestParam(name = "cantidad") Integer cantidad) {

        log.info("GET /stocks/disponible - productoId={} cantidad={}", productoId, cantidad);

        boolean disponible = stockService.estaDisponible(productoId, cantidad);

        // Construir respuesta con información util
        java.util.Map<String, Object> respuesta = new java.util.HashMap<>();
        respuesta.put("disponible", disponible);
        respuesta.put("productoId", productoId);
        respuesta.put("cantidadRequerida", cantidad);

        // Si está disponible, también devolvemos la cantidad actual (informativo)
        if (disponible) {
            try {
                StockResponseDTO stock = stockService.obtenerPorProductoId(productoId);
                respuesta.put("cantidadActual", stock.getCantidadDisponible());
                respuesta.put("stockMinimo", stock.getStockMinimo());
            } catch (Exception e) {
                log.debug("No se pudo obtener detalles de stock", e);
            }
        }

        return ResponseEntity.ok(respuesta);
    }

    /* ====================================
       ENDPOINTS DE CREACIÓN (POST)
       ==================================== */

    /**
     * POST /stocks
     *
     * Crear un nuevo stock.
     *
     * Request body (JSON):
     * {
     *   "productoId": 5,
     *   "cantidadDisponible": 100,
     *   "stockMinimo": 10
     * }
     *
     * Validaciones (@Valid):
     *   - productoId: obligatorio, > 0
     *   - cantidadDisponible: obligatorio, >= 0
     *   - stockMinimo: obligatorio, >= 0
     *
     * Respuesta HTTP 201 CREATED:
     * {
     *   "id": 1,
     *   "productoId": 5,
     *   "cantidadDisponible": 100,
     *   "stockMinimo": 10,
     *   "fechaCreacion": "2026-05-16T10:30:45.123",
     *   "fechaActualizacion": "2026-05-16T10:30:45.123"
     * }
     *
     * Errores posibles:
     *   - HTTP 400: DTO inválido (validación)
     *   - HTTP 409: Stock ya existe para ese producto
     *   - HTTP 502: Producto no existe en ms-catalogo
     *
     * @param dto datos para crear stock
     * @return StockResponseDTO creado (HTTP 201)
     */
    @PostMapping
    public ResponseEntity<StockResponseDTO> crearStock(@Valid @RequestBody StockCreateDTO dto) {
        log.info("POST /stocks - Crear stock para productoId={}", dto.getProductoId());
        StockResponseDTO creado = stockService.crearStock(dto);

        // HTTP 201 CREATED con URL del recurso creado en Location header
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

    /* ====================================
       ENDPOINTS DE ACTUALIZACIÓN (PUT)
       ==================================== */

    /**
     * PUT /stocks/aumentar
     *
     * Aumentar cantidad de stock (ENTRADA).
     *
     * Registra movimiento tipo ENTRADA en tabla movimiento_stock.
     *
     * Request body (JSON):
     * {
     *   "stockId": 1,
     *   "cantidad": 50,
     *   "observacion": "Compra proveedor XYZ"
     * }
     *
     * Respuesta HTTP 200:
     * {
     *   "id": 1,
     *   "productoId": 5,
     *   "cantidadDisponible": 150,  (antes era 100)
     *   "stockMinimo": 10,
     *   "fechaActualizacion": "2026-05-16T10:35:20.123"
     * }
     *
     * Errores:
     *   - HTTP 400: cantidad <= 0, stock no encontrado, validación
     *   - HTTP 404: stock no existe
     *
     * @param dto datos de entrada
     * @return StockResponseDTO actualizado
     */
    @PutMapping("/aumentar")
    public ResponseEntity<StockResponseDTO> aumentarStock(@Valid @RequestBody AumentarStockDTO dto) {
        log.info("PUT /stocks/aumentar - stockId={} cantidad={}", dto.getStockId(), dto.getCantidad());
        StockResponseDTO actualizado = stockService.aumentarStock(
                dto.getStockId(),
                dto.getCantidad(),
                dto.getObservacion()
        );
        return ResponseEntity.ok(actualizado);
    }

    /**
     * PUT /stocks/disminuir
     *
     * Disminuir cantidad de stock (SALIDA).
     *
     * Se usa cuando hay una VENTA o MOVIMIENTO DE SALIDA.
     * Valida que no quede negativo.
     * Registra movimiento tipo SALIDA.
     *
     * Request body (JSON):
     * {
     *   "stockId": 1,
     *   "cantidad": 10,
     *   "observacion": "Venta pedido #456"
     * }
     *
     * Respuesta HTTP 200:
     * {
     *   "id": 1,
     *   "productoId": 5,
     *   "cantidadDisponible": 90,  (antes era 100)
     *   ...
     * }
     *
     * Errores:
     *   - HTTP 400: cantidad > disponible, cantidad <= 0
     *   - HTTP 404: stock no existe
     *
     * Ejemplo de respuesta error 400 (stock insuficiente):
     * {
     *   "status": 400,
     *   "mensaje": "Stock insuficiente para el movimiento",
     *   "cantidadDisponible": 50,
     *   "cantidadSolicitada": 100,
     *   "diferencia": 50,
     *   ...
     * }
     *
     * @param dto datos de salida
     * @return StockResponseDTO actualizado
     */
    @PutMapping("/disminuir")
    public ResponseEntity<StockResponseDTO> disminuirStock(@Valid @RequestBody DisminuirStockDTO dto) {
        log.info("PUT /stocks/disminuir - stockId={} cantidad={}", dto.getStockId(), dto.getCantidad());
        StockResponseDTO actualizado = stockService.disminuirStock(
                dto.getStockId(),
                dto.getCantidad(),
                dto.getObservacion()
        );
        return ResponseEntity.ok(actualizado);
    }

    /**
     * PUT /stocks/ajustar
     *
     * Ajustar cantidad a un valor específico (operación manual).
     *
     * Útil para correcciones por inventario físico o errores.
     *
     * Request body (JSON):
     * {
     *   "stockId": 1,
     *   "nuevoValor": 75,
     *   "observacion": "Corrección por inventario físico"
     * }
     *
     * Respuesta HTTP 200:
     * {
     *   "id": 1,
     *   "productoId": 5,
     *   "cantidadDisponible": 75,  (se fijó directamente a este valor)
     *   ...
     * }
     *
     * Errores:
     *   - HTTP 400: nuevoValor < 0
     *   - HTTP 404: stock no existe
     *
     * @param dto datos de ajuste
     * @return StockResponseDTO actualizado
     */
    @PutMapping("/ajustar")
    public ResponseEntity<StockResponseDTO> ajustarStock(@Valid @RequestBody AjustarStockDTO dto) {
        log.info("PUT /stocks/ajustar - stockId={} nuevoValor={}", dto.getStockId(), dto.getNuevoValor());
        StockResponseDTO actualizado = stockService.ajustarStock(
                dto.getStockId(),
                dto.getNuevoValor(),
                dto.getObservacion()
        );
        return ResponseEntity.ok(actualizado);
    }
}