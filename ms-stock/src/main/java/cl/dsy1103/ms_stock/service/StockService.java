package cl.dsy1103.ms_stock.service;

import cl.dsy1103.ms_stock.config.CatalogoClient;
import cl.dsy1103.ms_stock.dto.MovimientoStockResponseDTO;
import cl.dsy1103.ms_stock.dto.StockCreateDTO;
import cl.dsy1103.ms_stock.dto.StockResponseDTO;
import cl.dsy1103.ms_stock.dto.StockUpdateDTO;
import cl.dsy1103.ms_stock.exception.CatalogoException;
import cl.dsy1103.ms_stock.exception.OperacionInvalidaException;
import cl.dsy1103.ms_stock.exception.StockDuplicadoException;
import cl.dsy1103.ms_stock.exception.StockInsuficienteException;
import cl.dsy1103.ms_stock.exception.StockNoEncontradoException;
import cl.dsy1103.ms_stock.model.MovimientoStock;
import cl.dsy1103.ms_stock.model.Stock;
import cl.dsy1103.ms_stock.model.TipoMovimiento;
import cl.dsy1103.ms_stock.repository.MovimientoStockRepository;
import cl.dsy1103.ms_stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Service: marca la clase como componente de servicio de Spring.
 *            Spring la detecta durante el escaneo y permite inyección.
 * @Slf4j:  (Lombok) añade un logger 'log' para mensajes informativos/errores.
 * @RequiredArgsConstructor: (Lombok) crea constructor con los campos final necesarios
 *                          (recomendado para inyección por constructor).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    // Repositorios y cliente inyectados (constructor generado por Lombok)
    private final StockRepository stockRepository;
    private final MovimientoStockRepository movimientoRepository;
    private final CatalogoClient catalogoClient;

    /* ============================
       Métodos de consulta (sin @Transactional)
       Las operaciones de solo lectura no necesitan transacción explícita.
       ============================ */

    /**
     * Obtener todos los stocks.
     * @return lista de StockResponseDTO
     */
    public List<StockResponseDTO> listarStocks() {
        log.debug("Listando todos los stocks");
        return stockRepository.findAll().stream()
                .map(StockResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Obtener stock por ID de stock.
     * Si no existe, lanza StockNoEncontradoException (404).
     * @param id id del stock
     * @return StockResponseDTO
     */
    public StockResponseDTO obtenerPorId(Long id) {
        log.debug("Obtener stock por id: {}", id);
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNoEncontradoException("Stock con ID " + id + " no encontrado"));
        return StockResponseDTO.from(stock);
    }

    /**
     * Obtener stock por productoId.
     * Si no existe, lanza StockNoEncontradoException.
     * @param productoId id del producto
     * @return StockResponseDTO
     */
    public StockResponseDTO obtenerPorProductoId(Long productoId) {
        log.debug("Obtener stock por productoId: {}", productoId);
        Stock stock = stockRepository.findByProductoId(productoId)
                .orElseThrow(() -> new StockNoEncontradoException("No hay stock para producto " + productoId));
        return StockResponseDTO.from(stock);
    }

    /* ============================
       Métodos que modifican BD -> deben ser @Transactional
       (aumentan/disminuyen/ajustan y guardan movimiento)
       ============================ */

    /**
     * Crear un nuevo stock.
     * - Verifica que el producto exista en ms-catalogo (evita stocks para productos inexistentes)
     * - Verifica que no exista stock para el mismo producto (evita duplicados)
     * - Persiste la entidad Stock
     *
     * @param dto datos para crear stock (StockCreateDTO)
     * @return StockResponseDTO creado
     */
    @Transactional
    public StockResponseDTO crearStock(StockCreateDTO dto) {
        log.info("Creando stock para productoId={}", dto.getProductoId());

        // 1) Validar en ms-catalogo (si falla se lanza CatalogoException)
        try {
            catalogoClient.obtenerProducto(dto.getProductoId());
        } catch (CatalogoException e) {
            // Propagar la excepción ya tipada para que el handler global la capture
            log.warn("No se puede crear stock: producto no existe o error en catálogo: {}", e.getMessage());
            throw e;
        }

        // 2) Evitar duplicados
        if (stockRepository.existsByProductoId(dto.getProductoId())) {
            throw new StockDuplicadoException("Ya existe stock registrado para productoId " + dto.getProductoId());
        }

        // 3) Construir entidad y persistir
        Stock stock = Stock.builder()
                .productoId(dto.getProductoId())
                .cantidadDisponible(dto.getCantidadDisponible())
                .stockMinimo(dto.getStockMinimo())
                .build();

        Stock guardado = stockRepository.save(stock);

        log.info("Stock creado con id={} para productoId={}", guardado.getId(), guardado.getProductoId());
        return StockResponseDTO.from(guardado);
    }

    /**
     * Aumentar stock.
     * - Crea un movimiento de tipo ENTRADA
     * - Actualiza cantidadDisponible
     *
     * @param stockId id del stock
     * @param cantidad cantidad a aumentar (debe ser > 0)
     * @param observacion observación opcional
     * @return StockResponseDTO actualizado
     */
    @Transactional
    public StockResponseDTO aumentarStock(Long stockId, int cantidad, String observacion) {
        log.info("Aumentando stock id={} cantidad={}", stockId, cantidad);

        if (cantidad <= 0) {
            throw new OperacionInvalidaException("La cantidad a aumentar debe ser mayor a cero");
        }

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNoEncontradoException("Stock con ID " + stockId + " no encontrado"));

        // Actualizar cantidad
        int nuevaCantidad = stock.getCantidadDisponible() + cantidad;
        stock.setCantidadDisponible(nuevaCantidad);
        stock.setFechaActualizacion(LocalDateTime.now()); // opcional, UpdateTimestamp lo hace automáticamente

        // Persistir stock (save actualiza)
        stockRepository.save(stock);

        // Registrar movimiento
        MovimientoStock movimiento = MovimientoStock.builder()
                .stock(stock)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(cantidad)
                .observacion(observacion)
                .build();
        movimientoRepository.save(movimiento);

        log.debug("Stock id={} aumentado a {}", stockId, nuevaCantidad);
        return StockResponseDTO.from(stock);
    }

    /**
     * Disminuir stock.
     * - Verifica suficiente cantidad (no permitir negativo)
     * - Crea movimiento SALIDA
     *
     * @param stockId id del stock
     * @param cantidad cantidad a disminuir (debe ser > 0)
     * @param observacion observación opcional
     * @return StockResponseDTO actualizado
     */
    @Transactional
    public StockResponseDTO disminuirStock(Long stockId, int cantidad, String observacion) {
        log.info("Disminuyendo stock id={} cantidad={}", stockId, cantidad);

        if (cantidad <= 0) {
            throw new OperacionInvalidaException("La cantidad a disminuir debe ser mayor a cero");
        }

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNoEncontradoException("Stock con ID " + stockId + " no encontrado"));

        int disponible = stock.getCantidadDisponible();
        if (disponible < cantidad) {
            // Lanzar excepción con detalles (esto será manejado por el GlobalExceptionHandler)
            throw new StockInsuficienteException("Stock insuficiente para el movimiento", disponible, cantidad);
        }

        // Actualizar cantidad
        stock.setCantidadDisponible(disponible - cantidad);
        stock.setFechaActualizacion(LocalDateTime.now());
        stockRepository.save(stock);

        // Registrar movimiento SALIDA
        MovimientoStock movimiento = MovimientoStock.builder()
                .stock(stock)
                .tipoMovimiento(TipoMovimiento.SALIDA)
                .cantidad(cantidad)
                .observacion(observacion)
                .build();
        movimientoRepository.save(movimiento);

        log.debug("Stock id={} disminuido a {}", stockId, stock.getCantidadDisponible());
        return StockResponseDTO.from(stock);
    }

    /**
     * Ajustar stock (operación manual a cualquier valor).
     * - Permite fijar la cantidad disponible a un nuevo valor no negativo.
     * - Registra movimiento tipo AJUSTE (la cantidad registrada será el valor absoluto del ajuste).
     *
     * @param stockId id del stock
     * @param nuevoValor valor final deseado (>= 0)
     * @param observacion motivo del ajuste
     * @return StockResponseDTO actualizado
     */
    @Transactional
    public StockResponseDTO ajustarStock(Long stockId, int nuevoValor, String observacion) {
        log.info("Ajustando stock id={} a nuevoValor={}", stockId, nuevoValor);

        if (nuevoValor < 0) {
            throw new OperacionInvalidaException("El nuevo valor de stock no puede ser negativo");
        }

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNoEncontradoException("Stock con ID " + stockId + " no encontrado"));

        int anterior = stock.getCantidadDisponible();
        if (anterior == nuevoValor) {
            log.debug("Ajuste no modifica cantidad (anterior == nuevo). No se registra movimiento.");
            return StockResponseDTO.from(stock);
        }

        // Actualizar stock
        stock.setCantidadDisponible(nuevoValor);
        stock.setFechaActualizacion(LocalDateTime.now());
        stockRepository.save(stock);

        // Registrar movimiento de ajuste. Guardamos la cantidad absoluta del cambio (o puedes guardar nuevo valor)
        int diferencia = Math.abs(nuevoValor - anterior);
        MovimientoStock movimiento = MovimientoStock.builder()
                .stock(stock)
                .tipoMovimiento(TipoMovimiento.AJUSTE)
                .cantidad(diferencia)
                .observacion(observacion != null ? observacion : "Ajuste manual")
                .build();
        movimientoRepository.save(movimiento);

        log.debug("Stock id={} ajustado de {} a {}", stockId, anterior, nuevoValor);
        return StockResponseDTO.from(stock);
    }

    /**
     * Verificar disponibilidad: devuelve true si hay al menos 'cantidad' disponible para productoId.
     * - Se busca stock por productoId y se compara.
     *
     * @param productoId id del producto (referencia a ms-catalogo)
     * @param cantidad requerida (>= 1)
     * @return true si disponible, false si no
     */
    public boolean estaDisponible(Long productoId, int cantidad) {
        log.debug("Verificando disponibilidad productoId={} cantidad={}", productoId, cantidad);

        if (cantidad <= 0) {
            throw new OperacionInvalidaException("La cantidad requerida debe ser mayor a cero");
        }

        return stockRepository.findByProductoId(productoId)
                .map(stock -> stock.getCantidadDisponible() >= cantidad)
                .orElse(false); // Si no hay stock registrado, no está disponible
    }
}