package com.ecommerce.demo.service;

import com.ecommerce.demo.dto.ItemPedidoRequestDTO;
import com.ecommerce.demo.dto.PedidoRequestDTO;
import com.ecommerce.demo.entity.DetallePedido;
import com.ecommerce.demo.entity.Pedido;
import com.ecommerce.demo.entity.Producto;
import com.ecommerce.demo.exception.BadRequestException;
import com.ecommerce.demo.exception.ResourceNotFoundException;
import com.ecommerce.demo.repository.PedidoRepository;
import com.ecommerce.demo.repository.ProductoCustomRepositoryImpl;
import com.ecommerce.demo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public class EcommerceService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoCustomRepositoryImpl productoCustomRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    public List<Producto> buscarProductosPorCategoriaNamed(String categoria) {
        return productoRepository.findByCategoriaNamed(categoria);
    }

    public List<Producto> buscarPorPrecioMaximo(BigDecimal precioMax) {
        return productoRepository.buscarPorPrecioMaximoJPQL(precioMax);
    }

    public List<Producto> buscarPorFiltroAvanzadoEM(String keyword, Integer stockMin) {
        return productoCustomRepository.buscarPorFiltrosAvanzados(keyword, stockMin);
    }

    @Transactional
    public Pedido crearPedido(PedidoRequestDTO request) {
        Pedido pedido = new Pedido();
        pedido.setCliente(request.getCliente());

        BigDecimal montoTotalAcumulado = BigDecimal.ZERO;

        for (ItemPedidoRequestDTO item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new BadRequestException("Stock insuficiente para el producto: " + producto.getNombre() 
                        + ". Stock disponible: " + producto.getStock() + ", solicitado: " + item.getCantidad());
            }

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            montoTotalAcumulado = montoTotalAcumulado.add(subtotal);

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);

            pedido.addDetalle(detalle);
        }

        pedido.setMontoTotal(montoTotalAcumulado);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el ID: " + id));
    }
}