package cl.dsy1103.ms_stock.repository;

import cl.dsy1103.ms_stock.model.MovimientoStock;
import cl.dsy1103.ms_stock.model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para acceder a la tabla MOVIMIENTO_STOCK.
 *
 * Proporciona métodos para consultar el historial de movimientos.
 * Esto es importante para auditoría y reportes.
 */
@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {

    /**
     * Obtener todos los movimientos de un stock específico.
     *
     * Spring genera:
     * SELECT * FROM movimiento_stock WHERE stock_id = ? ORDER BY fecha DESC
     *
     * @param stockId ID del stock
     * @return lista de movimientos (más recientes primero)
     *
     * Utilidad: ver historial de cambios de un producto
     */
    List<MovimientoStock> findByStockIdOrderByFechaDesc(Long stockId);

    /**
     * Obtener movimientos de un tipo específico.
     *
     * Spring genera:
     * SELECT * FROM movimiento_stock WHERE tipo_movimiento = ?
     *
     * @param tipo ENTRADA, SALIDA, o AJUSTE
     * @return lista de movimientos de ese tipo
     *
     * Utilidad: reportes "cuántas ventas hubo", "cuántas entradas"
     */
    List<MovimientoStock> findByTipoMovimiento(TipoMovimiento tipo);

    /**
     * Obtener movimientos de un stock en un rango de fechas.
     *
     * Spring genera:
     * SELECT * FROM movimiento_stock
     * WHERE stock_id = ? AND fecha BETWEEN ? AND ?
     *
     * @param stockId ID del stock
     * @param fechaInicio inicio del rango
     * @param fechaFin fin del rango
     * @return movimientos en ese período
     *
     * Utilidad: reportes mensuales, semanales
     */
    List<MovimientoStock> findByStockIdAndFechaBetween(
            Long stockId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    /**
     * Contar cuántos movimientos del tipo SALIDA (ventas) hay para un stock.
     *
     * Spring genera:
     * SELECT COUNT(*) FROM movimiento_stock
     * WHERE stock_id = ? AND tipo_movimiento = 'SALIDA'
     *
     * @param stockId ID del stock
     * @return cantidad de salidas/ventas
     */
    Integer countByStockIdAndTipoMovimiento(Long stockId, TipoMovimiento tipo);
}