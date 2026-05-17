package cl.dsy1103.ms_stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import cl.dsy1103.ms_stock.model.MovimientoStock;
import cl.dsy1103.ms_stock.model.TipoMovimiento;

import java.time.LocalDateTime;

/**
 * DTO para RESPONDER con datos de MovimientoStock.
 *
 * Ejemplo de response HTTP 200:
 * {
 *   "id": 10,
 *   "stockId": 1,
 *   "tipoMovimiento": "SALIDA",
 *   "cantidad": 5,
 *   "fecha": "2026-05-16T10:30:45.123",
 *   "observacion": "Venta pedido #123"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStockResponseDTO {

    private Long id;
    private Long stockId;
    private TipoMovimiento tipoMovimiento;
    private Integer cantidad;
    private LocalDateTime fecha;
    private String observacion;

    /**
     * Convertir entidad a DTO.
     */
    public static MovimientoStockResponseDTO from(MovimientoStock movimiento) {
        return MovimientoStockResponseDTO.builder()
                .id(movimiento.getId())
                .stockId(movimiento.getStock().getId())  // Stock ID del movimiento
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .cantidad(movimiento.getCantidad())
                .fecha(movimiento.getFecha())
                .observacion(movimiento.getObservacion())
                .build();
    }
}