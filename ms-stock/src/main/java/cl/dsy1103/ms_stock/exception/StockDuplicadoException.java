package cl.dsy1103.ms_stock.exception;

/**
 * Se lanza cuando se intenta crear un stock que YA EXISTE.
 *
 * Códigos HTTP: 409 Conflict
 *
 * Ejemplo:
 * - POST /stocks con productoId que ya tiene stock registrado
 */
public class StockDuplicadoException extends StockException {

    public StockDuplicadoException(String mensaje) {
        super(mensaje);
    }

    public StockDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}