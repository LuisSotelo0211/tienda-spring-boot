package com.ejemplo.tienda.controller;

import com.ejemplo.tienda.dto.Dtos.*;
import com.ejemplo.tienda.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor

public class PedidoController {
    private final PedidoService servicio;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoView crear(@Valid @RequestBody PedidoInput in) {
        return servicio.crear(in);
    }
    @GetMapping("/{id}")
    public PedidoView consultar(@PathVariable Long id) {
        return servicio.consultar(id);
    }
    @PatchMapping("/{id}/cancelacion")
    public PedidoView cancelar(@PathVariable Long id) {
        return servicio.cancelar(id);
    }
}