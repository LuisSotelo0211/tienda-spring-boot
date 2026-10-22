package com.ejemplo.tienda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class Dtos {
    private Dtos() { }
    public record CategoriaInput(
            @NotBlank @Size(max = 80) String nombre) { }
    public record CategoriaView(Long id, String nombre) { }
    public record ProductoInput(
            @NotBlank @Size(max = 100) String nombre,
            @NotNull @DecimalMin("0.01")
            @Digits(integer = 10, fraction = 2) BigDecimal precio,
            @NotNull @Min(0) @Max(1000000) Integer stock,
            @NotNull @Positive Long categoriaId) { }
    public record ProductoView(
            Long id, String nombre, BigDecimal precio, int stock,
            boolean activo, Long categoriaId) { }
    public record ClienteInput(
            @NotBlank @Size(max = 100) String nombre,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(max = 200) String direccion) { }
    public record ClienteView(
            Long id, String nombre, String email, String direccion) { }
    public record ItemInput(
            @NotNull @Positive Long productoId,
            @NotNull @Min(1) @Max(1000) Integer cantidad) { }
    public record PedidoInput(
            @NotNull @Positive Long clienteId,
            @NotEmpty @Size(max = 50)
            List<@NotNull @Valid ItemInput> items) { }
    public record ItemView(
            Long productoId, int cantidad,
            BigDecimal precioUnitario, BigDecimal subtotal) { }
    public record PedidoView(
            Long id, Long clienteId, Instant fecha, String estado,
            BigDecimal total, List<ItemView> items) { }
    public record Pagina<T>(
            List<T> contenido, int pagina, int tamano,
            long totalElementos, int totalPaginas) { }
}