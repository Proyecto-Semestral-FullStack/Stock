package com.dsy1103.gestion_de_inventario.service;

import com.dsy1103.gestion_de_inventario.dto.ProductoRequestDTO;
import com.dsy1103.gestion_de_inventario.dto.ProductoResponseDTO;
import com.dsy1103.gestion_de_inventario.model.Categoria;
import com.dsy1103.gestion_de_inventario.model.Producto;
import com.dsy1103.gestion_de_inventario.repository.CategoriaRepository;
import com.dsy1103.gestion_de_inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    private ProductoResponseDTO mapToDTO(Producto producto){
        return new ProductoResponseDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getCategoria().getNombre()
        );
    }

    public List<ProductoResponseDTO> obtenerTodos(){
        return productoRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<ProductoResponseDTO> obtenerPorId(Long id){
        return  productoRepository.findById(id).map(this::mapToDTO);
    }

    public ProductoResponseDTO guardar(ProductoRequestDTO dto){
        Categoria categoria = categoriaRepository
                .findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException(
                        "Categoría NO encontrada con id: " + dto.getCategoriaId()));

        Producto producto = new Producto(
                null,
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getPrecio(),
                categoria
        );
        return  mapToDTO(productoRepository.save(producto));
    }

    public Optional<ProductoResponseDTO> actualizar(Long id, ProductoRequestDTO dto){
        return productoRepository.findById(id).map(existente -> {
            Categoria categoria = categoriaRepository
                    .findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException(
                            "Categoría NO encontrada con id: " + dto.getCategoriaId()));
            existente.setNombre(dto.getNombre());
            existente.setDescripcion(dto.getDescripcion());
            existente.setPrecio(dto.getPrecio());
            existente.setCategoria(categoria);
            return mapToDTO(productoRepository.save(existente));
        });
    }

    public void eliminar(Long id){
        productoRepository.deleteById(id);
    }

    public List<ProductoResponseDTO> buscarPorTitulo(String texto) {
        return productoRepository.findByNombreContainingIgnoreCase(texto)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductoResponseDTO> buscarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductoResponseDTO> buscarBajoPresupuesto(Double precioMax) {
        return productoRepository.findProductosBajoPresupuesto(precioMax)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
}
