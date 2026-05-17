package cl.dsy1103.ms_stock.exception;

/**
 * Se lanza cuando NO hay suficiente stock para una operación.
 *
 * Códigos HTTP: 400 Bad Request
 *
 * Ejemplos:
 * - PUT /stocks/disminuir con cantidad > disponible
 * - POST /vender cuando cantidad > stock
 *
 * Información importante:
 * - Cuánto se disponible
 * - Cuánto se solicitó
 * - La diferencia
 */
public class StockInsuficienteException extends StockException {

    private Integer cantidadDisponible;
    private Integer cantidadSolicitada;

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con detalles de cantidad.
     */
    public StockInsuficienteException(
            String mensaje,
            Integer cantidadDisponible,
            Integer cantidadSolicitada
    ) {
        super(mensaje);
        this.cantidadDisponible = cantidadDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    // Getters para acceder a los datos
    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public Integer getDiferencia() {
        return cantidadSolicitada - cantidadDisponible;
    }
}