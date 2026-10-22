package com.ejemplo.tienda.service;

import com.ejemplo.tienda.dto.Dtos.*;
import com.ejemplo.tienda.entity.*;
import com.ejemplo.tienda.repository.*;
import static com.ejemplo.tienda.exception.Errores.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class PedidoService {
    private final PedidoRepository pedidos;
    private final ProductoRepository productos;
    private final ClienteRepository clientes;
    @Transactional
    public PedidoView crear(PedidoInput in) {
        Pedido pedido = new Pedido();
        pedido.setCliente(clientes.findById(in.clienteId())
                .orElseThrow(() -> noExiste("Cliente")));
        var vistos = new HashSet<Long>();
        BigDecimal total = BigDecimal.ZERO;
        for (ItemInput item : in.items()) {
            if (!vistos.add(item.productoId())) {
                throw conflicto("No repitas productos en el pedido");
            }
            Producto p = productos.findById(item.productoId())
                    .orElseThrow(() -> noExiste("Producto"));
            if (!p.isActivo()) {
                throw conflicto("El producto esta inactivo");
            }
            if (p.getStock() < item.cantidad()) {
                throw conflicto("Stock insuficiente: " + p.getNombre());
            }
            p.setStock(p.getStock() - item.cantidad());
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(p);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(p.getPrecio());
            pedido.getItems().add(detalle);
            total = total.add(
                    p.getPrecio().multiply(BigDecimal.valueOf(item.cantidad()))
            );
        }
        pedido.setTotal(total);
        pedidos.saveAndFlush(pedido);
        return vista(pedido);
    }
    public PedidoView consultar(Long id) {
        return vista(pedidos.findById(id)
                .orElseThrow(() -> noExiste("Pedido")));
    }
    @Transactional
    public PedidoView cancelar(Long id) {
        Pedido pedido = pedidos.findById(id)
                .orElseThrow(() -> noExiste("Pedido"));
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw conflicto("El pedido ya fue cancelado");
        }
        for (DetallePedido item : pedido.getItems()) {
            Producto p = item.getProducto();
            p.setStock(Math.addExact(p.getStock(), item.getCantidad()));
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidos.flush();
        return vista(pedido);
    }
    private PedidoView vista(Pedido p) {
        var items = p.getItems().stream().map(i -> new ItemView(
                i.getProducto().getId(), i.getCantidad(), i.getPrecioUnitario(),
                i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad()))
        )).toList();
        return new PedidoView(p.getId(), p.getCliente().getId(), p.getFecha(),
                p.getEstado().name(), p.getTotal(), items);
    }
}