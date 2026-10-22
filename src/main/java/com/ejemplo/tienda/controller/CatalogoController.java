package com.ejemplo.tienda.controller;

import com.ejemplo.tienda.dto.Dtos.*;
import com.ejemplo.tienda.service.CatalogoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class CatalogoController {
    private final CatalogoService servicio;
    @GetMapping("/categorias")
    public List<CategoriaView> categorias() {
        return servicio.categorias();
    }
    @PostMapping("/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaView crearCategoria(@Valid @RequestBody CategoriaInput in) {
        return servicio.guardarCategoria(null, in);
    }
    @PutMapping("/categorias/{id}")
    public CategoriaView editarCategoria(@PathVariable Long id,
                                         @Valid @RequestBody CategoriaInput in) {
        return servicio.guardarCategoria(id, in);
    }
    @DeleteMapping("/categorias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCategoria(@PathVariable Long id) {
        servicio.eliminarCategoria(id);
    }
    @GetMapping("/productos")
    public Pagina<ProductoView> productos(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int tamano) {
        return servicio.productos(nombre, pagina, tamano);
    }
    @GetMapping("/productos/{id}")
    public ProductoView producto(@PathVariable Long id) {
        return servicio.producto(id);
    }
    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoView crearProducto(@Valid @RequestBody ProductoInput in) {
        return servicio.guardarProducto(null, in);
    }
    @PutMapping("/productos/{id}")
    public ProductoView editarProducto(@PathVariable Long id,
                                       @Valid @RequestBody ProductoInput in) {
        return servicio.guardarProducto(id, in);
    }
    @DeleteMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        servicio.desactivarProducto(id);
    }
    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteView crearCliente(@Valid @RequestBody ClienteInput in) {
        return servicio.guardarCliente(null, in);
    }
    @GetMapping("/clientes/{id}")
    public ClienteView cliente(@PathVariable Long id) {
        return servicio.cliente(id);
    }
    @PutMapping("/clientes/{id}")
    public ClienteView editarCliente(@PathVariable Long id,
                                     @Valid @RequestBody ClienteInput in) {
        return servicio.guardarCliente(id, in);
    }
}