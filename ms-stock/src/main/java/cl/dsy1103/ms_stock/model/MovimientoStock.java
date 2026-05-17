package cl.dsy1103.ms_stock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que registra cada movimiento de stock.
 *
 * Esto es un AUDITORÍA. Cada vez que cambia el stock, guardamos:
 * - Qué stock cambió
 * - Qué tipo de movimiento fue (entrada, salida, ajuste)
 * - Cuánto cambió
 * - Cuándo fue
 * - Por qué (observación)
 *
 * Utilidad:
 * - Auditoría: saber quién/cuándo/por qué cambió stock
 * - Reportes: historial completo de movimientos
 * - Debugging: si hay discrepancia, ver qué pasó
 */
@Entity
@Table(name = "movimiento_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStock {

    /**
     * ID único del movimiento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Stock al que pertenece este movimiento.
     * Relación: Un Stock tiene muchos Movimientos.
     * Cuando eliminas un Stock, se eliminan sus movimientos automáticamente (CASCADE).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    /**
     * Tipo de movimiento: ENTRADA, SALIDA, AJUSTE.
     */
    @Column(name = "tipo_movimiento", nullable = false)
    @Enumerated(EnumType.STRING)  // Guardar como texto en BD: "ENTRADA", "SALIDA"
    private TipoMovimiento tipoMovimiento;

    /**
     * Cantidad que se movió.
     * Siempre positiva (el tipo indica si es entrada o salida).
     */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /**
     * Fecha del movimiento.
     * Se guarda automáticamente.
     */
    @CreationTimestamp
    @Column(name = "fecha", updatable = false, nullable = false)
    private LocalDateTime fecha;

    /**
     * Observación opcional (motivo del movimiento).
     * Ejemplos:
     * - "Venta pedido #123"
     * - "Devolución por defecto"
     * - "Compra a proveedor XYZ"
     * - "Ajuste por inventario física"
     */
    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;
}