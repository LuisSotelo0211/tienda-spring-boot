package com.ejemplo.tienda.repository;

import com.ejemplo.tienda.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByEmailAndIdNot(String email, Long id);
}