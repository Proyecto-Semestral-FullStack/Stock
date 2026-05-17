package cl.dsy1103.ms_stock.exception;

/**
 * Se lanza cuando hay error comunicándose con ms-catalogo.
 *
 * Códigos HTTP: 502 Bad Gateway o 503 Service Unavailable
 *
 * Ejemplos:
 * - ms-catalogo no responde
 * - ms-catalogo devuelve error
 * - Producto no existe en ms-catalogo
 * - Timeout al llamar ms-catalogo
 */
public class CatalogoException extends StockException {

    public CatalogoException(String mensaje) {
        super(mensaje);
    }

    public CatalogoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}