package cl.dsy1103.ms_stock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para CREAR un MovimientoStock.
 *
 * Se envía cuando hay cambios en el stock:
 * - Venta (SALIDA)
 * - Compra a proveedor (ENTRADA)
 * - Ajuste por inventario (AJUSTE)
 *
 * Ejemplo de request POST /movimientos:
 * {
 *   "stockId": 1,
 *   "tipoMovimiento": "SALIDA",
 *   "cantidad": 5,
 *   "observacion": "Venta pedido #123"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStockCreateDTO {

    /**
     * ID del stock que se mueve.
     */
    @NotNull(message = "El ID del stock es obligatorio")
    @Positive(message = "El ID del stock debe ser mayor a 0")
    private Long stockId;

    /**
     * Tipo de movimiento: ENTRADA, SALIDA, AJUSTE
     *
     * Para que funcione con Enum:
     * El cliente envía: "ENTRADA" (texto)
     * Spring lo convierte automáticamente a: TipoMovimiento.ENTRADA
     */
    @NotBlank(message = "El tipo de movimiento es obligatorio (Tipo de movimiento: ENTRADA, SALIDA, AJUSTE)")
    private String tipoMovimiento;

    /**
     * Cantidad que se mueve.
     * Siempre positiva (el tipo indica si es entrada o salida).
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    /**
     * Observación del movimiento (opcional).
     * Máximo 500 caracteres.
     */
    @Size(max = 500, message = "La observación no puede tener más de 500 caracteres")
    private String observacion;
}