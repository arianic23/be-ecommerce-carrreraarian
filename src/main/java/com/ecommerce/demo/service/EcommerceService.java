package com.ecommerce.demo.service;

import com.ecommerce.demo.dto.ItemPedidoRequestDTO;
import com.ecommerce.demo.dto.PedidoRequestDTO;
import com.ecommerce.demo.dto.ProductoDTO;
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

@Service
public class EcommerceService {
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoCustomRepositoryImpl productoCustomRepository;

    public List<ProductoDTO> listarProductos() {
        return productoRepository.findAll().stream().map(this::aDTO).toList();
    }

    public ProductoDTO obtenerProductoPorId(Long id) {
        return aDTO(buscarProducto(id));
    }

    public ProductoDTO crearProducto(ProductoDTO dto) {
        Producto producto = new Producto();
        copiarDatos(dto, producto);
        return aDTO(productoRepository.save(producto));
    }

    public ProductoDTO actualizarProducto(Long id, ProductoDTO dto) {
        Producto producto = buscarProducto(id);
        copiarDatos(dto, producto);
        return aDTO(productoRepository.save(producto));
    }

    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    public List<ProductoDTO> buscarProductosPorCategoriaNamed(String categoria) {
        return productoRepository.findByCategoriaNamed(categoria).stream().map(this::aDTO).toList();
    }

    public List<ProductoDTO> buscarPorPrecioMaximo(BigDecimal precioMax) {
        return productoRepository.buscarPorPrecioMaximoJPQL(precioMax).stream().map(this::aDTO).toList();
    }

    public List<ProductoDTO> buscarPorFiltroAvanzadoEM(String keyword, Integer stockMin) {
        return productoCustomRepository.buscarPorFiltrosAvanzados(keyword, stockMin).stream().map(this::aDTO).toList();
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
    }

    private void copiarDatos(ProductoDTO dto, Producto producto) {
        producto.setNombre(dto.getNombre());
        producto.setCategoria(dto.getCategoria());
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setStock(dto.getStock());
        producto.setMarca(dto.getMarca());
        producto.setCodigoBarras(dto.getCodigoBarras());
        producto.setFechaIngreso(dto.getFechaIngreso());
    }

    private ProductoDTO aDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setCategoria(p.getCategoria());
        dto.setPrecioCompra(p.getPrecioCompra());
        dto.setPrecioVenta(p.getPrecioVenta());
        dto.setStock(p.getStock());
        dto.setMarca(p.getMarca());
        dto.setCodigoBarras(p.getCodigoBarras());
        dto.setFechaIngreso(p.getFechaIngreso());
        return dto;
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

            BigDecimal subtotal = producto.getPrecioVenta().multiply(BigDecimal.valueOf(item.getCantidad()));
            montoTotalAcumulado = montoTotalAcumulado.add(subtotal);

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta());
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