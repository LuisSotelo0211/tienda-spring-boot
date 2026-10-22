package com.ejemplo.tienda;

import com.ejemplo.tienda.dto.Dtos.*;
import com.ejemplo.tienda.repository.*;
import com.ejemplo.tienda.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")

class PedidoServiceTest {
    @Autowired CatalogoService catalogo;
    @Autowired PedidoService servicio;
    @Autowired PedidoRepository pedidos;
    @Autowired ProductoRepository productos;
    @Autowired ClienteRepository clientes;
    @Autowired CategoriaRepository categorias;
    Long productoId;
    Long clienteId;
    @BeforeEach
    void preparar() {
        pedidos.deleteAll();
        productos.deleteAll();
        clientes.deleteAll();
        categorias.deleteAll();
        var c = catalogo.guardarCategoria(null, new CategoriaInput("Tecnologia"));
        var p = catalogo.guardarProducto(null, new ProductoInput(
                "Teclado", new BigDecimal("180.00"), 15, c.id()));
        productoId = p.id();
        clienteId = catalogo.guardarCliente(null, new ClienteInput(
                "Luis", "luis@example.com", "Lima")).id();
    }
    PedidoInput solicitud(int cantidad) {
        return new PedidoInput(clienteId,
                List.of(new ItemInput(productoId, cantidad)));
    }
    @Test
    void calculaTotalYDescuentaStock() {
        var pedido = servicio.crear(solicitud(2));
        assertEquals(0, new BigDecimal("360.00").compareTo(pedido.total()));
        assertEquals(13, catalogo.producto(productoId).stock());
        assertEquals(1, pedidos.count());
    }
    @Test
    void rechazaStockInsuficiente() {
        assertThrows(ResponseStatusException.class,
                () -> servicio.crear(solicitud(16)));
        assertEquals(15, catalogo.producto(productoId).stock());
        assertEquals(0, pedidos.count());
    }
    @Test
    void reviertePrimerItemSiElSegundoFalla() {
        var in = new PedidoInput(clienteId, List.of(
                new ItemInput(productoId, 2), new ItemInput(Long.MAX_VALUE, 1)));
        assertThrows(ResponseStatusException.class, () -> servicio.crear(in));
        assertEquals(15, catalogo.producto(productoId).stock());
        assertEquals(0, pedidos.count());
    }
    @Test
    void cancelarReponeUnaSolaVez() {
        var p = servicio.crear(solicitud(2));
        assertEquals("CANCELADO", servicio.cancelar(p.id()).estado());
        assertEquals(15, catalogo.producto(productoId).stock());
        assertThrows(ResponseStatusException.class, () -> servicio.cancelar(p.id()));
        assertEquals(15, catalogo.producto(productoId).stock());
    }
    @Test
    void rechazaProductoInactivo() {
        catalogo.desactivarProducto(productoId);
        assertThrows(ResponseStatusException.class,
                () -> servicio.crear(solicitud(1)));
        assertEquals(0, pedidos.count());
    }
    @Test
    void rechazaProductosRepetidosYRevierteStock() {
        var in = new PedidoInput(clienteId, List.of(
                new ItemInput(productoId, 2), new ItemInput(productoId, 1)));
        assertThrows(ResponseStatusException.class, () -> servicio.crear(in));
        assertEquals(15, catalogo.producto(productoId).stock());
        assertEquals(0, pedidos.count());
    }
    @Test
    void sumaSubtotalesDeDosProductos() {
        var categoria = catalogo.guardarCategoria(
                null,
                new CategoriaInput("Prueba multiples")
        );

        var productoA = catalogo.guardarProducto(
                null,
                new ProductoInput(
                        "Producto A",
                        new BigDecimal("100.00"),
                        10,
                        categoria.id()
                )
        );

        var productoB = catalogo.guardarProducto(
                null,
                new ProductoInput(
                        "Producto B",
                        new BigDecimal("50.00"),
                        10,
                        categoria.id()
                )
        );

        var entrada = new PedidoInput(
                clienteId,
                List.of(
                        new ItemInput(productoA.id(), 2),
                        new ItemInput(productoB.id(), 3)
                )
        );

        var pedido = servicio.crear(entrada);

        assertEquals(
                0,
                new BigDecimal("350.00").compareTo(pedido.total())
        );

        assertEquals(8, catalogo.producto(productoA.id()).stock());
        assertEquals(7, catalogo.producto(productoB.id()).stock());

        var sumaSubtotales = pedido.items().stream()
                .map(ItemView::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(0, sumaSubtotales.compareTo(pedido.total()));
    }
}