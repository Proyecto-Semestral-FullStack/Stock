package cl.dsy1103.ms_stock.model;

/**
 * Enum que representa los tipos de movimientos de stock.
 *
 * - ENTRADA: Cuando ingresa stock (compra a proveedor, devolución)
 * - SALIDA: Cuando sale stock (venta, pérdida)
 * - AJUSTE: Cuando se corrige manualmente (inventario, error)
 */
public enum TipoMovimiento {
    ENTRADA,
    SALIDA,
    AJUSTE
}