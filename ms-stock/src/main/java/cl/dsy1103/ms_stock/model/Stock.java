package cl.dsy1103.ms_stock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa el Stock de un producto.
 *
 * Esta tabla almacena:
 * - Cantidad disponible de cada producto
 * - Stock mínimo (para alertas)
 * - Timestamps de creación y actualización
 *
 * Nota: NO almacenamos la información del producto aquí.
 *       Solo guardamos el ID del producto.
 *       La información (nombre, precio) viene de ms-catalogo mediante WebClient.
 */
@Entity
@Table(name = "stock")  // Tabla en MySQL
@Data               // Lombok: genera getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Lombok: constructor sin argumentos (requiere JPA)
@AllArgsConstructor // Lombok: constructor con todos los campos
@Builder            // Lombok: patrón Builder para crear objetos fácilmente
public class Stock {

    /**
     * ID único del stock.
     * Se auto-incrementa automáticamente en la BD.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del producto (referencia a ms-catalogo).
     *
     * NO es una relación JPA (@ManyToOne) porque es otro microservicio.
     * Solo guardamos el ID del producto.
     * Si necesitamos info del producto, llamamos ms-catalogo con WebClient.
     */
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    /**
     * Cantidad disponible del producto.
     * Nunca puede ser negativa.
     */
    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;

    /**
     * Stock mínimo recomendado.
     * Cuando cantidadDisponible < stockMinimo, se debería alertar.
     */
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    /**
     * Fecha de creación del registro.
     * Se guarda automáticamente al insertar.
     */
    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false, nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha de última actualización.
     * Se actualiza automáticamente cada vez que cambios el registro.
     */
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}