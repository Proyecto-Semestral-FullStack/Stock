package com.dsy1103.gestion_de_inventario.repository;

import com.dsy1103.gestion_de_inventario.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
