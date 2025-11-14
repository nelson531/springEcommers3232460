package com.sena.springecommerce.controller;

import com.sena.springecommerce.model.DetalleOrden;
import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Producto;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.service.IDetalleOrdenService;
import com.sena.springecommerce.service.IOrdenService;
import com.sena.springecommerce.service.IProductoService;
import com.sena.springecommerce.service.IUsuarioService;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

	// Obtener todas las órdenes
	@GetMapping("/list")
	public List<Orden> listarOrdenes() {
		return ordenService.findAll();
	}

	// Obtener una orden por ID
	@GetMapping("/orden/{id}")
	public Optional<Orden> obtenerOrden(@PathVariable Integer id) {
		return ordenService.findById(id);
	}

	@PostMapping("/create")
	public Orden crearOrden(@RequestBody Orden orden) {

	    // Fecha
	    orden.setFechacreacion(new java.sql.Date(System.currentTimeMillis()));

	    // Número de orden
	    orden.setNumero(ordenService.generarNumeroOrden());

	    double total = 0.0;

	    // Verificar usuario
	    Usuario usuario = usuarioService.findById(orden.getUsuario().getId())
	            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	    orden.setUsuario(usuario);

	    // Guardar orden (ID generado)
	    Orden nuevaOrden = ordenService.save(orden);

	    // Procesar detalles
	    for (DetalleOrden detalle : orden.getDetalle()) {

	        Producto producto = productoService.findById(detalle.getProducto().getId())
	                .orElseThrow(() -> new RuntimeException(
	                        "Producto no encontrado: ID " + detalle.getProducto().getId()));

	        // Verificar stock
	        if (producto.getCantidad() < detalle.getCantidad()) {
	            throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
	        }

	        // Poner precio del producto
	        detalle.setPrecio(producto.getPrecio());

	        // Subtotal
	        detalle.setTotal(detalle.getCantidad() * producto.getPrecio());
	        total += detalle.getTotal();

	        // Descontar stock
	        producto.setCantidad(producto.getCantidad() - detalle.getCantidad());
	        productoService.save(producto);

	        // Relacionar con la orden
	        detalle.setOrden(nuevaOrden);

	        detalleOrdenService.save(detalle);
	    }

	    // Guardar total correcto
	    nuevaOrden.setTotal(total);
	    ordenService.save(nuevaOrden);

	    return nuevaOrden;
	}
}