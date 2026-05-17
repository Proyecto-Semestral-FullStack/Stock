package cl.dsy1103.ms_stock.controller;

import cl.dsy1103.ms_stock.dto.MovimientoStockResponseDTO;
import cl.dsy1103.ms_stock.service.MovimientoStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLADOR REST para consultas de MovimientoStock.
 *
 * Proporciona endpoints para ver historial/auditoría de cambios en stock.
 *
 * @RequestMapping("/movimientos"): URL base es /movimientos
 */
@RestController
@RequestMapping("/movimientos")
@Slf4j
@RequiredArgsConstructor
public class MovimientoStockController {

    private final MovimientoStockService movimientoService;

    /**
     * GET /movimientos
     *
     * Obtener TODOS los movimientos de stock (historial completo).
     *
     * Respuesta HTTP 200:
     * [
     *   {
     *     "id": 10,
     *     "stockId": 1,
     *     "tipoMovimiento": "ENTRADA",
     *     "cantidad": 50,
     *     "fecha": "2026-05-16T10:30:45.123",
     *     "observacion": "Compra proveedor XYZ"
     *   },
     *   {
     *     "id": 11,
     *     "stockId": 1,
     *     "tipoMovimiento": "SALIDA",
     *     "cantidad": 10,
     *     "fecha": "2026-05-16T10:35:20.123",
     *     "observacion": "Venta pedido #456"
     *   },
     *   ...
     * ]
     *
     * @return List<MovimientoStockResponseDTO>
     */
    @GetMapping
    public ResponseEntity<List<MovimientoStockResponseDTO>> listarMovimientos() {
        log.info("GET /movimientos - Listando todos los movimientos");
        List<MovimientoStockResponseDTO> movimientos = movimientoService.listarMovimientos();
        return ResponseEntity.ok(movimientos);
    }

    /**
     * GET /movimientos/stock/{stockId}
     *
     * Obtener movimientos de un STOCK específico (historial del producto).
     *
     * Los movimientos se devuelven en orden descendente de fecha (más recientes primero).
     *
     * Ejemplo:
     *   GET /movimientos/stock/1
     *
     * Respuesta HTTP 200:
     * [
     *   {
     *     "id": 11,
     *     "stockId": 1,
     *     "tipoMovimiento": "SALIDA",
     *     "cantidad": 10,
     *     "fecha": "2026-05-16T10:35:20.123",  (MÁS RECIENTE PRIMERO)
     *     "observacion": "Venta pedido #456"
     *   },
     *   {
     *     "id": 10,
     *     "stockId": 1,
     *     "tipoMovimiento": "ENTRADA",
     *     "cantidad": 50,
     *     "fecha": "2026-05-16T10:30:45.123",
     *     "observacion": "Compra proveedor XYZ"
     *   },
     *   ...
     * ]
     *
     * Error HTTP 404 si stock no existe:
     * {
     *   "status": 404,
     *   "mensaje": "Stock con ID 999 no encontrado",
     *   ...
     * }
     *
     * @param stockId ID del stock
     * @return List<MovimientoStockResponseDTO> ordenados por fecha DESC
     */
    @GetMapping("/stock/{stockId}")
    public ResponseEntity<List<MovimientoStockResponseDTO>> listarPorStock(@PathVariable Long stockId) {
        log.info("GET /movimientos/stock/{} - Listando movimientos del stock", stockId);
        List<MovimientoStockResponseDTO> movimientos = movimientoService.listarPorStockId(stockId);
        return ResponseEntity.ok(movimientos);
    }
}