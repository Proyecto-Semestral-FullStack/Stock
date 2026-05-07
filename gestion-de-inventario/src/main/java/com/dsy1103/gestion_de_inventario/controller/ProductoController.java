package com.dsy1103.gestion_de_inventario.controller;

import com.dsy1103.gestion_de_inventario.dto.ProductoResponseDTO;
import com.dsy1103.gestion_de_inventario.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodos(){
        return  ResponseEntity.ok(productoService.obtenerTodos());
    }
}
