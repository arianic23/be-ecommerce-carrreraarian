package com.ecommerce.demo.repository;

import com.ecommerce.demo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
// 1. JPQL con parámetro nombrado (Saniamiento automático contra SQL Injection)
@Query("SELECT p FROM Producto p WHERE p.precioVenta <= :precioMax ORDER BY p.precioVenta DESC")
List<Producto> buscarPorPrecioMaximoJPQL(@Param("precioMax") BigDecimal precioMax);
// 2. Ejecución de Named Query declarada en la Entidad Producto
List<Producto> findByCategoriaNamed(@Param("categoria") String categoria);
}