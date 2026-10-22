package com.ejemplo.tienda.repository;

import com.ejemplo.tienda.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Page<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(
            String nombre, Pageable pageable);
}