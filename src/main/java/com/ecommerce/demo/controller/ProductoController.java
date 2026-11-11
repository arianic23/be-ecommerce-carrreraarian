package com.ecommerce.demo.controller;

import com.ecommerce.demo.entity.Producto;
import com.ecommerce.demo.service.EcommerceService;
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
    public List<Producto> listar() {
        return ecommerceService.listarProductos();
    }

    @GetMapping("/{id}")
    public Producto obtenerPorId(@PathVariable Long id) {
        return ecommerceService.obtenerProductoPorId(id);
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        producto.setId(null); // fuerza INSERT
        return ResponseEntity.status(HttpStatus.CREATED).body(ecommerceService.guardarProducto(producto));
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        ecommerceService.obtenerProductoPorId(id); // lanza 404 si no existe
        producto.setId(id);
        return ecommerceService.guardarProducto(producto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ecommerceService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoria}")
    public List<Producto> porCategoria(@PathVariable String categoria) {
        return ecommerceService.buscarProductosPorCategoriaNamed(categoria);
    }

    @GetMapping("/precio-max/{precioMax}")
    public List<Producto> porPrecioMax(@PathVariable BigDecimal precioMax) {
        return ecommerceService.buscarPorPrecioMaximo(precioMax);
    }

    @GetMapping("/buscar")
    public List<Producto> buscar(@RequestParam(defaultValue = "") String keyword,
                                 @RequestParam(defaultValue = "0") Integer stockMin) {
        return ecommerceService.buscarPorFiltroAvanzadoEM(keyword, stockMin);
    }
}