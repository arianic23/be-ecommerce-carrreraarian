package com.ecommerce.demo.controller;

import com.ecommerce.demo.dto.ProductoDTO;
import com.ecommerce.demo.service.EcommerceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private EcommerceService ecommerceService;

    @GetMapping
    public List<ProductoDTO> listar() {
        return ecommerceService.listarProductos();
    }

    @GetMapping("/{id}")
    public ProductoDTO obtenerPorId(@PathVariable Long id) {
        return ecommerceService.obtenerProductoPorId(id);
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ecommerceService.crearProducto(dto));
    }

    @PutMapping("/{id}")
    public ProductoDTO actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        return ecommerceService.actualizarProducto(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ecommerceService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoria}")
    public List<ProductoDTO> porCategoria(@PathVariable String categoria) {
        return ecommerceService.buscarProductosPorCategoriaNamed(categoria);
    }

    @GetMapping("/precio-max/{precioMax}")
    public List<ProductoDTO> porPrecioMax(@PathVariable BigDecimal precioMax) {
        return ecommerceService.buscarPorPrecioMaximo(precioMax);
    }

    @GetMapping("/buscar")
    public List<ProductoDTO> buscar(@RequestParam(defaultValue = "") String keyword,
                                    @RequestParam(defaultValue = "0") Integer stockMin) {
        return ecommerceService.buscarPorFiltroAvanzadoEM(keyword, stockMin);
    }
}