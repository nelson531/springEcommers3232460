package com.sena.springecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
public class Orden {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String numero;

	private LocalDate fechaCreacion;

	private Double total = 0.0;

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetalleOrden> detalles = new ArrayList<>();

	public Orden() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDate fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<DetalleOrden> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetalleOrden> detalles) {
		this.detalles = detalles;
	}

	// Método para asignar detalles a una orden
	public void addDetalle(DetalleOrden detalle) {
		detalle.setOrden(this);
		this.detalles.add(detalle);
	}

	// Calcular total
	public void calcularTotal() {
		this.total = detalles.stream().mapToDouble(DetalleOrden::getTotal).sum();
	}
}