package com.sena.springecommerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.service.IUsuarioService;

@RestController
@RequestMapping("/apiusuario")
public class APIUsuarioController {

	@Autowired
	private IUsuarioService usuarioService;

	// Endpoint get obtener usuario
	@GetMapping("/list")
	public List<Usuario> getAllUsuario() {
		return usuarioService.findAll();

	}

	// Endpoint get para obtener un usuario por ID
	@GetMapping("/usuario/{id}")
	public ResponseEntity<Usuario> getUsuarioById(@PathVariable Integer id) {
		Optional<Usuario> usuario = usuarioService.get(id);
		return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// End POST crear un nuevo producto
	@PostMapping("/create")
	public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
		usuario.setRol("USER");
		Usuario savedUsuario = usuarioService.save(usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedUsuario);

	}

	// end PUT para actualizar un producto
	@PutMapping("/update/{id}")
	public ResponseEntity<Usuario> updateUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioDetails) {
		Optional<Usuario> usuario = usuarioService.get(id);
		if (!usuario.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Usuario existingUsuario = usuario.get();
		existingUsuario.setNombre(usuarioDetails.getNombre());
		existingUsuario.setTelefono(usuarioDetails.getTelefono());
		existingUsuario.setEmail(usuarioDetails.getEmail());
		existingUsuario.setDireccion(usuarioDetails.getDireccion());
		existingUsuario.setRol(usuarioDetails.getRol());
		existingUsuario.setPassword(usuarioDetails.getPassword());
		usuarioService.update(existingUsuario);
		return ResponseEntity.ok(existingUsuario);

	}

	// Endpoint DELETE para eliminar usuario
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteUsuario(@PathVariable Integer id) {
		Optional<Usuario> usuario = usuarioService.get(id);
		if (!usuario.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		usuarioService.delete(id);
		return ResponseEntity.ok().build();
	}
}
