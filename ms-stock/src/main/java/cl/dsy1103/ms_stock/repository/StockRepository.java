package cl.dsy1103.ms_stock.repository;

import cl.dsy1103.ms_stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository para acceder a la tabla STOCK en la BD.
 *
 * Hereda de JpaRepository que proporciona:
 * - findById(id)
 * - findAll()
 * - save(entity)
 * - delete(entity)
 * - deleteById(id)
 *
 * Además, definimos métodos personalizados que necesitamos.
 *
 * Genéricos: <Stock, Long>
 *   - Stock: clase entidad
 *   - Long: tipo del @Id (clave primaria)
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    /**
     * Buscar stock por ID del producto.
     *
     * Spring genera automáticamente:
     * SELECT * FROM stock WHERE producto_id = ?
     *
     * @param productoId ID del producto en ms-catalogo
     * @return Optional<Stock> (puede no existir)
     *
     * Uso en Service:
     *   Optional<Stock> stock = stockRepository.findByProductoId(5L);
     *   if (stock.isPresent()) { ... }
     */
    Optional<Stock> findByProductoId(Long productoId);

    /**
     * Buscar todos los stocks con cantidad disponible menor a cierto valor.
     *
     * Spring genera:
     * SELECT * FROM stock WHERE cantidad_disponible < ?
     *
     * Utilidad: encontrar productos que necesitan reabastecimiento
     *
     * @param cantidad límite
     * @return lista de stocks con baja cantidad
     */
    List<Stock> findByCantidadDisponibleLessThan(Integer cantidad);

    /**
     * Buscar todos los stocks donde cantidad < stockMinimo.
     *
     * Spring genera:
     * SELECT * FROM stock WHERE cantidad_disponible < stock_minimo
     *
     * Utilidad: alertas de stock bajo
     *
     * @return lista de stocks por debajo del mínimo
     */
    List<Stock> findByCantidadDisponibleLessThanEqual(Integer stockMinimo);

    /**
     * Verificar si existe stock para un producto.
     *
     * Spring genera:
     * SELECT COUNT(*) FROM stock WHERE producto_id = ? > 0
     *
     * @param productoId ID del producto
     * @return true si existe, false si no
     */
    boolean existsByProductoId(Long productoId);
}