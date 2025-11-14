package com.sena.springecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.repository.IOrdenRepository;

@Service
public class OrdenServiceImplement implements IOrdenService {
	
	@Autowired
	private IOrdenRepository ordenRepository;

	@Override
	public String generarNumeroOrden() {

	    List<Orden> ordenes = ordenRepository.findAll(); // ← CAMBIO CLAVE
	    List<Integer> numeros = new ArrayList<>();

	    // Filtrar solo los números puros, ignorar "ORD-xxxxx"
	    ordenes.forEach(o -> {
	        if (o.getNumero() != null && o.getNumero().matches("\\d+")) {
	            numeros.add(Integer.parseInt(o.getNumero()));
	        }
	    });

	    int numero = numeros.isEmpty()
	    		? 1
	    		: numeros.stream().max(Integer::compare).get() + 1;

	    return String.format("%012d", numero); // ← Mucho más limpio
	}

	@Override
	public Orden save(Orden orden) {
		return ordenRepository.save(orden);
	}

	@Override
	public List<Orden> findAll() {
		return ordenRepository.findAll();
	}

	@Override
	public List<Orden> findAllUsuario(Usuario usuario) {
		return ordenRepository.findByUsuario(usuario);
	}

	@Override
	public Optional<Orden> findById(Integer id) {
		return ordenRepository.findById(id);
	}

	@Override
	public Optional<Orden> get(Integer id) {
		return ordenRepository.findById(id);
	}

	@Override
	public Orden findTopByOrderByIdDesc() {
		return ordenRepository.findTopByOrderByIdDesc();
	}
}