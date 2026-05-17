package cl.dsy1103.ms_stock.exception;

/**
 * Se lanza cuando una operación es inválida lógicamente.
 *
 * Códigos HTTP: 400 Bad Request
 *
 * Ejemplos:
 * - Cantidad negativa o cero
 * - Tipo de movimiento inválido
 * - Operaciones que violan reglas de negocio
 */
public class OperacionInvalidaException extends StockException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }

    public OperacionInvalidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}