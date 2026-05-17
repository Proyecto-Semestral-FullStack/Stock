package cl.dsy1103.ms_stock.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para CREAR un Stock.
 *
 * Este es el objeto que el cliente envía en POST /stocks
 *
 * Ejemplo de request:
 * {
 *   "productoId": 5,
 *   "cantidadDisponible": 100,
 *   "stockMinimo": 10
 * }
 *
 * Validaciones:
 * - productoId: obligatorio y > 0
 * - cantidadDisponible: obligatorio y >= 0 (puede ser 0)
 * - stockMinimo: obligatorio y >= 0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockCreateDTO {

    /**
     * ID del producto en ms-catalogo.
     *
     * @NotNull: No puede ser null
     * @Positive: Debe ser mayor a 0
     */
    @NotNull(message = "El ID del producto es obligatorio")
    @Positive(message = "El ID del producto debe ser mayor a 0")
    private Long productoId;

    /**
     * Cantidad disponible inicial.
     *
     * @NotNull: Obligatorio
     * @PositiveOrZero: Puede ser 0 o más (no negativo)
     */
    @NotNull(message = "La cantidad disponible es obligatoria")
    @PositiveOrZero(message = "La cantidad disponible no puede ser negativa")
    private Integer cantidadDisponible;

    /**
     * Stock mínimo recomendado.
     * Cuando cantidad < stockMinimo, se debe alertar.
     *
     * @NotNull: Obligatorio
     * @PositiveOrZero: Puede ser 0 o más
     */
    @NotNull(message = "El stock mínimo es obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
}