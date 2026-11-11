package com.ecommerce.demo.controller;

import com.ecommerce.demo.dto.PedidoRequestDTO;
import com.ecommerce.demo.entity.Pedido;
import com.ecommerce.demo.service.EcommerceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private EcommerceService ecommerceService;

    @GetMapping
    public List<Pedido> listar() {
        return ecommerceService.listarPedidos();
    }

    @GetMapping("/{id}")
    public Pedido obtenerPorId(@PathVariable Long id) {
        return ecommerceService.obtenerPedidoPorId(id);
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ecommerceService.crearPedido(request));
    }
}