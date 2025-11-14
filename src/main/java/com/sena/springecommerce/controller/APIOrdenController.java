package com.sena.springecommerce.controller;

import com.sena.springecommerce.model.DetalleOrden;
import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Producto;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.service.IDetalleOrdenService;
import com.sena.springecommerce.service.IOrdenService;
import com.sena.springecommerce.service.IProductoService;
import com.sena.springecommerce.service.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/apiordenes")
@CrossOrigin(origins = "*")
public class APIOrdenController {

    @Autowired
    private IOrdenService ordenService;

    @Autowired
    private IDetalleOrdenService detalleOrdenService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IProductoService productoService;

    @PostMapping("/create")
    public Orden crearOrden(@RequestBody Orden orden) {

        // Fecha actual
        orden.setFechaCreacion(LocalDate.now());

        // Numero orden generado
        orden.setNumero(ordenService.generarNumeroOrden());

        // Verificar usuario
        Usuario usuario = usuarioService.findById(orden.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        orden.setUsuario(usuario);

        // Si no hay detalles
        if (orden.getDetalles() == null || orden.getDetalles().isEmpty()) {
            orden.setTotal(0.0);
            return ordenService.save(orden);
        }

        // Guardar orden inicial
        Orden nuevaOrden = ordenService.save(orden);

        double total = 0.0;

        for (DetalleOrden detalle : orden.getDetalles()) {

            Producto producto = productoService.findById(detalle.getProducto().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Producto no encontrado: ID " + detalle.getProducto().getId()));

            if (producto.getCantidad() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            detalle.setProducto(producto);
            detalle.setNombre(producto.getNombre());
            detalle.setPrecio(producto.getPrecio());

            detalle.setTotal(detalle.getCantidad() * producto.getPrecio());
            total += detalle.getTotal();

            producto.setCantidad(producto.getCantidad() - detalle.getCantidad());
            productoService.save(producto);

            detalle.setOrden(nuevaOrden);

            detalleOrdenService.save(detalle);
        }

        nuevaOrden.setTotal(total);
        ordenService.save(nuevaOrden);

        return nuevaOrden;
    }
}