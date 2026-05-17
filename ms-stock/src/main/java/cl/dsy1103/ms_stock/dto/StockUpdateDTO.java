package cl.dsy1103.ms_stock.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para ACTUALIZAR un Stock.
 *
 * Este es el objeto que el cliente envía en PUT /stocks/{id}
 *
 * Aquí los campos son OPCIONALES porque el cliente puede actualizar
 * solo algunos campos sin pasar todos.
 *
 * Ejemplo de request:
 * {
 *   "stockMinimo": 20
 * }
 * Solo actualiza el stock mínimo, deja los demás igual.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateDTO {

    /**
     * Stock mínimo (opcional).
     * Si no se envía, se mantiene el anterior.
     */
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
}