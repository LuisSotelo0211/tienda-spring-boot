package com.ejemplo.tienda.service;

import com.ejemplo.tienda.dto.Dtos.*;
import com.ejemplo.tienda.entity.*;
import com.ejemplo.tienda.repository.*;
import static com.ejemplo.tienda.exception.Errores.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class CatalogoService {
    private final CategoriaRepository categorias;
    private final ProductoRepository productos;
    private final ClienteRepository clientes;
    public List<CategoriaView> categorias() {
        return categorias.findAll(Sort.by("id")).stream()
                .map(c -> new CategoriaView(c.getId(), c.getNombre()))
                .toList();
    }
    @Transactional
    public CategoriaView guardarCategoria(Long id, CategoriaInput in) {
        Categoria c = id == null ? new Categoria() : categorias.findById(id)
                .orElseThrow(() -> noExiste("Categoria"));
        c.setNombre(in.nombre().strip());
        categorias.saveAndFlush(c);
        return new CategoriaView(c.getId(), c.getNombre());
    }
    @Transactional
    public void eliminarCategoria(Long id) {
        Categoria c = categorias.findById(id)
                .orElseThrow(() -> noExiste("Categoria"));
        categorias.delete(c);
        categorias.flush();
    }
    public Pagina<ProductoView> productos(String nombre, int pagina, int tamano) {
        var page = productos.findByNombreContainingIgnoreCaseAndActivoTrue(
                nombre, PageRequest.of(pagina, tamano, Sort.by("id")));
        return new Pagina<>(page.getContent().stream().map(this::vista).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages());
    }
    public ProductoView producto(Long id) {
        return vista(productos.findById(id)
                .orElseThrow(() -> noExiste("Producto")));
    }
    @Transactional
    public ProductoView guardarProducto(Long id, ProductoInput in) {
        Producto p = id == null ? new Producto() : productos.findById(id)
                .orElseThrow(() -> noExiste("Producto"));
        p.setCategoria(categorias.findById(in.categoriaId())
                .orElseThrow(() -> noExiste("Categoria")));
        p.setNombre(in.nombre().strip());
        p.setPrecio(in.precio());
        p.setStock(in.stock());
        productos.saveAndFlush(p);
        return vista(p);
    }
    @Transactional
    public void desactivarProducto(Long id) {
        Producto p = productos.findById(id)
                .orElseThrow(() -> noExiste("Producto"));
        p.setActivo(false);
    }
    public ClienteView cliente(Long id) {
        return vista(clientes.findById(id)
                .orElseThrow(() -> noExiste("Cliente")));
    }
    @Transactional
    public ClienteView guardarCliente(Long id, ClienteInput in) {
        Cliente c = id == null ? new Cliente() : clientes.findById(id)
                .orElseThrow(() -> noExiste("Cliente"));
        String email = in.email().strip().toLowerCase(Locale.ROOT);
        if (clientes.existsByEmailAndIdNot(email, id == null ? -1L : id)) {
            throw conflicto("El correo ya esta registrado");
        }
        c.setNombre(in.nombre().strip());
        c.setEmail(email);
        c.setDireccion(in.direccion().strip());
        clientes.saveAndFlush(c);
        return vista(c);
    }
    private ProductoView vista(Producto p) {
        return new ProductoView(p.getId(), p.getNombre(), p.getPrecio(),
                p.getStock(), p.isActivo(), p.getCategoria().getId());
    }
    private ClienteView vista(Cliente c) {
        return new ClienteView(c.getId(), c.getNombre(), c.getEmail(),
                c.getDireccion());
    }
}