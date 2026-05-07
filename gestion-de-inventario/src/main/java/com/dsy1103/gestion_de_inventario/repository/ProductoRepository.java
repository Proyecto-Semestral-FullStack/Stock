package com.dsy1103.gestion_de_inventario.repository;

import com.dsy1103.gestion_de_inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByPrecioLessThan(Double precio);

    List<Producto> findByPrecioBetween(Double min, Double max);

    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId")
    List<Producto> findByCategoriaId(@Param("categoriaId") Long categoriaId);

    @Query("SELECT p FROM Producto p WHERE p.precio <= :precioMax ORDER BY p.precio DESC")
    List<Producto> findProductosBajoPresupuesto(@Param("precioMax") Double precioMax);
}
