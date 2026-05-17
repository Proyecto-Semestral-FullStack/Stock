package cl.dsy1103.ms_stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import cl.dsy1103.ms_stock.model.Stock;

import java.time.LocalDateTime;

/**
 * DTO para RESPONDER con datos de Stock.
 *
 * Este es el objeto que el servidor envía al cliente.
 *
 * Aquí incluimos:
 * - ID (generado por servidor)
 * - Datos del stock
 * - Fechas de auditoría
 *
 * NOT incluimos:
 * - Información del producto (eso lo pide el cliente a ms-catalogo)
 *
 * Ejemplo de response HTTP 200:
 * {
 *   "id": 1,
 *   "productoId": 5,
 *   "cantidadDisponible": 100,
 *   "stockMinimo": 10,
 *   "fechaCreacion": "2026-05-16T10:30:45.123",
 *   "fechaActualizacion": "2026-05-16T10:30:45.123"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponseDTO {

    private Long id;
    private Long productoId;
    private Integer cantidadDisponible;
    private Integer stockMinimo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    /**
     * Método HELPER para convertir una entidad Stock a DTO.
     *
     * Este patrón es común: transforma la entidad JPA normal en DTO seguro.
     *
     * Uso en el Service:
     *   Stock stock = stockRepository.findById(1L).orElseThrow(...);
     *   StockResponseDTO dto = StockResponseDTO.from(stock);
     *   return dto;
     */
    public static StockResponseDTO from(Stock stock) {
        return StockResponseDTO.builder()
                .id(stock.getId())
                .productoId(stock.getProductoId())
                .cantidadDisponible(stock.getCantidadDisponible())
                .stockMinimo(stock.getStockMinimo())
                .fechaCreacion(stock.getFechaCreacion())
                .fechaActualizacion(stock.getFechaActualizacion())
                .build();
    }
}