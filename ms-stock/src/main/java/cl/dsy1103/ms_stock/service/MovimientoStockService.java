package cl.dsy1103.ms_stock.service;

import cl.dsy1103.ms_stock.dto.MovimientoStockResponseDTO;
import cl.dsy1103.ms_stock.exception.StockNoEncontradoException;
import cl.dsy1103.ms_stock.model.MovimientoStock;
import cl.dsy1103.ms_stock.repository.MovimientoStockRepository;
import cl.dsy1103.ms_stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para operaciones de consulta sobre movimientos de stock.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MovimientoStockService {

    private final MovimientoStockRepository movimientoRepository;
    private final StockRepository stockRepository;

    /**
     * Obtener todos los movimientos (paginación puede implementarse si se necesita).
     * @return lista de MovimientoStockResponseDTO
     */
    public List<MovimientoStockResponseDTO> listarMovimientos() {
        log.debug("Listando todos los movimientos de stock");
        return movimientoRepository.findAll().stream()
                .map(MovimientoStockResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Obtener movimientos por stockId.
     * Si el stock no existe, lanzamos StockNoEncontradoException.
     * @param stockId id del stock
     * @return lista de MovimientoStockResponseDTO
     */
    public List<MovimientoStockResponseDTO> listarPorStockId(Long stockId) {
        log.debug("Listando movimientos para stockId={}", stockId);

        if (!stockRepository.existsById(stockId)) {
            throw new StockNoEncontradoException("Stock con ID " + stockId + " no encontrado");
        }

        return movimientoRepository.findByStockIdOrderByFechaDesc(stockId).stream()
                .map(MovimientoStockResponseDTO::from)
                .collect(Collectors.toList());
    }
}