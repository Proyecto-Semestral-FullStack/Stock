package cl.dsy1103.ms_stock.exception;

/**
 * Excepción BASE para todo lo relacionado con Stock.
 *
 * Todas las otras excepciones heredan de esta.
 *
 * ¿Por qué una clase base?
 * - Podemos capturar TODAS las excepciones de stock con: catch (StockException e)
 * - Fácil de mantener: cambio un lugar, afecta a todas
 * - Consistencia: todas siguen el mismo patrón
 */
public class StockException extends RuntimeException {

    /**
     * Constructor simple con mensaje.
     * @param mensaje descripción del error
     */
    public StockException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa (otra excepción).
     * @param mensaje descripción del error
     * @param causa excepción que causó este error
     */
    public StockException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}