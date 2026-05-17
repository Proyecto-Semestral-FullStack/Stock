package cl.dsy1103.ms_stock.exception;

/**
 * Se lanza cuando se intenta acceder a un stock que no existe.
 *
 * Códigos HTTP: 404 Not Found
 *
 * Ejemplos:
 * - GET /stocks/999 (ID no existe)
 * - GET /stocks/producto/999 (producto no existe en stock)
 */
public class StockNoEncontradoException extends StockException {

    public StockNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public StockNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}